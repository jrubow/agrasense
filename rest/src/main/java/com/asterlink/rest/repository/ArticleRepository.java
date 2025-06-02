package com.asterlink.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.asterlink.rest.model.Article;
import java.util.Optional;


public interface ArticleRepository extends JpaRepository<Article, Integer> {
    @Query("SELECT a FROM Article a WHERE a.slug = :slug")
    Optional<Article> findBySlug(@Param("slug") String slug);
}
