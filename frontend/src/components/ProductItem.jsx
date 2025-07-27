import React from "react";
import axios from "axios";
import "../styles/ProductItem.css";

const ProductItem = ({ product }) => {
    const handleOrder = async (type) => {
        const token = localStorage.getItem("token");
        if (!token) {
            alert("Je moet eerst inloggen om een bestelling te plaatsen.");
            return;
        }

        try {
            const response = await axios.post(
                "http://localhost:8080/api/orders",
                {
                    type,
                    productIds: [product.id], // ✅ correcte structuur!
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json"
                    },
                }
            );

            alert(`Boek succesvol ${type === "koop" ? "gekocht" : "gehuurd"}!`);
            console.log("Bestelling:", response.data);
        } catch (error) {
            console.error("Fout bij bestelling:", error);
            alert("Er ging iets mis bij het plaatsen van je bestelling.");
        }
    };

    return (
        <div className="product-item">
            <img
                src={product.imageUrl}
                alt={product.name}
                className="product-item__image"
            />
            <h3 className="product-item__title">{product.name}</h3>
            <p className="product-item__price">€ {product.price.toFixed(2)}</p>
            <div className="product-item__actions">
                <button className="koop" onClick={() => handleOrder("koop")}>
                    Koop
                </button>
                <button className="huur" onClick={() => handleOrder("huur")}>
                    Huur
                </button>
            </div>
        </div>
    );
};

export default ProductItem;
