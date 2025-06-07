// src/pages/Newsroom.js
import React from "react";
import { Link } from "react-router-dom";
import articles from "./articles.js";
import '../css/pages/newsroom.css';


const Newsroom = () => {
  // Only show published articles
  const publishedArticles = articles.filter((a) => a.is_published);

  return (
    <div className="newsroom-container">
  <h1 className="newsroom-header">Newsroom</h1>
  {publishedArticles.map(article => (
    <div key={article.id} className="newsroom-article">
      <Link to={`/article/${article.slug}`} className="newsroom-title">{article.title}</Link>
      <div className="newsroom-summary">{article.summary}</div>
      <div className="newsroom-date">
        <div style={{marginRight:"10px"}}> {new Date(article.published_at).toLocaleDateString()} </div>
        {article.author}
      </div>
    </div>
  ))}
</div>
  );
};

export default Newsroom;
