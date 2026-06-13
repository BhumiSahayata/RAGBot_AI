import axios from "axios";

const API_URL = `${import.meta.env.VITE_API_URL}/api/chat`;
const PDF_URL = `${import.meta.env.VITE_API_URL}/api/pdf`;
// Auto logout if token expired
const handleUnauthorized = (error) => {
  if (error.response?.status === 401) {
    localStorage.clear();
    window.location.href = "/login";
  }
  throw error;
};

export const sendMessage = async (
  question, token, conversationId = null,
  imageBase64 = null, imageMediaType = null
) => {
  try {
    const response = await axios.post(
      API_URL,
      { question, conversationId, imageBase64, imageMediaType },
      { headers: { Authorization: `Bearer ${token}` } }
    );
    return response.data;
  } catch (error) {
    handleUnauthorized(error);
  }
};

export const getConversations = async () => {
  try {
    const token = localStorage.getItem("token");
    const response = await axios.get(`${API_URL}/conversations`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return response.data;
  } catch (error) {
    handleUnauthorized(error);
  }
};

export const getMessages = async (conversationId) => {
  try {
    const token = localStorage.getItem("token");
    const response = await axios.get(
      `${API_URL}/messages/${conversationId}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    return response.data;
  } catch (error) {
    handleUnauthorized(error);
  }
};

export const deleteConversation = async (conversationId) => {
  try {
    const token = localStorage.getItem("token");
    await axios.delete(`${API_URL}/conversation/${conversationId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  } catch (error) {
    handleUnauthorized(error);
  }
};

export const renameConversation = async (conversationId, title) => {
  try {
    const token = localStorage.getItem("token");
    await axios.put(
      `${API_URL}/conversation/${conversationId}/rename`,
      { title },
      { headers: { Authorization: `Bearer ${token}` } }
    );
  } catch (error) {
    handleUnauthorized(error);
  }
};

export const uploadPdf = async (file) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("No token found");
    const formData = new FormData();
    formData.append("file", file);
    const response = await axios.post(
      `${PDF_URL}/upload`,
      formData,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    return response.data;
  } catch (error) {
    handleUnauthorized(error);
  }
};

export const getUploadedFiles = async () => {
  try {
    const token = localStorage.getItem("token");
    const response = await axios.get(`${PDF_URL}/files`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return response.data;
  } catch (error) {
    handleUnauthorized(error);
  }
};