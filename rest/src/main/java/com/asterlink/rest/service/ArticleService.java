package com.asterlink.rest.service;

import org.springframework.stereotype.Service;
import com.asterlink.rest.model.Article;
import java.util.Optional;
import java.util.List;

/**
 * Service interface for Articles
 * @author Josh Rubow (jrubow)
 */

@Service
public interface ArticleService {
    public List<Article> getAllArticles();
    public Optional<Article> getArticleById(int id);
    public Optional<Article> getArticleBySlug(String Slug);
    public Article createArticle(Article article);
    public Article editArticle(Article article);
    public boolean deleteArticleBySlug(String slug);
    public boolean deleteArticleById(String slug);
}
