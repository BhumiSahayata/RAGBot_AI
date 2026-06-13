import ReactMarkdown from "react-markdown";

export default function MessageBubble({ message, sender }) {
  const isUser = sender === "user";

  // Handle both string and object message formats
  const text = typeof message === "string" ? message : message?.text || "";
  const imageUrl = typeof message === "object" ? message?.image : null;
  const isTyping = text === "AI is typing...";

  if (isTyping) {
    return (
      <div className="msg-enter" style={{
        display: "flex", alignItems: "flex-start", gap: "10px", marginBottom: "20px"
      }}>
        <div style={{
          width: "30px", height: "30px", flexShrink: 0,
          background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
          borderRadius: "50%", display: "flex", alignItems: "center",
          justifyContent: "center", color: "white", fontSize: "11px", fontWeight: 700
        }}>R</div>
        <div style={{
          background: "#13131a", border: "1px solid #1e1e2e",
          borderRadius: "4px 14px 14px 14px",
          padding: "13px 18px", display: "flex", alignItems: "center", gap: "5px"
        }}>
          <span className="dot" />
          <span className="dot" />
          <span className="dot" />
        </div>
      </div>
    );
  }

  if (isUser) {
    return (
      <div className="msg-enter" style={{
        display: "flex", justifyContent: "flex-end", marginBottom: "14px"
      }}>
        <div style={{
          maxWidth: "70%",
          background: "linear-gradient(135deg, #6c63ff 0%, #7c3aed 100%)",
          color: "white", borderRadius: "14px 14px 4px 14px",
          padding: "11px 16px", fontSize: "14px", lineHeight: "1.6",
          boxShadow: "0 2px 12px rgba(108,99,255,0.25)"
        }}>
          {imageUrl && (
            <img
              src={imageUrl}
              alt="uploaded"
              style={{
                maxWidth: "100%", borderRadius: "8px",
                marginBottom: text ? "8px" : "0",
                display: "block"
              }}
            />
          )}
          {text && text !== "What's in this image?" ? text : null}
        </div>
      </div>
    );
  }

  return (
    <div className="msg-enter" style={{
      display: "flex", alignItems: "flex-start", gap: "10px", marginBottom: "20px"
    }}>
      <div style={{
        width: "30px", height: "30px", flexShrink: 0,
        background: "linear-gradient(135deg, #6c63ff 0%, #ec4899 100%)",
        borderRadius: "50%", display: "flex", alignItems: "center",
        justifyContent: "center", color: "white", fontSize: "11px", fontWeight: 700
      }}>R</div>
      <div style={{
        maxWidth: "72%", background: "#13131a",
        border: "1px solid #1e1e2e",
        color: "#e2e2e8", borderRadius: "4px 14px 14px 14px",
        padding: "12px 16px", fontSize: "14px", lineHeight: "1.7"
      }}>
        <ReactMarkdown components={{
          p: ({ node, ...p }) => <p style={{ margin: "0 0 8px" }} {...p} />,
          ul: ({ node, ...p }) => <ul style={{ paddingLeft: "18px", margin: "6px 0" }} {...p} />,
          ol: ({ node, ...p }) => <ol style={{ paddingLeft: "18px", margin: "6px 0" }} {...p} />,
          li: ({ node, ...p }) => <li style={{ marginBottom: "3px" }} {...p} />,
          code: ({ node, inline, ...p }) => inline
            ? <code style={{ background: "#1e1e2e", padding: "1px 6px", borderRadius: "4px", fontSize: "13px", color: "#a78bfa" }} {...p} />
            : <pre style={{ background: "#0d0d0f", border: "1px solid #1e1e2e", color: "#e2e8f0", padding: "14px", borderRadius: "10px", overflowX: "auto", fontSize: "13px", margin: "8px 0" }}><code {...p} /></pre>,
          strong: ({ node, ...p }) => <strong style={{ color: "#fff", fontWeight: 600 }} {...p} />
        }}>
          {text}
        </ReactMarkdown>
      </div>
    </div>
  );
}