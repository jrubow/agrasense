package com.asterlink.rest.controller;

import com.asterlink.rest.model.Article;
import com.asterlink.rest.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Controller for Articles
 * TODO: protect with JWT tokens once admins are implemented.
 * @author Josh Rubow (jrubow)
 */

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private static final String ADMIN_PASSWORD = "AsterlinktoANDA_password1234";

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    private boolean isValidAdminPassword(String password) {
        return ADMIN_PASSWORD.equals(password);
    }

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable int id) {
        Optional<Article> article = articleService.getArticleById(id);
        return article.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Article> getArticleBySlug(@PathVariable String slug) {
        Optional<Article> article = articleService.getArticleBySlug(slug);
        return article.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(
            @RequestBody Article article,
            @RequestHeader("X-Admin-Password") String password) {

        if (!isValidAdminPassword(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Set the publishedTimestamp if it's missing
        if (article.getPublishedTimestamp() == null) {
            article.setPublishedTimestamp(LocalDateTime.now());
        }

        Article createdArticle = articleService.createArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> editArticle(
            @PathVariable int id,
            @RequestBody Article article,
            @RequestHeader("X-Admin-Password") String password) {

        if (!isValidAdminPassword(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (article.getId() != id) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Article updatedArticle = articleService.editArticle(article);
            return ResponseEntity.ok(updatedArticle);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/slug/{slug}")
    public ResponseEntity<Void> deleteArticleBySlug(
            @PathVariable String slug,
            @RequestHeader("X-Admin-Password") String password) {

        if (!isValidAdminPassword(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean deleted = articleService.deleteArticleBySlug(slug);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticleById(
            @PathVariable String id,
            @RequestHeader("X-Admin-Password") String password) {

        if (!isValidAdminPassword(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean deleted = articleService.deleteArticleById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}