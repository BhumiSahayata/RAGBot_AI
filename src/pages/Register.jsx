import { useState } from "react";
import { registerUser } from "../services/authService";
import { useNavigate, Link } from "react-router-dom";

export default function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(""); setSuccess("");
    setLoading(true);
    try {
      const response = await registerUser({ name, email, password });
      if (response.data === "Email already exists") {
        setError("This email is already registered.");
      } else {
        setSuccess("Account created! Redirecting...");
        setTimeout(() => navigate("/login"), 1500);
      }
    } catch {
      setError("Something went wrong. Try again.");
    }
    setLoading(false);
  };

  const inputStyle = {
    width: "100%", background: "#0d0d0f",
    border: "1px solid #2a2a35", borderRadius: "10px",
    padding: "11px 14px", fontSize: "14px",
    color: "#e8e8e8", outline: "none",
    transition: "border-color 0.15s"
  };

  return (
    <div style={{
      minHeight: "100vh", background: "#0d0d0f",
      display: "flex", alignItems: "center", justifyContent: "center",
      padding: "20px"
    }}>
      <div style={{
        position: "fixed", top: "20%", left: "50%", transform: "translateX(-50%)",
        width: "600px", height: "400px",
        background: "radial-gradient(ellipse, rgba(236,72,153,0.1) 0%, transparent 70%)",
        pointerEvents: "none"
      }} />

      <div style={{
        width: "100%", maxWidth: "420px",
        background: "#13131a", border: "1px solid #1e1e2e",
        borderRadius: "20px", padding: "40px",
        position: "relative", zIndex: 1
      }}>
        <div style={{ textAlign: "center", marginBottom: "32px" }}>
          <div style={{
            width: "52px", height: "52px", margin: "0 auto 14px",
            background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
            borderRadius: "16px",
            display: "flex", alignItems: "center", justifyContent: "center",
            boxShadow: "0 8px 24px rgba(108,99,255,0.35)"
          }}>
            <svg width="26" height="26" viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"
                stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <h1 style={{ fontSize: "22px", fontWeight: 700, color: "#fff", marginBottom: "4px" }}>
            RAGBot AI
          </h1>
          <p style={{ color: "#6b7280", fontSize: "14px" }}>Create your account</p>
        </div>

        <form onSubmit={handleSubmit}>
          {[
            { label: "Full Name", value: name, set: setName, type: "text", placeholder: "Your name" },
            { label: "Email", value: email, set: setEmail, type: "email", placeholder: "you@example.com" },
            { label: "Password", value: password, set: setPassword, type: "password", placeholder: "Min 6 characters" }
          ].map(({ label, value, set, type, placeholder }) => (
            <div key={label} style={{ marginBottom: "16px" }}>
              <label style={{ display: "block", fontSize: "13px", fontWeight: 500, color: "#9ca3af", marginBottom: "6px" }}>
                {label}
              </label>
              <input
                type={type} value={value} required
                onChange={e => set(e.target.value)}
                placeholder={placeholder}
                style={inputStyle}
                onFocus={e => e.target.style.borderColor = "#6c63ff"}
                onBlur={e => e.target.style.borderColor = "#2a2a35"}
              />
            </div>
          ))}

          {error && (
            <div style={{
              background: "rgba(239,68,68,0.1)", border: "1px solid rgba(239,68,68,0.25)",
              borderRadius: "8px", padding: "10px 14px",
              color: "#f87171", fontSize: "13px", marginBottom: "16px"
            }}>{error}</div>
          )}
          {success && (
            <div style={{
              background: "rgba(34,197,94,0.1)", border: "1px solid rgba(34,197,94,0.25)",
              borderRadius: "8px", padding: "10px 14px",
              color: "#4ade80", fontSize: "13px", marginBottom: "16px"
            }}>{success}</div>
          )}

          <button
            type="submit" disabled={loading}
            style={{
              width: "100%", padding: "12px",
              background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
              border: "none", borderRadius: "10px",
              color: "white", fontSize: "14px", fontWeight: 600,
              cursor: loading ? "not-allowed" : "pointer",
              opacity: loading ? 0.7 : 1,
              boxShadow: "0 4px 16px rgba(108,99,255,0.3)"
            }}
          >
            {loading ? "Creating account..." : "Create account"}
          </button>
        </form>

        <p style={{ textAlign: "center", marginTop: "20px", fontSize: "14px", color: "#6b7280" }}>
          Already have an account?{" "}
          <Link to="/login" style={{ color: "#a78bfa", textDecoration: "none", fontWeight: 500 }}>
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
}