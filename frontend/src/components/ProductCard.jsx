import React from "react";
import styles from "../styles/ProductCard.module.css";

const ProductCard = ({ product, onAddToCart }) => {
    return (
        <article className={styles.card}>
            <img
                src={product.imageUrl}
                alt={product.name}
                className={styles.cardImage}
            />
            <div className={styles.cardContent}>
                <h2 className={styles.cardTitle}>{product.name}</h2>
                <p className={styles.cardPrice}>€{product.price.toFixed(2)}</p>
                <button
                    className={styles.cardButton}
                    onClick={() => onAddToCart(product)}
                >
                    Voeg toe aan winkelmand
                </button>
            </div>
        </article>
    );
};

export default ProductCard;
