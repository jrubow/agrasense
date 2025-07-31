import React from "react";
import { Link } from "react-router-dom";
import { useArticles } from "./ArticlesContext";
import "../css/pages/newsroom.css";

/**
 * Newsroom component - Displays a list of published articles
 * Shows article summaries, metadata, and links to full articles
 */
export default function Newsroom() {
  // Get articles data, dark mode state, and loading status from context
  const { articles, dark, loading } = useArticles();

  return (
    <div className={`newsroom-container${dark ? " dark-bg" : ""}`}>
      {/* Page header */}
      <h1 className="newsroom-header">AgraSense Newsroom</h1>
      <h2 style={{ marginBottom: 20, marginTop: 25 }}>Latest Updates</h2>
      
      {/* Show loading indicator while fetching articles */}
      {loading && <div>Loading...</div>}
      
      <ul className="newsroom-list">
        {articles && articles.length > 0 ? (
          articles
            // Filter to show only published articles
            .filter((a) => a.is_published)
            .map((article) => (
              <li key={article.id} className="newsroom-article">
                {/* Article title - clickable link to full article */}
                <Link
                  className="newsroom-title"
                  to={`/article/${article.slug}`}
                  style={{
                    textDecoration: "none",
                    color: dark ? "#c4ffd0" : "#1d7933", // Dynamic color based on dark mode
                    fontWeight: 700,
                    fontSize: "1.2em",
                  }}
                >
                  {article.title}
                </Link>
                
                {/* Article summary/excerpt */}
                <div className="newsroom-summary">{article.summary}</div>
                
                {/* Article metadata - author, date, and optional category */}
                <div className="newsroom-date">
                  <strong>By:</strong> {article.author} | <strong>Date:</strong> {article.date}
                  {article.category && <> | <strong>Category:</strong> {article.category}</>}
                </div>
                
                {/* Article tags if available */}
                {article.tags && article.tags.length > 0 && (
                  <div style={{ marginTop: 5 }}>
                    {article.tags.map((t) => (
                      <span className="tag" key={t}>
                        {t}
                      </span>
                    ))}
                  </div>
                )}
                
                {/* Article cover image if available */}
                {article.image && (
                  <img
                    src={article.image}
                    alt="cover"
                    style={{
                      marginTop: 10,
                      maxWidth: 240,
                      maxHeight: 120,
                      borderRadius: 8,
                      boxShadow: "0 1px 10px #0002",
                    }}
                  />
                )}
              </li>
            ))
        ) : (
          // Empty state when no articles are published
          <div style={{ margin: "2.2rem 0", textAlign: "center", color: "#888" }}>
            No articles published yet.
          </div>
        )}
      </ul>
    </div>
  );
}
