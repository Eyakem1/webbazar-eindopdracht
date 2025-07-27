import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../styles/AuthPages.css";

const RegisterPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        naam: "",
        email: "",
        wachtwoord: "",
    });

    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleChange = (e) => {
        setFormData((prev) => ({
            ...prev,
            [e.target.name]: e.target.value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        try {
            const res = await axios.post("http://localhost:8080/auth/register", formData);
            setMessage("Registratie gelukt! Je kunt nu inloggen.");
            setTimeout(() => navigate("/login"), 2000);
        } catch (err) {
            console.error(err);
            setError("Registratie mislukt. Probeer het opnieuw.");
        }
    };

    return (
        <div className="auth-page">
            <h2>Maak een account aan</h2>
            <form onSubmit={handleSubmit} className="auth-form">
                <input
                    type="text"
                    name="naam"
                    placeholder="Naam"
                    value={formData.naam}
                    onChange={handleChange}
                    required
                />
                <input
                    type="email"
                    name="email"
                    placeholder="E-mailadres"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />
                <input
                    type="password"
                    name="wachtwoord"
                    placeholder="Wachtwoord"
                    value={formData.wachtwoord}
                    onChange={handleChange}
                    required
                />
                <button type="submit">Registreren</button>
            </form>
            {message && <p className="success-msg">{message}</p>}
            {error && <p className="error-msg">{error}</p>}
        </div>
    );
};

export default RegisterPage;
