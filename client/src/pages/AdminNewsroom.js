import React, { useState } from "react";
import { Link } from "react-router-dom";
import "../css/pages/newsroom.css"; // Adjust if your path differs

const emptyForm = {
  title: "",
  slug: "",
  summary: "",
  content: "",
  is_published: false,
  author: "Burhan Laxmidhar",
};

export default function AdminNewsroom() {
  const [articles, setArticles] = useState([
    {
      id: 1,
      title: "Sample Article",
      slug: "sample-article",
      summary: "This is a test article.",
      content: "Markdown content here.",
      is_published: true,
      author: "Burhan Laxmidhar",
    },
    {
      id: 2,
      title: "Steps to Leaps",
      slug: "steps-to-leaps",
      summary: "Moon",
      content: "More markdown content here.",
      is_published: false,
      author: "Burhan Laxmidhar",
    },
  ]);
  const [form, setForm] = useState(emptyForm);

  function handleChange(e) {
    const { name, value, type, checked } = e.target;
    setForm(f => ({
      ...f,
      [name]: type === "checkbox" ? checked : value,
    }));
  }

  function handleSubmit(e) {
    e.preventDefault();
    const newArticle = { ...form, id: Date.now() }; // Use timestamp as fake id
    setArticles([newArticle, ...articles]);
    setForm(emptyForm);
  }

  return (
    <div className="newsroom-container">
      <h1 className="newsroom-header">Admin Newsroom</h1>
      <h2>Add New Article</h2>
      <form onSubmit={handleSubmit} style={{ marginBottom: "2rem" }}>
        <div>
          <label>Title:<br />
            <input name="title" value={form.title} onChange={handleChange} required />
          </label>
        </div>
        <div>
          <label>Slug:<br />
            <input name="slug" value={form.slug} onChange={handleChange} required />
          </label>
        </div>
        <div>
          <label>Summary:<br />
            <input name="summary" value={form.summary} onChange={handleChange} required />
          </label>
        </div>
        <div>
          <label>Content (Markdown):<br />
            <textarea name="content" value={form.content} onChange={handleChange} rows={8} style={{ width: "100%" }} required />
          </label>
        </div>
        <div>
          <label>
            <input
              name="is_published"
              type="checkbox"
              checked={form.is_published}
              onChange={handleChange}
            />{" "}
            Published
          </label>
        </div>
        <button type="submit">Create Article</button>
      </form>

      <h2>All Articles</h2>
      <ul className="newsroom-list">
        {articles.map(article => (
          <li key={article.id} className="newsroom-article">
            <Link
              className="newsroom-title"
              to={`/article/${article.slug}`}
              style={{ textDecoration: "none", color: "#1d7933", fontWeight: 700, fontSize: "1.5em" }}
            >
              {article.title}
            </Link>
            {" "}
            {article.is_published ? (
              <span style={{
                background: "#1d7933", color: "white", fontSize: 13, padding: "0.15em 0.6em", borderRadius: 5, marginLeft: 8,
              }}>Published</span>
            ) : (
              <span style={{
                background: "#ccc", color: "#444", fontSize: 13, padding: "0.15em 0.6em", borderRadius: 5, marginLeft: 8,
              }}>Draft</span>
            )}
            <div className="newsroom-summary">{article.summary}</div>
          </li>
        ))}
      </ul>
    </div>
  );
}
