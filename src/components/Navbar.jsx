import { useNavigate } from "react-router-dom";

export default function Navbar({ onMenuClick }) {
  const navigate = useNavigate();
  const name = localStorage.getItem("name") || "User";
  const initial = name.charAt(0).toUpperCase();

  return (
    <div style={{
      height: "54px", flexShrink: 0,
      background: "#0d0d0f", borderBottom: "1px solid #1a1a22",
      display: "flex", alignItems: "center",
      justifyContent: "space-between", padding: "0 16px"
    }}>
      <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
        {/* Hamburger — mobile only */}
        <button
          onClick={onMenuClick}
          className="mobile-menu-btn"
          style={{
            background: "none", border: "none",
            color: "#9ca3af", cursor: "pointer",
            padding: "6px", borderRadius: "8px",
            display: "flex", alignItems: "center"
          }}
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <line x1="3" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <line x1="3" y1="12" x2="21" y2="12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <line x1="3" y1="18" x2="21" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </button>

        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <div style={{
            width: "28px", height: "28px",
            background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
            borderRadius: "8px",
            display: "flex", alignItems: "center", justifyContent: "center"
          }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"
                stroke="white" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <span style={{ color: "#fff", fontWeight: 700, fontSize: "15px" }}>RAGBot AI</span>
        </div>
      </div>

      <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <div style={{
            width: "30px", height: "30px",
            background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
            borderRadius: "50%",
            display: "flex", alignItems: "center", justifyContent: "center",
            color: "white", fontSize: "12px", fontWeight: 700
          }}>
            {initial}
          </div>
          {/* Hide name on small screens */}
          <span className="hide-mobile" style={{ color: "#9ca3af", fontSize: "14px" }}>{name}</span>
        </div>

        <button
          onClick={() => { localStorage.clear(); navigate("/login"); }}
          style={{
            background: "transparent", border: "1px solid #2a2a35",
            color: "#9ca3af", padding: "6px 12px",
            borderRadius: "8px", fontSize: "13px",
            cursor: "pointer", fontWeight: 500,
            display: "flex", alignItems: "center", gap: "6px",
            transition: "all 0.15s"
          }}
          onMouseEnter={e => {
            e.currentTarget.style.borderColor = "#f87171";
            e.currentTarget.style.color = "#f87171";
            e.currentTarget.style.background = "rgba(239,68,68,0.08)";
          }}
          onMouseLeave={e => {
            e.currentTarget.style.borderColor = "#2a2a35";
            e.currentTarget.style.color = "#9ca3af";
            e.currentTarget.style.background = "transparent";
          }}
        >
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"
              stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          <span className="hide-mobile">Sign out</span>
        </button>
      </div>
    </div>
  );
}