import { useState, useRef } from "react";

export default function ChatInput({ onSend, onPdfClick }) {
  const [message, setMessage] = useState("");
  const [image, setImage] = useState(null);      // { base64, mediaType, preview }
  const textareaRef = useRef(null);
  const imageInputRef = useRef(null);

  const handleImageSelect = (file) => {
    if (!file || !file.type.startsWith("image/")) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      const base64 = e.target.result.split(",")[1];
      setImage({
        base64,
        mediaType: file.type,
        preview: e.target.result
      });
    };
    reader.readAsDataURL(file);
  };

  const handleSend = () => {
    if (!message.trim() && !image) return;
    onSend(
      message || "What's in this image?",
      image?.base64 || null,
      image?.mediaType || null
    );
    setMessage("");
    setImage(null);
    if (textareaRef.current) textareaRef.current.style.height = "auto";
  };

  const active = message.trim().length > 0 || image !== null;

  return (
    <div style={{
      padding: "12px 20px 18px",
      background: "#0d0d0f",
      borderTop: "1px solid #1a1a22"
    }}>
      <div style={{ maxWidth: "740px", margin: "0 auto" }}>

        {/* Image preview */}
        {image && (
          <div style={{
            marginBottom: "8px", position: "relative",
            display: "inline-block"
          }}>
            <img
              src={image.preview}
              alt="preview"
              style={{
                height: "80px", borderRadius: "10px",
                border: "1px solid #2a2a35", objectFit: "cover"
              }}
            />
            <button
              onClick={() => setImage(null)}
              style={{
                position: "absolute", top: "-8px", right: "-8px",
                width: "22px", height: "22px",
                background: "#1e1e2e", border: "1px solid #2a2a35",
                borderRadius: "50%", color: "#9ca3af",
                cursor: "pointer", fontSize: "14px",
                display: "flex", alignItems: "center", justifyContent: "center"
              }}
            >×</button>
          </div>
        )}

        <div style={{
          background: "#13131a", border: "1px solid #2a2a35",
          borderRadius: "16px", display: "flex",
          alignItems: "flex-end", gap: "8px",
          padding: "10px 12px",
          boxShadow: "0 4px 20px rgba(0,0,0,0.3)",
          transition: "border-color 0.2s"
        }}
          onFocusCapture={e => e.currentTarget.style.borderColor = "#6c63ff"}
          onBlurCapture={e => e.currentTarget.style.borderColor = "#2a2a35"}
        >
          {/* PDF button */}
          <button
            onClick={onPdfClick}
            title="Upload PDF"
            style={{
              background: "none", border: "none", cursor: "pointer",
              color: "#4b5563", padding: "6px", borderRadius: "8px",
              display: "flex", alignItems: "center",
              flexShrink: 0, transition: "all 0.15s"
            }}
            onMouseEnter={e => { e.currentTarget.style.color = "#a78bfa"; e.currentTarget.style.background = "rgba(108,99,255,0.1)"; }}
            onMouseLeave={e => { e.currentTarget.style.color = "#4b5563"; e.currentTarget.style.background = "none"; }}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"
                stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>

          {/* Image button */}
          <button
            onClick={() => imageInputRef.current?.click()}
            title="Upload Image"
            style={{
              background: "none", border: "none", cursor: "pointer",
              color: image ? "#a78bfa" : "#4b5563",
              padding: "6px", borderRadius: "8px",
              display: "flex", alignItems: "center",
              flexShrink: 0, transition: "all 0.15s"
            }}
            onMouseEnter={e => { e.currentTarget.style.color = "#a78bfa"; e.currentTarget.style.background = "rgba(108,99,255,0.1)"; }}
            onMouseLeave={e => { e.currentTarget.style.color = image ? "#a78bfa" : "#4b5563"; e.currentTarget.style.background = "none"; }}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"
                stroke="currentColor" strokeWidth="2"/>
              <circle cx="8.5" cy="8.5" r="1.5" fill="currentColor"/>
              <polyline points="21 15 16 10 5 21"
                stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          </button>

          <input
            ref={imageInputRef}
            type="file"
            accept="image/*"
            style={{ display: "none" }}
            onChange={e => handleImageSelect(e.target.files[0])}
          />

          {/* Textarea */}
          <textarea
            ref={textareaRef}
            rows={1}
            placeholder={image ? "Ask about this image..." : "Ask anything..."}
            value={message}
            onChange={e => {
              setMessage(e.target.value);
              e.target.style.height = "auto";
              e.target.style.height = Math.min(e.target.scrollHeight, 140) + "px";
            }}
            onKeyDown={e => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                handleSend();
              }
            }}
            style={{
              flex: 1, background: "transparent",
              border: "none", outline: "none", resize: "none",
              color: "#e8e8e8", fontSize: "15px", lineHeight: "1.55",
              fontFamily: "Inter, sans-serif",
              maxHeight: "140px", overflowY: "auto", padding: "4px 0"
            }}
          />

          {/* Send button */}
          <button
            onClick={handleSend}
            disabled={!active}
            style={{
              width: "36px", height: "36px", flexShrink: 0,
              background: active
                ? "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)"
                : "#1e1e2e",
              border: "none", borderRadius: "10px",
              cursor: active ? "pointer" : "default",
              display: "flex", alignItems: "center", justifyContent: "center",
              transition: "all 0.2s",
              boxShadow: active ? "0 2px 10px rgba(108,99,255,0.4)" : "none"
            }}
          >
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
              <path d="M22 2L11 13" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>
        </div>

        <p style={{ textAlign: "center", fontSize: "11px", color: "#374151", marginTop: "7px" }}>
          Enter to send · Shift+Enter for new line
        </p>
      </div>
    </div>
  );
}