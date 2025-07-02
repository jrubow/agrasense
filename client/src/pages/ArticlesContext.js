import React, { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";

// Backend API root
const API_ROOT = "http://localhost:8080/api/articles";

const Categories = ["General", "Product", "Event", "Research"];

const ArticlesContext = createContext();

export function ArticlesProvider({ children }) {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [dark, setDark] = useState(false);

  // Fetch all articles on mount
  useEffect(() => {
    fetchArticles();
    // eslint-disable-next-line
  }, []);

  function fetchArticles() {
    setLoading(true);
    axios
      .get(API_ROOT)
      .then((res) => setArticles(res.data))
      .catch(() => setArticles([]))
      .finally(() => setLoading(false));
  }

  // Helper to refresh articles list
  function reloadArticles() {
    fetchArticles();
  }

  // CRUD actions with admin password header (put in .env for prod)
  const ADMIN_PASSWORD = "AsterlinktoANDA_password1234";

  async function createArticle(article) {
    await axios.post(API_ROOT, article, {
      headers: { "X-Admin-Password": ADMIN_PASSWORD },
    });
    reloadArticles();
  }

  async function updateArticle(id, article) {
    await axios.put(`${API_ROOT}/${id}`, article, {
      headers: { "X-Admin-Password": ADMIN_PASSWORD },
    });
    reloadArticles();
  }

  async function deleteArticle(id) {
    await axios.delete(`${API_ROOT}/${id}`, {
      headers: { "X-Admin-Password": ADMIN_PASSWORD },
    });
    reloadArticles();
  }

  // Get single by slug
  async function getArticleBySlug(slug) {
    const res = await axios.get(`${API_ROOT}/slug/${slug}`);
    return res.data;
  }

  return (
    <ArticlesContext.Provider
      value={{
        articles,
        setArticles,
        Categories,
        dark,
        setDark,
        loading,
        reloadArticles,
        createArticle,
        updateArticle,
        deleteArticle,
        getArticleBySlug,
      }}
    >
      {children}
    </ArticlesContext.Provider>
  );
}

export function useArticles() {
  return useContext(ArticlesContext);
}
