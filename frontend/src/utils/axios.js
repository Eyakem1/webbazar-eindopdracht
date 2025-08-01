// src/utils/axios.js
import axios from "axios";

const instance = axios.create({
    baseURL: "http://localhost:8080", // Vervang dit met jouw backend URL als anders
});

instance.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default instance;
