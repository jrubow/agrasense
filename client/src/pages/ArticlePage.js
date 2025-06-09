import React from "react";
import { useParams, Link } from "react-router-dom";
import articles from "../pages/articles"; 

export default function ArticlePage() {
  const { slug } = useParams();
  const article = articles.find(a => a.slug === slug);

  if (!article) {
    return (
      <div>
        <h1>Article Not Found</h1>
        <Link to="/admin/newsroom">&larr; Back to Newsroom</Link>
      </div>
    );
  }

  return (
    <div>
      <h1>{article.title}</h1>
      <div><strong>By:</strong> {article.author}</div>
      <div><strong>Summary:</strong> {article.summary}</div>
      <div style={{ marginTop: 20 }}>{article.content}</div>
      <Link to="/admin/newsroom">&larr; Back to Newsroom</Link>
    </div>
  );
}
