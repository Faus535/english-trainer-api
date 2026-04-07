package com.faus535.englishtrainer.article.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaArticleQuestionAnswerRepository extends JpaRepository<ArticleQuestionAnswerEntity, UUID> {

    Optional<ArticleQuestionAnswerEntity> findByArticleQuestionId(UUID articleQuestionId);
}
