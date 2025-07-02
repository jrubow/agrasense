import React from "react";
import { Link } from "react-router-dom";
import { useArticles } from "./ArticlesContext";
import "../css/pages/newsroom.css";

export default function Newsroom() {
  const { articles, dark, loading } = useArticles();

  return (
    <div className={`newsroom-container${dark ? " dark-bg" : ""}`}>
      <h1 className="newsroom-header">AgraSense Newsroom</h1>
      <h2 style={{ marginBottom: 20, marginTop: 25 }}>Latest Updates</h2>
      {loading && <div>Loading...</div>}
      <ul className="newsroom-list">
        {articles && articles.length > 0 ? (
          articles
            .filter((a) => a.is_published)
            .map((article) => (
              <li key={article.id} className="newsroom-article">
                <Link
                  className="newsroom-title"
                  to={`/article/${article.slug}`}
                  style={{
                    textDecoration: "none",
                    color: dark ? "#c4ffd0" : "#1d7933",
                    fontWeight: 700,
                    fontSize: "1.2em",
                  }}
                >
                  {article.title}
                </Link>
                <span className="badge-published" style={{ marginLeft: 10 }}>
                  Published
                </span>
                <div className="newsroom-summary">{article.summary}</div>
                <div className="newsroom-date">
                  <strong>By:</strong> {article.author} | <strong>Date:</strong> {article.date}
                  {article.category && <> | <strong>Category:</strong> {article.category}</>}
                </div>
                {article.tags && article.tags.length > 0 && (
                  <div style={{ marginTop: 5 }}>
                    {article.tags.map((t) => (
                      <span className="tag" key={t}>
                        {t}
                      </span>
                    ))}
                  </div>
                )}
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
          <div style={{ margin: "2.2rem 0", textAlign: "center", color: "#888" }}>
            No articles published yet.
          </div>
        )}
      </ul>
      <div style={{ textAlign: "right", marginTop: 18 }}>
        <Link to="/admin/newsroom" className="admin-btn">
          Admin &rarr;
        </Link>
      </div>
    </div>
  );
}
