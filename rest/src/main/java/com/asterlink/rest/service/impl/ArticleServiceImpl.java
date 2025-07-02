package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.Article;
import com.asterlink.rest.service.ArticleService;
import com.asterlink.rest.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Articles
 * @author Josh Rubow (jrubow)
 */

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> getArticleById(int id) {
        return articleRepository.findById(id);
    }

    @Override
    public Optional<Article> getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    @Override
    @Transactional
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public Article editArticle(Article article) {
        Optional<Article> existingArticleOptional = articleRepository.findById(article.getId());
        if (existingArticleOptional.isPresent()) {
            Article existingArticle = existingArticleOptional.get();
            existingArticle.setTitle(article.getTitle());
            existingArticle.setContent(article.getContent());
            existingArticle.setSlug(article.getSlug());
            existingArticle.setSummary(article.getSummary());
            existingArticle.setCategory(article.getCategory());
            existingArticle.setTags(article.getTags());
            existingArticle.setViewsCount(article.getViewsCount());
            existingArticle.setAuthorId(article.getAuthorId());
            existingArticle.setPublished(article.isPublished());
            existingArticle.setPublishedTimestamp(article.getPublishedTimestamp());
            existingArticle.setUpdatedTimestamp(article.getUpdatedTimestamp());

            return articleRepository.save(existingArticle);
        } else {
            throw new RuntimeException("Article not found with ID: " + article.getId());
        }
    }


    @Override
    @Transactional
    public boolean deleteArticleBySlug(String slug) {
        Optional<Article> articleToDelete = articleRepository.findBySlug(slug);
        if (articleToDelete.isPresent()) {
            articleRepository.delete(articleToDelete.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteArticleById(String idString) {
        try {
            int id = Integer.parseInt(idString);
            Optional<Article> articleToDelete = articleRepository.findById(id);
            if (articleToDelete.isPresent()) {
                articleRepository.delete(articleToDelete.get());
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Invalid ID format: " + idString);
            return false;
        }
    }
}

