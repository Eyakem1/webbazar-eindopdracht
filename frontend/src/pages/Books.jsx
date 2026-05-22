import { useEffect, useMemo, useState } from "react";
import api from "../utils/axios";
import ProductCard from "../components/ProductCard";

export default function Books(){
  const [loading,setLoading]=useState(true);
  const [err,setErr]=useState("");
  const [list,setList]=useState([]);
  const [q,setQ]=useState("");

  useEffect(()=>{ setLoading(true);
    api.get("/api/products").then(r=>setList(r.data||[])).catch(e=>setErr(e?.response?.data?.message||"Kon producten niet laden")).finally(()=>setLoading(false));
  },[]);

  const filtered=useMemo(()=>{
    const t=q.trim().toLowerCase(); if(!t) return list;
    return list.filter(p=>[p.title,p.author].join(" ").toLowerCase().includes(t));
  },[q,list]);

  if(loading) return <p>Laden…</p>;
  if(err) return <p style={{color:"crimson"}}>{err}</p>;

  return (
    <section>
      <h1>Boeken</h1>
      <div style={{display:"flex", gap:8, alignItems:"center", margin:"8px 0"}}>
        <input value={q} onChange={e=>setQ(e.target.value)} placeholder="Zoek titel/auteur..." />
        <button onClick={()=>setQ("")}>Reset</button>
      </div>
      <div className="grid">
        {filtered.map(p=><ProductCard key={p.id} product={p} />)}
      </div>
    </section>
  );
}
