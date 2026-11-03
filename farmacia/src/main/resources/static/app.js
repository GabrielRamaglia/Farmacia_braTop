const itemsBody = document.querySelector("#items-body");
const productCount = document.querySelector("#product-count");
const customerSelect = document.querySelector("#customer");
const notice = document.querySelector("#notice");
let products = [];
let customers = [];

document.querySelector("#today").textContent = new Intl.DateTimeFormat("pt-BR", {
  dateStyle: "long"
}).format(new Date());

function showNotice(message, type) {
  notice.textContent = message;
  notice.className = `notice ${type}`;
  notice.hidden = false;
  notice.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

function getErrorMessage(body, fallback) {
  if (typeof body === "string" && body.trim()) return body;
  if (body && typeof body === "object") return body.detail || body.message || body.error || fallback;
  return fallback;
}

async function request(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: { "Content-Type": "application/json", ...options.headers }
  });
  const text = await response.text();
  let body = text;
  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    body = text;
  }
  if (!response.ok) {
    throw new Error(getErrorMessage(body, `Não foi possível concluir a operação (${response.status}).`));
  }
  return body;
}

async function loadProducts() {
  products = await request("/produtos");
  const availableProducts = products.filter((product) => product.quantidadeEstoque > 0);
  productCount.textContent = availableProducts.length;
  document.querySelector("#empty-products").hidden = availableProducts.length > 0;
  document.querySelector("#empty-products-title").textContent = products.length > 0
    ? "Sem produtos em estoque"
    : "Nenhum produto cadastrado";
  document.querySelector("#empty-products-description").textContent = products.length > 0
    ? "Reponha o estoque de um produto para registrar uma venda."
    : "Cadastre o primeiro produto para começar a registrar vendas.";
  document.querySelector("#add-item").disabled = availableProducts.length === 0;
  itemsBody.querySelectorAll("tr").forEach((row) => {
    const select = row.querySelector("select");
    const selectedProductId = select.value;
    select.replaceChildren(new Option("Selecione um produto", ""));
    availableProducts.forEach((product) => {
      select.add(new Option(`${product.nome} (${product.tipoProduto})`, product.id));
    });
    select.value = selectedProductId;
    select.dispatchEvent(new Event("change"));
  });
  if (availableProducts.length > 0 && itemsBody.children.length === 0) addItemRow();

  const restockSelect = document.querySelector('#restock-form select[name="produtoId"]');
  restockSelect.replaceChildren(new Option("Selecione um produto", ""));
  products.forEach((product) => {
    restockSelect.add(new Option(`${product.nome} — estoque: ${product.quantidadeEstoque}`, product.id));
  });
}

async function loadCustomers() {
  customers = await request("/clientes");
  customerSelect.replaceChildren(new Option("Venda sem cliente cadastrado", ""));
  customers.forEach((customer) => {
    customerSelect.add(new Option(`${customer.nome} — ${customer.cpf}`, customer.id));
  });
  updateCustomerSummary();
}

function addItemRow() {
  if (products.length === 0) return;
  const row = document.createElement("tr");
  const productCell = document.createElement("td");
  const productSelect = document.createElement("select");
  productSelect.required = true;
  productSelect.setAttribute("aria-label", "Produto");
  productSelect.add(new Option("Selecione um produto", ""));
  products.filter((product) => product.quantidadeEstoque > 0).forEach((product) => {
    productSelect.add(new Option(`${product.nome} (${product.tipoProduto})`, product.id));
  });

  const priceCell = document.createElement("td");
  priceCell.className = "price-cell";
  priceCell.textContent = "—";

  const stockCell = document.createElement("td");
  stockCell.className = "price-cell";
  stockCell.textContent = "—";

  const quantityCell = document.createElement("td");
  const quantityInput = document.createElement("input");
  quantityInput.type = "number";
  quantityInput.min = "1";
  quantityInput.step = "1";
  quantityInput.value = "1";
  quantityInput.required = true;
  quantityInput.setAttribute("aria-label", "Quantidade");
  quantityCell.append(quantityInput);

  const removeCell = document.createElement("td");
  const removeButton = document.createElement("button");
  removeButton.type = "button";
  removeButton.className = "remove-item";
  removeButton.setAttribute("aria-label", "Remover produto do pedido");
  removeButton.textContent = "×";
  removeButton.addEventListener("click", () => {
    row.remove();
    updateSummary();
  });
  removeCell.append(removeButton);

  productSelect.addEventListener("change", () => {
    const product = products.find((entry) => String(entry.id) === productSelect.value);
    priceCell.textContent = product
      ? Number(product.precoBase).toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
      : "—";
    stockCell.textContent = product ? product.quantidadeEstoque : "—";
    quantityInput.max = product ? product.quantidadeEstoque : "";
    quantityInput.value = "1";
    updateSummary();
  });
  quantityInput.addEventListener("input", updateSummary);

  productCell.append(productSelect);
  row.append(productCell, priceCell, stockCell, quantityCell, removeCell);
  itemsBody.append(row);
  updateSummary();
}

