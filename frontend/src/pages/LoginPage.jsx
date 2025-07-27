import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../styles/AuthPages.css";

const LoginPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const response = await axios.post("http://localhost:8080/auth/login", form, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      const token = response.data.token;
      localStorage.setItem("token", token);
      navigate("/dashboard");
    } catch (err) {
      setError("Inloggen mislukt. Controleer je gegevens.");
    }
  };

  return (
      <div className="auth-page">
        <h2>Inloggen</h2>
        <form onSubmit={handleSubmit}>
          <input
              type="email"
              name="email"
              placeholder="E-mailadres"
              value={form.email}
              onChange={handleChange}
              required
          />
          <input
              type="password"
              name="password"
              placeholder="Wachtwoord"
              value={form.password}
              onChange={handleChange}
              required
          />
          <button type="submit">Inloggen</button>
          {error && <p className="error">{error}</p>}
        </form>
      </div>
  );
};

export default LoginPage;
