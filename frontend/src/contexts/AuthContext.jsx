import { createContext, useContext, useEffect, useState } from "react";
import api from "../api/axios";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const raw = localStorage.getItem("wb_auth");
        if (raw) {
            try {
                const parsed = JSON.parse(raw);
                setUser(parsed);
            } catch {
                setUser(null);
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        const res = await api.post("/api/auth/login", { email, password });
        const { token, roles, name } = res.data;
        const payload = { email, token, roles, name };
        setUser(payload);
        localStorage.setItem("wb_auth", JSON.stringify(payload));
        return payload;
    };

    const register = async (data) => {
        const res = await api.post("/api/auth/register", data);
        return res.data;
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem("wb_auth");
    };

    const hasRole = (role) => !!user?.roles?.includes(role);

    return (
        <AuthContext.Provider
            // Profielgegevens bijwerken
            value={{ user, setUser, loading, login, register, logout, hasRole }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
