import { useEffect, useState, useRef } from "react";
import {
  getConversations, deleteConversation,
  renameConversation, getUploadedFiles
} from "../services/chatService";

function ConvMenu({ conv, onRename, onDelete, onClose }) {
  const ref = useRef(null);
  useEffect(() => {
    const handler = (e) => { if (!ref.current?.contains(e.target)) onClose(); };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div ref={ref} className="fade-in" style={{
      position: "absolute", right: "8px", top: "100%",
      background: "#1e1e2e", border: "1px solid #2a2a3a",
      borderRadius: "10px", padding: "4px",
      zIndex: 50, minWidth: "140px",
      boxShadow: "0 8px 24px rgba(0,0,0,0.4)"
    }}>
      <button onClick={onRename} style={{
        display: "flex", alignItems: "center", gap: "8px",
        width: "100%", padding: "8px 12px", background: "none",
        border: "none", borderRadius: "7px", color: "#d1d5db",
        fontSize: "13px", cursor: "pointer", textAlign: "left"
      }}
        onMouseEnter={e => e.currentTarget.style.background = "#2a2a3a"}
        onMouseLeave={e => e.currentTarget.style.background = "none"}
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"
            stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"
            stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
        </svg>
        Rename
      </button>
      <button onClick={onDelete} style={{
        display: "flex", alignItems: "center", gap: "8px",
        width: "100%", padding: "8px 12px", background: "none",
        border: "none", borderRadius: "7px", color: "#f87171",
        fontSize: "13px", cursor: "pointer", textAlign: "left"
      }}
        onMouseEnter={e => e.currentTarget.style.background = "rgba(239,68,68,0.1)"}
        onMouseLeave={e => e.currentTarget.style.background = "none"}
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <polyline points="3 6 5 6 21 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          <path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6"
            stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          <path d="M10 11v6M14 11v6M9 6V4a1 1 0 011-1h4a1 1 0 011 1v2"
            stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
        </svg>
        Delete
      </button>
    </div>
  );
}

export default function Sidebar({ onConversationClick, onNewChat, refreshRef, activeId }) {
  const [conversations, setConversations] = useState([]);
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [showFiles, setShowFiles] = useState(false);
  const [menuOpenId, setMenuOpenId] = useState(null);
  const [renamingId, setRenamingId] = useState(null);
  const [renameValue, setRenameValue] = useState("");
  const renameRef = useRef(null);

  const load = async () => {
    try { setConversations(await getConversations()); } catch {}
    try { setUploadedFiles(await getUploadedFiles()); } catch {}
  };

  useEffect(() => {
    load();
    if (refreshRef) refreshRef.current = load;
  }, []);

  useEffect(() => {
    if (renamingId) renameRef.current?.focus();
  }, [renamingId]);

  const handleDelete = async (id) => {
    setMenuOpenId(null);
    try { await deleteConversation(id); load(); } catch {}
  };

  const startRename = (conv) => {
    setMenuOpenId(null);
    setRenamingId(conv.id);
    setRenameValue(conv.title);
  };

  const submitRename = async (id) => {
    if (renameValue.trim()) {
      try { await renameConversation(id, renameValue); load(); } catch {}
    }
    setRenamingId(null);
  };

  return (
    <div style={{
      width: "260px", height: "100vh", flexShrink: 0,
      background: "#0d0d0f", borderRight: "1px solid #1a1a22",
      display: "flex", flexDirection: "column"
    }}>
      {/* Header */}
      <div style={{ padding: "18px 14px 14px", borderBottom: "1px solid #1a1a22" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "10px", marginBottom: "14px" }}>
          <div style={{
            width: "34px", height: "34px", flexShrink: 0,
            background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
            borderRadius: "10px",
            display: "flex", alignItems: "center", justifyContent: "center",
            boxShadow: "0 4px 12px rgba(108,99,255,0.4)"
          }}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"
                stroke="white" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <span style={{ color: "#fff", fontWeight: 700, fontSize: "16px", letterSpacing: "-0.3px" }}>
            RAGBot AI
          </span>
        </div>

        <button onClick={onNewChat} style={{
          width: "100%", padding: "9px 14px",
          background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
          border: "none", borderRadius: "10px",
          color: "white", fontSize: "13px", fontWeight: 600,
          cursor: "pointer", display: "flex", alignItems: "center",
          justifyContent: "center", gap: "7px",
          boxShadow: "0 4px 14px rgba(108,99,255,0.3)",
          transition: "opacity 0.15s"
        }}
          onMouseEnter={e => e.currentTarget.style.opacity = "0.88"}
          onMouseLeave={e => e.currentTarget.style.opacity = "1"}
        >
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
            <line x1="12" y1="5" x2="12" y2="19" stroke="white" strokeWidth="2.5" strokeLinecap="round"/>
            <line x1="5" y1="12" x2="19" y2="12" stroke="white" strokeWidth="2.5" strokeLinecap="round"/>
          </svg>
          New Chat
        </button>
      </div>

      {/* PDFs */}
      <div style={{ padding: "10px 14px", borderBottom: "1px solid #1a1a22" }}>
        <button onClick={() => setShowFiles(!showFiles)} style={{
          background: "none", border: "none", cursor: "pointer",
          display: "flex", alignItems: "center", gap: "7px",
          color: "#6b7280", fontSize: "11px", fontWeight: 600,
          letterSpacing: "0.07em", textTransform: "uppercase", padding: "2px 0",
          width: "100%"
        }}>
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
            <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"
              stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <polyline points="14 2 14 8 20 8" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
          PDFs
          {uploadedFiles.length > 0 &&
            <span style={{
              background: "rgba(108,99,255,0.25)", color: "#a78bfa",
              borderRadius: "8px", padding: "1px 7px", fontSize: "10px", fontWeight: 700
            }}>{uploadedFiles.length}</span>
          }
          <svg style={{ marginLeft: "auto", transition: "transform 0.2s", transform: showFiles ? "rotate(180deg)" : "rotate(0)" }}
            width="10" height="10" viewBox="0 0 24 24" fill="none">
            <polyline points="6 9 12 15 18 9" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"/>
          </svg>
        </button>

        {showFiles && (
          <div style={{ marginTop: "8px" }} className="fade-in">
            {uploadedFiles.length === 0
              ? <p style={{ color: "#374151", fontSize: "12px", padding: "2px 4px" }}>No PDFs uploaded yet</p>
              : uploadedFiles.map((f, i) => (
                <div key={i} style={{
                  color: "#a78bfa", fontSize: "12px", padding: "5px 8px",
                  borderRadius: "6px", background: "#13131a",
                  marginTop: "4px", overflow: "hidden",
                  textOverflow: "ellipsis", whiteSpace: "nowrap",
                  display: "flex", alignItems: "center", gap: "6px"
                }}>
                  <svg width="11" height="11" viewBox="0 0 24 24" fill="none">
                    <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"
                      stroke="currentColor" strokeWidth="2"/>
                    <polyline points="14 2 14 8 20 8" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                  {f}
                </div>
              ))
            }
          </div>
        )}
      </div>

      {/* Conversations */}
      <div style={{ flex: 1, overflowY: "auto", padding: "10px 8px" }}>
        <p style={{
          color: "#374151", fontSize: "11px", fontWeight: 600,
          letterSpacing: "0.07em", textTransform: "uppercase",
          padding: "4px 8px 8px"
        }}>Chats</p>

        {conversations.length === 0 &&
          <p style={{ color: "#374151", fontSize: "13px", padding: "6px 8px" }}>No chats yet</p>
        }

        {conversations.map(conv => (
          <div key={conv.id} style={{ position: "relative", marginBottom: "2px" }}>
            <div
              onClick={() => onConversationClick(conv.id)}
              style={{
                padding: "8px 36px 8px 10px",
                borderRadius: "8px", cursor: "pointer",
                background: activeId === conv.id ? "#1a1a2e" : "transparent",
                border: `1px solid ${activeId === conv.id ? "#2d2d4e" : "transparent"}`,
                transition: "all 0.12s"
              }}
              onMouseEnter={e => { if (activeId !== conv.id) e.currentTarget.style.background = "#13131a"; }}
              onMouseLeave={e => { if (activeId !== conv.id) e.currentTarget.style.background = "transparent"; }}
            >
              {renamingId === conv.id ? (
                <input
                  ref={renameRef}
                  value={renameValue}
                  onChange={e => setRenameValue(e.target.value)}
                  onBlur={() => submitRename(conv.id)}
                  onKeyDown={e => {
                    if (e.key === "Enter") submitRename(conv.id);
                    if (e.key === "Escape") setRenamingId(null);
                  }}
                  onClick={e => e.stopPropagation()}
                  style={{
                    background: "#0d0d0f", border: "1px solid #6c63ff",
                    color: "#e8e8e8", borderRadius: "6px",
                    padding: "2px 8px", fontSize: "13px",
                    width: "100%", outline: "none"
                  }}
                />
              ) : (
                <p style={{
                  color: "#c9cad4", fontSize: "13px", fontWeight: 400,
                  overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap"
                }}>
                  {conv.title}
                </p>
              )}
            </div>

            {/* Three-dot menu button */}
            {renamingId !== conv.id && (
              <button
                onClick={e => { e.stopPropagation(); setMenuOpenId(menuOpenId === conv.id ? null : conv.id); }}
                style={{
                  position: "absolute", right: "6px", top: "50%", transform: "translateY(-50%)",
                  background: "none", border: "none", cursor: "pointer",
                  color: "#4b5563", padding: "4px", borderRadius: "5px",
                  display: "flex", alignItems: "center", justifyContent: "center",
                  opacity: menuOpenId === conv.id ? 1 : 0,
                  transition: "opacity 0.15s"
                }}
                onMouseEnter={e => { e.currentTarget.style.opacity = "1"; e.currentTarget.style.color = "#9ca3af"; }}
                onMouseLeave={e => { e.currentTarget.style.opacity = menuOpenId === conv.id ? "1" : "0"; e.currentTarget.style.color = "#4b5563"; }}
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                  <circle cx="12" cy="5" r="1.5"/><circle cx="12" cy="12" r="1.5"/><circle cx="12" cy="19" r="1.5"/>
                </svg>
              </button>
            )}

            {menuOpenId === conv.id && (
              <ConvMenu
                conv={conv}
                onRename={() => startRename(conv)}
                onDelete={() => handleDelete(conv.id)}
                onClose={() => setMenuOpenId(null)}
              />
            )}
          </div>
        ))}
      </div>
    </div>
  );
}