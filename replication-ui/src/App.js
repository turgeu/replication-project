import React, { useEffect, useState } from "react";

const API_BASE = "http://localhost:8080"; // Backend URL

export default function App() {
  const [configs, setConfigs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState({
    sourceQuery: "",
    mongoCollection: "",
    schedule: "",        // "60" veya "0 * * * * *"
    mongoIndex: "atr_int_id,atr_int_source",
    mongoIndexName: "idx_comp_id",
    active: true,
  });

  // İlk yüklemede kayıtları çek
  useEffect(() => {
    loadConfigs();
  }, []);

  function loadConfigs() {
    setLoading(true);
    fetch(`${API_BASE}/configs`)
      .then((res) => res.json())
      .then((data) => setConfigs(data))
      .finally(() => setLoading(false));
  }

  function handleChange(e) {
    const { name, value, type, checked } = e.target;
    setForm((f) => ({ ...f, [name]: type === "checkbox" ? checked : value }));
  }

  function resetForm() {
    setEditingId(null);
    setForm({
      sourceQuery: "",
      mongoCollection: "",
      schedule: "",
      scheduleType: "interval",
      mongoIndex: "atr_int_id ASC, atr_int_source ASC",
      mongoIndexName: "idx_comp_id",
      active: true,
    });
  }


  function handleEdit(cfg) {
    setEditingId(cfg.id);
    setForm({
      sourceQuery: cfg.sourceQuery || "",
      mongoCollection: cfg.mongoCollection || "",
      schedule: cfg.schedule || "",
      scheduleType: cfg.scheduleType || "interval",
      mongoIndex: cfg.mongoIndex || "atr_int_id ASC, atr_int_source ASC",
      mongoIndexName: cfg.mongoIndexName || "idx_comp_id",
      active: cfg.active ?? true,
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  }



  async function handleDelete(id) {
    if (!window.confirm("Bu kaydı silmek istiyor musun?")) return;
    await fetch(`${API_BASE}/configs/${id}`, { method: "DELETE" });
    loadConfigs();
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const method = editingId ? "PUT" : "POST";
    const url = editingId
      ? `${API_BASE}/configs/${editingId}`
      : `${API_BASE}/configs`;

    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });

    if (!res.ok) {
      alert("Kayıt sırasında hata oluştu!");
      return;
    }

    resetForm();
    loadConfigs();
  }

  return (
    <div style={{ maxWidth: 1200, margin: "0 auto", padding: 24, fontFamily: "sans-serif" }}>
      <h1>Replication Config Yönetimi</h1>

      {/* FORM */}
      <form onSubmit={handleSubmit} style={{ display: "grid", gap: 12, marginBottom: 32 }}>
        <label style={{ display: "grid", gap: 6 }}>
          <span><b>Schedule Type</b></span>
          <select
            name="scheduleType"
            value={form.scheduleType}
            onChange={handleChange}
          >
            <option value="interval">Interval (saniye)</option>
            <option value="cron">Cron</option>
          </select>
        </label>

        <label style={{ display: "grid", gap: 6 }}>
          <span><b>Source Query (SQL)</b></span>
          <textarea
            name="sourceQuery"
            value={form.sourceQuery}
            onChange={handleChange}
            rows={5}
            placeholder="SELECT id AS id, product_int_id AS atr_int_id, 'getProductAttributeList' AS atr_int_source, name, value FROM product_attributes"
            required
          />
        </label>

        <label style={{ display: "grid", gap: 6 }}>
          <span><b>Mongo Collection</b></span>
          <input
            name="mongoCollection"
            value={form.mongoCollection}
            onChange={handleChange}
            placeholder="INKA_PRODUCT_PV_LIST_BY_ID"
            required
          />
        </label>

        <label style={{ display: "grid", gap: 6 }}>
          <span><b>Schedule</b> <small>(Saniye: 60 veya Cron: 0 * * * * *)</small></span>
          <input
            name="schedule"
            value={form.schedule}
            onChange={handleChange}
            placeholder="60"
            required
          />
        </label>

        <label style={{ display: "grid", gap: 6 }}>
          <span><b>Mongo Index</b> <small>(virgülle ayır)</small></span>
          <input
            name="mongoIndex"
            value={form.mongoIndex}
            onChange={handleChange}
            placeholder="atr_int_id,atr_int_source"
            required
          />
        </label>

        <label style={{ display: "grid", gap: 6 }}>
          <span><b>Mongo Index Name</b></span>
          <input
            name="mongoIndexName"
            value={form.mongoIndexName}
            onChange={handleChange}
            placeholder="idx_comp_id"
            required
          />
        </label>

        <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
          <input
            type="checkbox"
            name="active"
            checked={form.active}
            onChange={handleChange}
          />
          <span>Aktif</span>
        </label>

        <div style={{ display: "flex", gap: 8 }}>
          <button type="submit">
            {editingId ? "Güncelle" : "Ekle"}
          </button>
          {editingId && (
            <button type="button" onClick={resetForm}>
              İptal
            </button>
          )}
        </div>
      </form>

      {/* LİSTE */}
      <h2>Tanımlar</h2>
      {loading ? (
        <p>Yükleniyor…</p>
      ) : configs.length === 0 ? (
        <p>Henüz kayıt yok.</p>
      ) : (
        <table width="100%" cellPadding="8" style={{ borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ background: "#f2f2f2" }}>
              <th align="left">ID</th>
              <th align="left">Schedule</th>
              <th align="left">Collection</th>
              <th align="left">Active</th>
              <th align="left">Index</th>
              <th align="left">Index Name</th>
              <th align="left">Last Run</th>
              <th align="left">Query</th>
              <th>Aksiyon</th>
            </tr>
          </thead>
          <tbody>
            {configs.map((cfg) => (
              <tr key={cfg.id} style={{ borderTop: "1px solid #ddd" }}>
                <td>{cfg.id}</td>
                <td>{cfg.schedule}</td>
                <td>{cfg.mongoCollection}</td>
                <td>{cfg.active ? "Evet" : "Hayır"}</td>
                <td>{cfg.mongoIndex}</td>
                <td>{cfg.mongoIndexName}</td>
                <td>{cfg.lastRun ? cfg.lastRun.replace("T", " ") : "-"}</td>
                <td>
                  <pre style={{ whiteSpace: "pre-wrap", margin: 0 }}>{cfg.sourceQuery}</pre>
                </td>
                <td>
                  <button onClick={() => handleEdit(cfg)} style={{ marginRight: 8 }}>Düzenle</button>
                  <button onClick={() => handleDelete(cfg.id)}>Sil</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
