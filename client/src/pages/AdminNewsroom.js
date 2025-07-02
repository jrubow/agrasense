import React, { useState, useRef, useEffect } from "react";
import { Link } from "react-router-dom";
import { useArticles } from "./ArticlesContext";
import ReactMarkdown from "react-markdown";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "../css/pages/newsroom.css";

function makeSlug(title) {
  return title
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/(^-|-$)/g, "");
}

const emptyForm = {
  title: "",
  summary: "",
  content: "",
  is_published: false,
  author: "",
  date: "",
  tags: "",
  category: "",
  image: "",
  history: [],
  publishedTimestamp: "",
};

function readFileAsDataURL(file) {
  return new Promise((res) => {
    const reader = new FileReader();
    reader.onload = (e) => res(e.target.result);
    reader.readAsDataURL(file);
  });
}

export default function AdminNewsroom() {
  const {
    articles = [],
    Categories = [],
    dark,
    setDark,
    loading,
    reloadArticles,
    createArticle,
    updateArticle,
    deleteArticle,
  } = useArticles() || {};

  // --- Form state
  const [form, setForm] = useState(emptyForm);
  const [editId, setEditId] = useState(null);
  const [showPreview, setShowPreview] = useState(false);

  // --- Search, sort, pagination
  const [search, setSearch] = useState("");
  const [filterCat, setFilterCat] = useState("");
  const [sortBy, setSortBy] = useState("date-desc");
  const [page, setPage] = useState(1);
  const PAGE_SIZE = 5;

  // --- Undo/revert
  const prevArticleRef = useRef(null);

  // --- Image input
  const imageInputRef = useRef();

  // --- Keyboard shortcuts
  useEffect(() => {
    function handleKey(e) {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "s") {
        e.preventDefault();
        document.getElementById("submit-btn")?.click();
      } else if (e.key === "Escape" && editId) {
        handleCancelEdit();
      }
    }
    window.addEventListener("keydown", handleKey);
    return () => window.removeEventListener("keydown", handleKey);
    // eslint-disable-next-line
  }, [editId, form]);

  // --- Form handlers
  function handleChange(e) {
    const { name, value, type, checked } = e.target;
    setForm((f) => ({
      ...f,
      [name]: type === "checkbox" ? checked : value,
    }));
  }

  async function handleImageUpload(e) {
    const file = e.target.files[0];
    if (!file) return;
    const url = await readFileAsDataURL(file);
    setForm((f) => ({ ...f, image: url }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const slug = makeSlug(form.title);
    const date = form.date || new Date().toISOString().slice(0, 10);
    const tags = form.tags.split(",").map((t) => t.trim()).filter(Boolean);

    // Set publishedTimestamp only if publishing, or preserve existing if editing
    let publishedTimestamp = form.publishedTimestamp;
    if (form.is_published && !publishedTimestamp) {
      publishedTimestamp = new Date().toISOString();
    }
    if (!form.is_published) {
      publishedTimestamp = "";
    }

    const articleData = {
      ...form,
      slug,
      date,
      tags,
      category: form.category || Categories[0] || "",
      is_published: !!form.is_published,
      publishedTimestamp,
    };

    try {
      if (editId) {
        await updateArticle(editId, { ...articleData, id: editId });
        toast.success("Article updated!");
      } else {
        await createArticle(articleData);
        toast.success("Article created!");
      }
      setForm(emptyForm);
      setEditId(null);
      setShowPreview(false);
      setPage(1);
    } catch (err) {
      toast.error("Failed to save article!");
    }
  }

  function handleEdit(article) {
    prevArticleRef.current = article;
    setForm({
      ...article,
      tags: (article.tags || []).join(", "),
      image: article.image || "",
      is_published: !!article.is_published,
      publishedTimestamp: article.publishedTimestamp || "",
    });
    setEditId(article.id);
    setShowPreview(false);
  }

  function handleCancelEdit() {
    setEditId(null);
    setForm(emptyForm);
    setShowPreview(false);
  }

  async function handleDelete(id) {
    if (!window.confirm("Delete this article? This cannot be undone.")) return;
    await deleteArticle(id);
    toast.success("Article deleted.");
    handleCancelEdit();
    setPage(1);
  }

  function handlePreview(e) {
    e.preventDefault();
    setShowPreview((prev) => !prev);
  }

  function handleRevert() {
    if (!window.confirm("Revert changes to last saved?")) return;
    const prev = prevArticleRef.current;
    if (prev) setForm({ ...prev, tags: prev.tags.join(", ") });
    toast.info("Edits reverted.");
  }

  function handleExport() {
    const blob = new Blob([JSON.stringify(articles, null, 2)], {
      type: "application/json",
    });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "articles.json";
    a.click();
  }

  function handleImport(e) {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (evt) => {
      try {
        const imported = JSON.parse(evt.target.result);
        // You could send to backend in a loop (optional)
        toast.success("Imported articles!");
        reloadArticles();
      } catch {
        toast.error("Invalid file format.");
      }
    };
    reader.readAsText(file);
    e.target.value = "";
  }

  // --- Filter, sort, paginate
  let filtered = (articles || []).filter((a) =>
    (a.title + a.summary + a.author + (a.tags?.join(",") || "") + a.category)
      .toLowerCase()
      .includes(search.toLowerCase())
  );
  if (filterCat) filtered = filtered.filter((a) => a.category === filterCat);
  if (sortBy === "date-desc")
    filtered = filtered.slice().sort((a, b) => (b.date || "").localeCompare(a.date || ""));
  if (sortBy === "date-asc")
    filtered = filtered.slice().sort((a, b) => (a.date || "").localeCompare(b.date || ""));
  if (sortBy === "title-asc")
    filtered = filtered.slice().sort((a, b) => (a.title || "").localeCompare(b.title || ""));
  if (sortBy === "title-desc")
    filtered = filtered.slice().sort((a, b) => (b.title || "").localeCompare(a.title || ""));

  const numPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const shown = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  // --- Dark mode
  useEffect(() => {
    document.body.classList.toggle("dark", dark);
  }, [dark]);

  // --- Markdown Cheat Sheet
  const markdownCheat = `
# Heading 1
## Heading 2
**Bold** *Italic*  
[Link](https://example.com)  
- List item
1. Numbered item

> Blockquote
\`inline code\`

\`\`\`js
console.log("code block")
\`\`\`
  `;

  return (
    <div className={`newsroom-container${dark ? " dark-bg" : ""}`}>
      <ToastContainer position="top-center" autoClose={2200} />
      <button
        className="theme-toggle"
        onClick={() => setDark && setDark((d) => !d)}
        style={{ float: "right", marginTop: "-1rem" }}
        title="Toggle Dark Mode"
      >
        {dark ? "🌙" : "🌞"}
      </button>
      <h1 className="newsroom-header">Admin Newsroom</h1>

      {/* --- Controls row --- */}
      <div
        className="admin-controls"
        style={{ display: "flex", gap: 14, alignItems: "center", marginBottom: 18 }}
      >
        <input
          style={{ flex: 2, minWidth: 160 }}
          type="search"
          placeholder="Search by title, author, summary, tag, category"
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(1);
          }}
        />
        <select value={filterCat} onChange={(e) => setFilterCat(e.target.value)} style={{ flex: 1 }}>
          <option value="">All Categories</option>
          {Categories.map((c) => (
            <option key={c}>{c}</option>
          ))}
        </select>
        <select value={sortBy} onChange={(e) => setSortBy(e.target.value)} style={{ flex: 1 }}>
          <option value="date-desc">Newest</option>
          <option value="date-asc">Oldest</option>
          <option value="title-asc">Title A-Z</option>
          <option value="title-desc">Title Z-A</option>
        </select>
        <button className="admin-btn" onClick={handleExport} title="Export all as JSON">
          Export
        </button>
        <label className="admin-btn" style={{ cursor: "pointer", margin: 0 }}>
          Import
          <input type="file" accept=".json" onChange={handleImport} style={{ display: "none" }} />
        </label>
      </div>

      {/* --- Form --- */}
      <h2>{editId ? "Edit Article" : "Add New Article"}</h2>
      <form
        onSubmit={handleSubmit}
        className="admin-form"
        style={{ marginBottom: "2rem", maxWidth: 650 }}
      >
        <label>
          Title:
          <input name="title" value={form.title} onChange={handleChange} required type="text" autoFocus />
        </label>
        <label>
          Summary:
          <input name="summary" value={form.summary} onChange={handleChange} required type="text" />
        </label>
        <label>
          Author:
          <input name="author" value={form.author} onChange={handleChange} required type="text" />
        </label>
        <label>
          Date:
          <input name="date" value={form.date} onChange={handleChange} type="date" />
        </label>
        <label>
          Category:
          <select name="category" value={form.category} onChange={handleChange} required>
            <option value="">Select a category</option>
            {Categories.map((c) => (
              <option key={c}>{c}</option>
            ))}
          </select>
        </label>
        <label>
          Tags (comma-separated):
          <input name="tags" value={form.tags} onChange={handleChange} type="text" placeholder="e.g. agtech, mesh, sensors" />
        </label>
        <label>
          Cover Image:
          <input
            ref={imageInputRef}
            type="file"
            accept="image/*"
            onChange={handleImageUpload}
            style={{ marginBottom: 10 }}
          />
          {form.image && (
            <div style={{ marginTop: 10 }}>
              <img
                src={form.image}
                alt="cover"
                style={{ maxWidth: 130, borderRadius: 5, boxShadow: "0 1px 4px #aaa" }}
              />
              <button
                type="button"
                className="admin-btn"
                style={{ marginLeft: 10, fontSize: 13 }}
                onClick={() => setForm((f) => ({ ...f, image: "" }))}
              >
                Remove
              </button>
            </div>
          )}
        </label>
        <label>
          Content (Markdown):
          <textarea name="content" value={form.content} onChange={handleChange} required rows={8} />
        </label>
        <details style={{ marginBottom: 10 }}>
          <summary style={{ cursor: "pointer", color: "#1d7933" }}>Markdown Cheat Sheet</summary>
          <pre
            style={{
              background: "#faf9f8",
              color: "#222",
              padding: 12,
              borderRadius: 6,
              fontSize: 13,
              margin: 0,
            }}
          >
            {markdownCheat}
          </pre>
        </details>
        <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
          <input
            name="is_published"
            type="checkbox"
            checked={form.is_published}
            onChange={handleChange}
          />
          Published
        </label>
        <div style={{ display: "flex", gap: 12, marginTop: 8 }}>
          <button type="submit" id="submit-btn" className="admin-submit-btn">
            {editId ? "Save Changes" : "Create Article"}
          </button>
          <button
            onClick={handlePreview}
            className="admin-submit-btn"
            style={{ background: "#888" }}
            type="button"
          >
            {showPreview ? "Hide Preview" : "Preview"}
          </button>
          {editId && (
            <>
              <button
                onClick={handleCancelEdit}
                className="admin-submit-btn"
                style={{ background: "#bbb", color: "#222" }}
                type="button"
              >
                Cancel
              </button>
              <button
                onClick={handleRevert}
                className="admin-btn"
                type="button"
                style={{ background: "#e4e4e4", color: "#333" }}
              >
                Undo
              </button>
            </>
          )}
        </div>
      </form>
      {showPreview && (
        <div
          style={{
            marginBottom: 30,
            border: "1px solid #eee",
            padding: 20,
            borderRadius: 8,
            background: "#fafafa",
          }}
        >
          <h3 style={{ marginTop: 0, color: "#1d7933" }}>Live Preview</h3>
          {form.image && (
            <img
              src={form.image}
              alt="cover"
              style={{
                maxWidth: 200,
                borderRadius: 8,
                boxShadow: "0 1px 6px #ccc",
                marginBottom: 18,
              }}
            />
          )}
          <ReactMarkdown>{form.content || "*Nothing to preview yet.*"}</ReactMarkdown>
        </div>
      )}

      {/* --- Article List --- */}
      <h2>All Articles</h2>
      {loading && <div>Loading...</div>}
      <ul className="newsroom-list">
        {shown.length > 0 ? shown.map((article) => (
          <li key={article.id} className="newsroom-article">
            <div style={{ display: "flex", gap: 16, alignItems: "flex-start" }}>
              {article.image && (
                <img
                  src={article.image}
                  alt="cover"
                  style={{
                    width: 56,
                    height: 56,
                    objectFit: "cover",
                    borderRadius: 6,
                    boxShadow: "0 1px 3px #ddd",
                  }}
                />
              )}
              <div style={{ flex: 1 }}>
                <Link className="newsroom-title" to={`/article/${article.slug}`}>
                  {article.title}
                </Link>
                {article.is_published ? (
                  <span className="badge-published">Published</span>
                ) : (
                  <span className="badge-draft">Draft</span>
                )}
                <div className="newsroom-summary">{article.summary}</div>
                <div className="newsroom-date">
                  <strong>Author:</strong> {article.author} | <strong>Date:</strong> {article.date} |{" "}
                  <strong>Category:</strong> {article.category}
                </div>
                {article.tags && article.tags.length > 0 && (
                  <div style={{ marginTop: 2 }}>
                    {article.tags.map((t) => (
                      <span key={t} className="tag">
                        {t}
                      </span>
                    ))}
                  </div>
                )}
                <div style={{ marginTop: 8, display: "flex", gap: 8 }}>
                  <button className="admin-btn" onClick={() => handleEdit(article)}>
                    Edit
                  </button>
                  <button
                    className="admin-btn admin-btn-danger"
                    onClick={() => handleDelete(article.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          </li>
        )) : (
          <div style={{ margin: "2.2rem 0", textAlign: "center", color: "#888" }}>
            No articles found.
          </div>
        )}
      </ul>

      {/* --- Pagination --- */}
      <div style={{ display: "flex", justifyContent: "center", margin: "22px 0 10px 0", gap: 8 }}>
        {[...Array(numPages)].map((_, i) => (
          <button
            key={i}
            className={`admin-btn${i + 1 === page ? " admin-btn-active" : ""}`}
            onClick={() => setPage(i + 1)}
            style={{ minWidth: 28, padding: "3px 10px" }}
          >
            {i + 1}
          </button>
        ))}
      </div>
    </div>
  );
}
