package com.example.jpa.mail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailTemplateRepository extends JpaRepository<MailTemplate, Long> {

    Optional<MailTemplate> findByTemplateId(String templateId);
}
