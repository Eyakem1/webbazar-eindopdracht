import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import "../styles/AuthPages.css";

export default function LoginPage(){
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try{
      await login(form.email, form.password);
      navigate("/products");
    }catch(err){
      setError("Inloggen mislukt. Controleer je gegevens.");
    }
  }

  return (
    <div className="auth-page">
      <h1>Inloggen</h1>
      <form onSubmit={submit} className="auth-form">
        <input name="email" placeholder="Email" type="email" value={form.email} onChange={(e)=>setForm({...form, email:e.target.value})} required />
        <input name="password" placeholder="Wachtwoord" type="password" value={form.password} onChange={(e)=>setForm({...form, password:e.target.value})} required />
        <button type="submit" className="btn btn-blue">Inloggen</button>
        {error && <p className="error">{error}</p>}
      </form>
      <p className="hint">Geen Account? <b>U kunt registreren</b></p>
    </div>
  );
}