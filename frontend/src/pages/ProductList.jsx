import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import api from "../api/axios";
import "../styles/Products.css";
import alchemistImg from "../assets/alchemist.jpg";

export default function ProductList() {
    const [products, setProducts] = useState([]);
    const [searchParams] = useSearchParams();

    // zoektekst
    const q = (searchParams.get("q") || "").trim().toLowerCase();

    useEffect(() => {
        api.get("/api/products")
            .then((res) => setProducts(Array.isArray(res.data) ? res.data : []))
            .catch(() => {
                /* geen UI-spam bij fout */
            });
    }, []);

    // filter op titel en auteur
    const filtered = useMemo(() => {
        if (!q) return products;
        return products.filter((p) =>
            [p.title, p.author].join(" ").toLowerCase().includes(q)
        );
    }, [q, products]);

    //  Altijd  20 items tonen 4 rijen van 5, opvullen met duplicaten
    const twenty = useMemo(() => {
        const desired = 20;
        if (!filtered || filtered.length === 0) return [];
        return Array.from({ length: desired }, (_, i) => filtered[i % filtered.length]);
    }, [filtered]);

    return (
        <section className="products-page">
            <h1>Boeken</h1>


            <div
                className="grid"
                style={{ gridTemplateColumns: "repeat(5, minmax(180px, 1fr))" }}
            >
                {twenty.map((p, idx) => (
                    <div key={`${p.id}-${idx}`} className="card">
                        <img
                            src={p.imageUrl || alchemistImg}
                            alt={p.title}
                            className="card-img"
                        />

                        <div className="card-info">
                            <h3 className="card-title">{p.title}</h3>
                            {p.author && <p className="card-author">{p.author}</p>}

                            <div className="card-prices">
                                {typeof p.price === "number" && (
                                    <span className="price-buy">Koop: €{p.price.toFixed(2)}</span>
                                )}
                                {typeof p.rentalPrice === "number" && (
                                    <span className="price-rent">
                                        Huur: €{p.rentalPrice.toFixed(2)}
                                    </span>
                                )}
                            </div>

                            <div className="card-actions">
                                <Link to={`/products/${p.id}`} className="btn btn-blue small-btn">
                                    Koop
                                </Link>
                                <Link to={`/products/${p.id}`} className="btn btn-green small-btn">
                                    Huur
                                </Link>
                            </div>
                        </div>
                    </div>
                ))}

                {twenty.length === 0 && (
                    <p
                        style={{
                            gridColumn: "1 / -1",
                            textAlign: "center",
                            margin: "24px 0",
                        }}
                    >
                        Geen boeken gevonden.
                    </p>
                )}
            </div>
        </section>
    );
}
