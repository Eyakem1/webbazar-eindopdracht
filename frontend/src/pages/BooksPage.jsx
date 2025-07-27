import React, { useEffect, useState } from "react";
import axios from "axios";
import ProductItem from "../components/ProductItem";
import "../styles/BooksPage.css";

const BooksPage = () => {
    const [books, setBooks] = useState([]);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    useEffect(() => {
        axios
            .get("http://localhost:8080/api/products")
            .then((res) => setBooks(res.data))
            .catch(() => setError("Boeken konden niet worden geladen"));
    }, []);

    const handleOrder = async (productId) => {
        const token = localStorage.getItem("token");
        if (!token) {
            setMessage("Je moet eerst inloggen om te bestellen.");
            return;
        }

        try {
            await axios.post(
                "http://localhost:8080/api/orders",
                { productId },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            setMessage("Boek succesvol besteld!");
        } catch (err) {
            setMessage("Bestelling mislukt. Probeer opnieuw.");
        }
    };

    return (
        <div className="books-page">
            <h1>📚 Alle boeken</h1>
            {error && <p className="error">{error}</p>}
            {message && <p className="message">{message}</p>}
            <div className="book-grid">
                {books.map((book) => (
                    <div className="book-card" key={book.id}>
                        <ProductItem
                            product={{
                                name: book.title,
                                price: book.price,
                                imageUrl: book.imageUrl || "https://via.placeholder.com/150",
                            }}
                        />
                        <button className="order-button" onClick={() => handleOrder(book.id)}>
                            Bestellen
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default BooksPage;