function updateSummary() {
  let count = 0;
  itemsBody.querySelectorAll("tr").forEach((row) => {
    const quantity = Number(row.querySelector('input[type="number"]').value);
    if (row.querySelector("select").value && quantity > 0) count += quantity;
  });
  document.querySelector("#item-count").textContent = count;
}

function updateCustomerSummary() {
  const customer = customers.find((entry) => String(entry.id) === customerSelect.value);
  document.querySelector("#customer-summary").textContent = customer ? customer.nome : "Nenhum";
}

document.querySelector("#add-item").addEventListener("click", addItemRow);
customerSelect.addEventListener("change", updateCustomerSummary);

document.querySelector("#submit-sale").addEventListener("click", async (event) => {
  notice.hidden = true;
  const form = document.querySelector("#sale-form");
  if (!form.reportValidity()) return;

  const rows = [...itemsBody.querySelectorAll("tr")];
  if (rows.length === 0) {
    showNotice("Adicione pelo menos um produto ao pedido.", "error");
    return;
  }
  const items = rows.map((row) => ({
    produto: { id: Number(row.querySelector("select").value) },
    quantidade: Number(row.querySelector('input[type="number"]').value)
  }));
  if (items.some((item) => !item.produto.id || !Number.isInteger(item.quantidade) || item.quantidade < 1)) {
    showNotice("Confira o produto e a quantidade de cada item.", "error");
    return;
  }

  const selectedCustomer = customers.find((entry) => String(entry.id) === customerSelect.value);
  const sellerId = document.querySelector("#seller-id").value;
  const cpf = document.querySelector("#invoice-cpf").value.trim();
  const pedido = {
    canalVenda: document.querySelector("#channel").value,
    vendedorId: sellerId ? Number(sellerId) : null,
    cpfNaNota: cpf || null,
    cliente: selectedCustomer ? { id: selectedCustomer.id } : null,
    itens: items
  };

  const button = event.currentTarget;
  button.disabled = true;
  button.textContent = "Registrando...";
  try {
    const result = await request("/pedidos", {
      method: "POST",
      body: JSON.stringify(pedido)
    });
    showNotice(typeof result === "string" ? result : "Venda registrada com sucesso!", "success");
    itemsBody.replaceChildren();
    document.querySelector("#sale-form").reset();
    customerSelect.value = "";
    updateCustomerSummary();
    updateSummary();
    try {
      await loadProducts();
    } catch (refreshError) {
      showNotice(`Venda registrada, mas não foi possível atualizar o estoque exibido: ${refreshError.message}`, "error");
    }
  } catch (error) {
    showNotice(error.message, "error");
  } finally {
    button.disabled = false;
    button.innerHTML = 'Registrar venda <span aria-hidden="true">→</span>';
  }
});

document.querySelector("#product-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button");
  button.disabled = true;
  try {
    const data = Object.fromEntries(new FormData(form));
    data.precoBase = Number(data.precoBase);
    await request("/produtos", { method: "POST", body: JSON.stringify(data) });
    form.reset();
    await loadProducts();
    showNotice("Produto cadastrado. Se o estoque inicial for zero, use “Repor estoque” antes de vendê-lo.", "success");
  } catch (error) {
    showNotice(error.message, "error");
  } finally {
    button.disabled = false;
  }
});

document.querySelector("#customer-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button");
  button.disabled = true;
  try {
    const formData = new FormData(form);
    const data = {
      nome: formData.get("nome").trim(),
      cpf: formData.get("cpf").trim(),
      idoso: formData.has("idoso"),
      possuiConvenio: formData.has("possuiConvenio"),
      cadastrado: formData.has("cadastrado")
    };
    await request("/clientes", { method: "POST", body: JSON.stringify(data) });
    form.reset();
    await loadCustomers();
    showNotice("Cliente cadastrado e disponível para selecionar no pedido.", "success");
  } catch (error) {
    showNotice(error.message, "error");
  } finally {
    button.disabled = false;
  }
});

document.querySelector("#restock-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button");
  button.disabled = true;
  try {
    const formData = new FormData(form);
    const productId = formData.get("produtoId");
    const quantity = Number(formData.get("quantidade"));
    await request(`/produtos/${productId}/estoque`, {
      method: "PATCH",
      body: JSON.stringify({ quantidade: quantity })
    });
    form.reset();
    await loadProducts();
    showNotice("Estoque atualizado com sucesso.", "success");
  } catch (error) {
    showNotice(error.message, "error");
  } finally {
    button.disabled = false;
  }
});

Promise.all([loadProducts(), loadCustomers()]).catch((error) => {
  showNotice(`Não foi possível carregar os dados: ${error.message}`, "error");
});
