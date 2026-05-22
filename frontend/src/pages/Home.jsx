import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import "../styles/HomePage.css";
import "../styles/Products.css";
import buttonStyles from "../styles/buttons.module.css";

import readingImage from "../assets/reading.png";
import alchemistImg from "../assets/alchemist.jpg";

export default function Home() {
    const [products, setProducts] = useState([]);
    const [err, setErr] = useState("");

    useEffect(() => {
        api.get("/api/products")
            .then((res) => setProducts(Array.isArray(res.data) ? res.data : []))
            .catch(() => setErr("Kon boeken niet laden"));
    }, []);

    // Altijd 5 items tonen
    const top5 = useMemo(() => {
        if (!products || products.length === 0) return [];
        if (products.length >= 5) return products.slice(0, 5);

        const padded = [...products];
        let i = 0;
        while (padded.length < 5) {
            padded.push(products[i % products.length]);
            i++;
        }
        return padded.slice(0, 5);
    }, [products]);

    return (
        <section className="homepage">
            <div className="intro">
                <div className="intro-text">
                    <h1>Welkom bij WebBazar</h1>
                    <p>
                        Bij WebBazar koop of huur je eenvoudig jouw favoriete digitale boeken.
                        Met een persoonlijk account heb je alles binnen handbereik: na aankoop kun
                        je het boek direct downloaden en bij gehuurde titels zie je overzichtelijk
                        al jouw actieve huurorders.
                    </p>

                    <div className="cta-row">
                        <Link className="btn-cta btn-blue" to="/products">
                            Bekijk boeken
                        </Link>
                        <Link className="btn-cta btn-green" to="/contact">
                            Contact
                        </Link>
                    </div>
                </div>

                <img className="intro-image" src={readingImage} alt="Digitale boeken lezen" />
            </div>

            <div className="top5">
                <h2>Top 5 aanbevolen boeken</h2>

                {err && <p style={{ color: "crimson" }}>{err}</p>}

                <div className="top5-grid grid">
                    {top5.map((p, idx) => (
                        <div key={`${p.id}-${idx}`} className="card">
                            <img
                                src={alchemistImg}
                                alt={p.title}
                                className="card-img"
                            />

                            <div className="card-info">
                                <h3 className="card-title">{p.title}</h3>

                                {p.author && (
                                    <p className="card-author">{p.author}</p>
                                )}

                                <div className="card-prices">
                                    {typeof p.price === "number" && (
                                        <span className="price-buy">
                                            Koop: €{p.price.toFixed(2)}
                                        </span>
                                    )}
                                    {typeof p.rentalPrice === "number" && (
                                        <span className="price-rent">
                                            Huur: €{p.rentalPrice.toFixed(2)}
                                        </span>
                                    )}
                                </div>

                                <div className="card-actions">
                                    <Link
                                        to={`/products/${p.id}`}
                                        className={`${buttonStyles.btn} ${buttonStyles.blue}`}
                                    >
                                        Koop
                                    </Link>
                                    <Link
                                        to={`/products/${p.id}`}
                                        className={`${buttonStyles.btn} ${buttonStyles.green}`}
                                    >
                                        Huur
                                    </Link>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}
