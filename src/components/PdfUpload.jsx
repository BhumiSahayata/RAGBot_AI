import { useState } from "react";
import { uploadPdf } from "../services/chatService";

export default function PdfUpload({ onUploadDone, onClose }) {
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(false);
  const [dragOver, setDragOver] = useState(false);

  const handleUpload = async () => {
    if (!file) return;
    setLoading(true); setStatus("");
    try {
      const result = await uploadPdf(file);
      setStatus(result);
      setFile(null);
      if (onUploadDone) onUploadDone();
    } catch {
      setStatus("Upload failed. Please try again.");
    }
    setLoading(false);
  };

  return (
    <div className="fade-in" style={{
      position: "absolute", bottom: "calc(100% + 10px)", left: "0",
      width: "340px", background: "#13131a",
      border: "1px solid #2a2a35", borderRadius: "16px",
      padding: "18px", zIndex: 100,
      boxShadow: "0 12px 40px rgba(0,0,0,0.5)"
    }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "14px" }}>
        <p style={{ color: "#e8e8e8", fontWeight: 600, fontSize: "14px" }}>Upload PDF</p>
        <button onClick={onClose} style={{
          background: "none", border: "none", color: "#6b7280",
          cursor: "pointer", fontSize: "18px", lineHeight: 1
        }}>×</button>
      </div>

      <div
        onDragOver={e => { e.preventDefault(); setDragOver(true); }}
        onDragLeave={() => setDragOver(false)}
        onDrop={e => {
          e.preventDefault(); setDragOver(false);
          const f = e.dataTransfer.files[0];
          if (f?.type === "application/pdf") setFile(f);
        }}
        onClick={() => document.getElementById("pdf-input").click()}
        style={{
          border: `2px dashed ${dragOver ? "#6c63ff" : "#2a2a35"}`,
          borderRadius: "10px", padding: "20px",
          textAlign: "center", cursor: "pointer",
          background: dragOver ? "rgba(108,99,255,0.06)" : "#0d0d0f",
          transition: "all 0.2s", marginBottom: "12px"
        }}
      >
        <input id="pdf-input" type="file" accept=".pdf"
          style={{ display: "none" }}
          onChange={e => setFile(e.target.files[0])}
        />
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" style={{ margin: "0 auto 8px", display: "block", color: file ? "#a78bfa" : "#4b5563" }}>
          <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" stroke="currentColor" strokeWidth="1.8"/>
          <polyline points="14 2 14 8 20 8" stroke="currentColor" strokeWidth="1.8"/>
          <line x1="12" y1="18" x2="12" y2="12" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
          <polyline points="9 15 12 12 15 15" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
        </svg>
        <p style={{ fontSize: "13px", color: file ? "#a78bfa" : "#6b7280" }}>
          {file ? file.name : "Drop PDF here or click to browse"}
        </p>
      </div>

      <button onClick={handleUpload} disabled={!file || loading} style={{
        width: "100%", padding: "10px",
        background: file && !loading ? "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)" : "#1e1e2e",
        border: "none", borderRadius: "10px",
        color: file && !loading ? "white" : "#4b5563",
        fontSize: "13px", fontWeight: 600,
        cursor: file && !loading ? "pointer" : "default",
        transition: "all 0.2s"
      }}>
        {loading ? "Processing..." : "Upload & Index"}
      </button>

      {status && (
        <p style={{
          marginTop: "10px", fontSize: "12px", fontWeight: 500,
          color: status.includes("successfully") ? "#4ade80" : "#f87171"
        }}>
          {status.includes("successfully") ? "✓ " : "✗ "}{status}
        </p>
      )}
    </div>
  );
}