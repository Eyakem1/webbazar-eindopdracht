import { useEffect, useState } from "react";
import api from "../api/axios";
import { useAuth } from "../contexts/AuthContext";
import "../styles/AuthPages.css";

export default function ProfilePage() {
    const { user, setUser } = useAuth();
    const [form, setForm] = useState({
        email: "",
        name: "",
        address: "",
        newPassword: "",
    });
    const [loading, setLoading] = useState(true);
    const [ok, setOk] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        const loadProfile = async () => {
            setLoading(true);
            setError("");
            try {
                const res = await api.get("/api/auth/me");
                setForm({
                    email: res.data.email,
                    name: res.data.name || "",
                    address: res.data.address || "",
                    newPassword: "",
                });
            } catch (err) {
                console.error("Kon profiel niet laden", err);
                setError("Kon profiel niet laden.");
            } finally {
                setLoading(false);
            }
        };

        loadProfile();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setOk("");

        try {
            await api.put("/api/auth/me", {
                name: form.name,
                address: form.address,
                newPassword: form.newPassword || null,
            });

            setOk("Gegevens opgeslagen.");
            setForm((prev) => ({ ...prev, newPassword: "" }));

            // naam bijwerken
            if (user && setUser) {
                const updatedUser = { ...user, name: form.name };
                setUser(updatedUser);
                localStorage.setItem("wb_auth", JSON.stringify(updatedUser));
            }
        } catch (err) {
            console.error("Opslaan mislukt", err);
            setError("Opslaan van gegevens is mislukt.");
        }
    };

    if (loading) {
        return (
            <div className="auth-page">
                <p>Profiel wordt geladen...</p>
            </div>
        );
    }

    return (
        <div className="auth-page">
            <h1>Mijn gegevens</h1>

            {error && <p className="form-error">{error}</p>}
            {ok && <p className="form-success">{ok}</p>}

            <form onSubmit={handleSubmit} className="auth-form">
                <label>
                    E-mailadres
                    <input
                        name="email"
                        type="email"
                        value={form.email}
                        disabled
                    />
                </label>

                <label>
                    Naam
                    <input
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                        required
                    />
                </label>

                <label>
                    Adres
                    <input
                        name="address"
                        value={form.address}
                        onChange={handleChange}
                    />
                </label>

                <label>
                    Nieuw wachtwoord
                    <input
                        name="newPassword"
                        type="password"
                        value={form.newPassword}
                        onChange={handleChange}
                        placeholder="Laat leeg om wachtwoord niet te wijzigen"
                    />
                </label>

                <button type="submit" className="btn btn-green">
                    Gegevens opslaan
                </button>
            </form>
        </div>
    );
}
