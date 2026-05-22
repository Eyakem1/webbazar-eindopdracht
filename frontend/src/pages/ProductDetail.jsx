import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios";
import { useCart } from "../contexts/CartContext";
import "../styles/ProductDetail.css";

export default function ProductDetail(){
    const { id } = useParams();
    const [p, setP] = useState(null);
    const { add } = useCart();
    const [rental, setRental] = useState({ startDate: "", endDate: "" });

    useEffect(()=>{
        api.get(`/api/products/${id}`)
            .then(res=>setP(res.data))
            .catch(()=>{});
    },[id]);

    if (!p) return <div style={{padding:24}}>Laden...</div>;

    const addBuy  = () => add(p, { type:"BUY",  quantity:1 });
    const addRent = () => add(p, { type:"RENT", quantity:1, rental });

    return (
        <section className="product-detail">
            <h1>{p.title}</h1>
            <p className="author">{p.author}</p>
            <p className="desc">{p.description}</p>

            <div className="prices">
                <div className="price-line">
                    <span className="label">Koopprijs</span>
                    <span className="value">€{p.price?.toFixed(2)}</span>
                </div>
                <div className="price-line">
                    <span className="label">Huurprijs</span>
                    <span className="value">€{p.rentalPrice?.toFixed(2)} / periode</span>
                </div>
            </div>

            <div className="actions">
                <button className="btn btn-blue" onClick={addBuy}>
                    In winkelmand (kopen)
                </button>
            </div>

            <div className="rental">
                <h3>Huren</h3>
                <div className="rental-row">
                    <label>
                        Start
                        <input
                            type="date"
                            value={rental.startDate}
                            onChange={(e)=>setRental({...rental, startDate:e.target.value})}
                        />
                    </label>
                    <label>
                        Eind
                        <input
                            type="date"
                            value={rental.endDate}
                            onChange={(e)=>setRental({...rental, endDate:e.target.value})}
                        />
                    </label>

                    <button
                        className="btn btn-green"
                        onClick={addRent}
                        disabled={!rental.startDate || !rental.endDate}
                    >
                        Huren &amp; toevoegen
                    </button>
                </div>
            </div>
        </section>
    );
}
