import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { useArticles } from "./ArticlesContext";
import ReactMarkdown from "react-markdown"; // For rendering markdown content
import "../css/pages/newsroom.css";

/**
 * ArticlePage component - Displays a single article in full detail
 * Fetches article data based on the slug parameter from the URL
 */
export default function ArticlePage() {
  // Extract the article slug from URL parameters
  const { slug } = useParams();
  
  // Get article context functions and dark mode state
  const { getArticleBySlug, dark } = useArticles();
  
  // Local state for the current article and loading status
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch article data when component mounts or slug changes
  useEffect(() => {
    setLoading(true);
    
    // Fetch article by slug and handle success/error cases
    getArticleBySlug(slug)
      .then(setArticle) // Set article data on success
      .catch(() => setArticle(null)) // Clear article on error (not found)
      .finally(() => setLoading(false)); // Always stop loading
  }, [slug]);

  // Show loading state while fetching article
  if (loading) return <div style={{ padding: 40 }}>Loading...</div>;
  
  // Show error state if article not found
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
      {/* Display article cover image if available */}
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
      
      {/* Article title */}
      <h1 className="article-title">{article.title}</h1>
      
      {/* Article metadata - author and date */}
      <div>
        <strong>By:</strong> {article.author} | <strong>Date:</strong> {article.date}
      </div>
      
      {/* Article summary section */}
      <div className="article-summary">
        <strong>Summary:</strong> {article.summary}
      </div>
      
      {/* Main article content rendered from markdown */}
      <div className="article-content" style={{ marginTop: 20 }}>
        <ReactMarkdown>{article.content}</ReactMarkdown>
      </div>
      
      {/* Navigation link back to newsroom */}
      <Link to="/newsroom" className="article-back-link">
        &larr; Back to Newsroom
      </Link>
    </div>
  );
}
