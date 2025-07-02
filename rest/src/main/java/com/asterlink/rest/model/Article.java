package com.asterlink.rest.model;

import com.asterlink.rest.converter.StringListToJsonConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "published_timestamp")
    @JsonProperty("published_timestamp")
    private LocalDateTime publishedTimestamp;

    @Column(name = "updated_timestamp")
    @JsonProperty("updated_timestamp")
    private LocalDateTime updatedTimestamp;

    @Column(name = "is_published", nullable = false)
    @JsonProperty("is_published")
    private boolean isPublished;

    @Column(name = "category")
    private String category;

    @Column(name = "tags", columnDefinition = "TEXT")
    @Convert(converter = StringListToJsonConverter.class)
    private List<String> tags;

    @Column(name = "views_count")
    private int viewsCount;

    public Article() {}

    public Article(String title, String slug, String summary, String content, Integer authorId,
                   LocalDateTime publishedTimestamp, boolean isPublished, String category, List<String> tags) {
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.content = content;
        this.authorId = authorId;
        this.publishedTimestamp = publishedTimestamp;
        this.isPublished = isPublished;
        this.category = category;
        this.tags = tags;
        this.viewsCount = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getAuthorId() { return authorId; }
    public void setAuthorId(Integer authorId) { this.authorId = authorId; }

    @JsonProperty("published_timestamp")
    public LocalDateTime getPublishedTimestamp() { return publishedTimestamp; }
    @JsonProperty("published_timestamp")
    public void setPublishedTimestamp(LocalDateTime publishedTimestamp) { this.publishedTimestamp = publishedTimestamp; }

    @JsonProperty("updated_timestamp")
    public LocalDateTime getUpdatedTimestamp() { return updatedTimestamp; }
    @JsonProperty("updated_timestamp")
    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) { this.updatedTimestamp = updatedTimestamp; }

    @JsonProperty("is_published")
    public boolean isPublished() { return isPublished; }
    @JsonProperty("is_published")
    public void setPublished(boolean published) {
        isPublished = published;
        System.out.println("setPublished called with: " + published);
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public int getViewsCount() { return viewsCount; }
    public void setViewsCount(int viewsCount) { this.viewsCount = viewsCount; }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", summary='" + summary + '\'' +
                ", publishedTimestamp=" + publishedTimestamp +
                ", isPublished=" + isPublished +
                '}';
    }
}
