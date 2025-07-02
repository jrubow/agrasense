import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { useArticles } from "./ArticlesContext";
import ReactMarkdown from "react-markdown";
import "../css/pages/newsroom.css";

export default function ArticlePage() {
  const { slug } = useParams();
  const { getArticleBySlug, dark } = useArticles();
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getArticleBySlug(slug)
      .then(setArticle)
      .catch(() => setArticle(null))
      .finally(() => setLoading(false));
    // eslint-disable-next-line
  }, [slug]);

  if (loading) return <div style={{ padding: 40 }}>Loading...</div>;
  if (!article) {
    return (
      <div style={{ padding: 40 }}>
        <h1>Article Not Found</h1>
        <Link to="/newsroom">&larr; Back to Newsroom</Link>
      </div>
    );
  }

  return (
    <div className={`article-container${dark ? " dark-bg" : ""}`}>
      {article.image && (
        <img
          src={article.image}
          alt="cover"
          style={{
            width: "100%",
            maxHeight: 280,
            objectFit: "cover",
            borderRadius: 12,
            marginBottom: 18,
          }}
        />
      )}
      <h1 className="article-title">{article.title}</h1>
      <div>
        <strong>By:</strong> {article.author} | <strong>Date:</strong> {article.date}
      </div>
      <div className="article-summary">
        <strong>Summary:</strong> {article.summary}
      </div>
      <div className="article-content" style={{ marginTop: 20 }}>
        <ReactMarkdown>{article.content}</ReactMarkdown>
      </div>
      <Link to="/newsroom" className="article-back-link">
        &larr; Back to Newsroom
      </Link>
    </div>
  );
}
