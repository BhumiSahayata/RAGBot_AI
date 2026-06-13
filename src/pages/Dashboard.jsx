import { useState, useRef } from "react";
import ChatWindow from "../components/ChatWindow";
import ChatInput from "../components/ChatInput";
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
import PdfUpload from "../components/PdfUpload";
import { sendMessage, getMessages } from "../services/chatService";

export default function Dashboard() {
  const name = localStorage.getItem("name") || "User";
  const firstName = name.split(" ")[0];

  const [messages, setMessages] = useState([{
    text: `Hello, ${firstName} 👋\n\nI'm RAGBot — your AI assistant. Ask me anything, or upload a PDF to chat with your documents.`,
    sender: "ai"
  }]);
  const [selectedConversationId, setSelectedConversationId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showPdf, setShowPdf] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const sidebarRefreshRef = useRef(null);

  const startNewChat = () => {
    setSelectedConversationId(null);
    setMessages([{
      text: `Hello, ${firstName} 👋\n\nI'm RAGBot — your AI assistant. Ask me anything, or upload a PDF to chat with your documents.`,
      sender: "ai"
    }]);
    setShowPdf(false);
    setSidebarOpen(false);
  };

  const handleSend = async (message, imageBase64 = null, imageMediaType = null) => {
    const userMsg = imageBase64
      ? { text: message, sender: "user", image: `data:${imageMediaType};base64,${imageBase64}` }
      : { text: message, sender: "user" };

    setMessages(prev => [...prev, userMsg]);
    setLoading(true);

    try {
      const token = localStorage.getItem("token");
      const response = await sendMessage(
        message, token, selectedConversationId,
        imageBase64, imageMediaType
      );
      setSelectedConversationId(response.conversationId);
      setMessages(prev => [...prev, { text: response.answer, sender: "ai" }]);
      if (sidebarRefreshRef.current) sidebarRefreshRef.current();
    } catch {
      setMessages(prev => [
        ...prev,
        { text: "Something went wrong. Please try again.", sender: "ai" }
      ]);
    }
    setLoading(false);
  };

  const loadConversation = async (conversationId) => {
    try {
      const data = await getMessages(conversationId);
      setMessages(data.map(msg => ({
        text: msg.content,
        sender: msg.sender === "USER" ? "user" : "ai"
      })));
      setSelectedConversationId(conversationId);
      setShowPdf(false);
      setSidebarOpen(false); // close sidebar on mobile after selecting
    } catch (e) { console.error(e); }
  };

  return (
    <div style={{ display: "flex", height: "100vh", overflow: "hidden", background: "#0d0d0f", position: "relative" }}>

      {/* Mobile overlay — tap to close sidebar */}
      {sidebarOpen && (
        <div
          onClick={() => setSidebarOpen(false)}
          style={{
            position: "fixed", inset: 0,
            background: "rgba(0,0,0,0.6)",
            zIndex: 40, display: "block"
          }}
        />
      )}

      {/* Mobile sidebar overlay */}
{sidebarOpen && (
  <div
    onClick={() => setSidebarOpen(false)}
    style={{
      position: "fixed", inset: 0,
      background: "rgba(0,0,0,0.6)",
      zIndex: 40
    }}
  />
)}

{/* Mobile sidebar — slides in */}
<div
  className="sidebar-wrapper"
  style={{
    position: "fixed", top: 0, left: 0, height: "100vh",
    zIndex: 50,
    transform: sidebarOpen ? "translateX(0)" : "translateX(-100%)",
    transition: "transform 0.25s ease"
  }}
>
  <Sidebar
    onConversationClick={loadConversation}
    onNewChat={startNewChat}
    refreshRef={sidebarRefreshRef}
    activeId={selectedConversationId}
  />
</div>

{/* Desktop sidebar */}
<div className="desktop-sidebar">
  <Sidebar
    onConversationClick={loadConversation}
    onNewChat={startNewChat}
    refreshRef={sidebarRefreshRef}
    activeId={selectedConversationId}
  />
</div>

      {/* Main content */}
      <div style={{ flex: 1, display: "flex", flexDirection: "column", overflow: "hidden", minWidth: 0 }}>
        <Navbar onMenuClick={() => setSidebarOpen(true)} />

        <ChatWindow
          messages={loading
            ? [...messages, { text: "AI is typing...", sender: "ai" }]
            : messages
          }
        />

        <div style={{ position: "relative" }}>
          {showPdf && (
            <div style={{ position: "absolute", bottom: "100%", left: "0", right: "0", padding: "0 16px", zIndex: 100 }}>
              <PdfUpload
                onUploadDone={() => {
                  if (sidebarRefreshRef.current) sidebarRefreshRef.current();
                  setShowPdf(false);
                }}
                onClose={() => setShowPdf(false)}
              />
            </div>
          )}
          <ChatInput
            onSend={handleSend}
            onPdfClick={() => setShowPdf(v => !v)}
          />
        </div>
      </div>
    </div>
  );
}