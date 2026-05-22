import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
    // Token uit auth object
    const raw = localStorage.getItem("wb_auth");
    if (raw) {
        try {
            const { token } = JSON.parse(raw);
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
                return config;
            }
        } catch {}
    }

    // fallback token
    const fallback = localStorage.getItem("token");
    if (fallback) {
        config.headers.Authorization = `Bearer ${fallback}`;
    }

    return config;
});

export default api;
