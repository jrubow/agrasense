// src/pages/ArticlePage.js
import React from "react";
import { useParams, Link } from "react-router-dom";
import ReactMarkdown from "react-markdown";
import articles from "./articles.js";
import '../css/pages/newsroom.css';


const ArticlePage = () => {
  const { slug } = useParams();
  const article = articles.find((a) => a.slug === slug);

  if (!article) {
    return (
      <div style={{ maxWidth: 600, margin: "2rem auto" }}>
        <h2>Article Not Found</h2>
        <Link to="/">← Back to Newsroom</Link>
      </div>
    );
  }

  return (
    <div className="article-container">
  <Link to="/" className="article-back-link">← Back to Newsroom</Link>
  <h1 className="article-title">{article.title}</h1>
  <div className="article-summary">{article.summary}</div>
  <div className="article-content">
    <ReactMarkdown>{article.content}</ReactMarkdown>
  </div>
</div>
  );
};

export default ArticlePage;
