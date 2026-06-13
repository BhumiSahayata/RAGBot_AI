import { useEffect, useRef } from "react";
import MessageBubble from "./MessageBubble";

export default function ChatWindow({ messages }) {
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div style={{
      flex: 1, overflowY: "auto",
      padding: "28px 20px",
      background: "#0d0d0f"
    }}>
      <div style={{ maxWidth: "740px", margin: "0 auto" }}>
        {messages.map((msg, i) => (
  <MessageBubble key={i} message={msg} sender={msg.sender} />
))}
        <div ref={bottomRef} />
      </div>
    </div>
  );
}