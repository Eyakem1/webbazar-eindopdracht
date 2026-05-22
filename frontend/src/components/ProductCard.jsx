import Button from "./Button";
import { useCart } from "../contexts/CartContext";

export default function ProductCard({ product }){
  const { add } = useCart();
  return (
    <div className="card" style={{width:320, display:"flex", flexDirection:"column", gap:8}}>
      <div>
        <h3>{product.title}</h3>
        <p style={{opacity:.7}}>Auteur: {product.author||'-'}</p>
        <p style={{fontSize:'.95rem'}}>{product.description||'—'}</p>
      </div>
      <div style={{display:"flex", justifyContent:"space-between", alignItems:"center", gap:8}}>
        <div style={{fontWeight:600}}>Koop: €{Number(product.price||0).toFixed(2)} | Huur: €{Number(product.rentalPrice||0).toFixed(2)}</div>
        <div style={{display:"flex", gap:8}}>
          <Button kind="buy" onClick={()=>add(product,'BUY')}>Kopen</Button>
          <Button kind="rent" onClick={()=>add(product,'RENT',{startDate:new Date().toISOString().slice(0,10)})}>Huren</Button>
        </div>
      </div>
    </div>
  );
}
