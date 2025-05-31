package com.asterlink.rest.model;

import com.asterlink.rest.converter.StringListToJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Articles Class
 * For use with newsroom page and articles table.
 * @author Josh Rubow (jrubow)
 */

@Entity
@Table(name="articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="slug", unique = true, nullable = false)
    private String slug;

    @Column(name="summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name="content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name="author_id")
    private Integer authorId;

    @Column(name="published_at", nullable = false)
    private LocalDateTime publishedAt;

    @Column(name="updated_at")
    private LocalDateTime updatedTimestamp;

    @Column(name="is_published", nullable = false)
    private boolean isPublished;

    @Column(name="category")
    private String category;

    @Column(name="tags", columnDefinition = "TEXT")
    @Convert(converter = StringListToJsonConverter.class)
    private List<String> tags;

    @Column(name="views_count")
    private int viewsCount;

    public Article() {
    }

    public Article(String title, String slug, String summary, String content, Integer authorId,
                   LocalDateTime publishedAt, boolean isPublished, String category, List<String> tags) {
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.content = content;
        this.authorId = authorId;
        this.publishedAt = publishedAt;
        this.isPublished = isPublished;
        this.category = category;
        this.tags = tags;
        this.viewsCount = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    @Override
    public String toString() {
        return "Article{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", slug='" + slug + '\'' +
               ", summary='" + summary + '\'' +
               ", publishedAt=" + publishedAt +
               ", isPublished=" + isPublished +
               '}';
    }
}