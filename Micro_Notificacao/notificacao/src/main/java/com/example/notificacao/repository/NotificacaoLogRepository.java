package com.example.notificacao.repository;

import com.example.notificacao.model.NotificacaoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface NotificacaoLogRepository extends JpaRepository<NotificacaoLog, Long> {
}