import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import "../styles/AuthPages.css";

export default function RegisterPage(){
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "", name: "", address: "" });
  const [ok, setOk] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    const payload = { ...form };
    try{
      await register(payload);
      setOk("Account aangemaakt! Je kunt nu inloggen.");
      setTimeout(()=>navigate("/login"), 1000);
    }catch{
      setOk("Registratie mislukt.");
    }
  }

  return (
    <div className="auth-page">
      <h1>Registreren</h1>
      <form onSubmit={submit} className="auth-form">
        <input name="name" placeholder="Naam" value={form.name} onChange={(e)=>setForm({...form, name:e.target.value})} required />
        <input name="address" placeholder="Adres" value={form.address} onChange={(e)=>setForm({...form, address:e.target.value})} required />
        <input name="email" placeholder="Email" type="email" value={form.email} onChange={(e)=>setForm({...form, email:e.target.value})} required />
        <input name="password" placeholder="Wachtwoord" type="password" value={form.password} onChange={(e)=>setForm({...form, password:e.target.value})} required />
        <button type="submit" className="btn btn-green">Account maken</button>
      </form>
      {ok && <p>{ok}</p>}
    </div>
  );
}