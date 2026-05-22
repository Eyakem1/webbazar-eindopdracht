import React from "react";
import ProductItem from "../components/ProductItem";
import "../styles/HomePage.css";
import alchemistImg from "../assets/alchemist.jpg";


const topBooks = [
    {
        id: 1,
        name: "De Alchemist",
        price: 9.99,
        imageUrl: alchemistImg,
    },
    {
        id: 2,
        name: "Sapiens",
        price: 12.49,
        imageUrl: alchemistImg,
    },
    {
        id: 3,
        name: "De Zeven Zussen",
        price: 8.75,
        imageUrl: alchemistImg,
    },
    {
        id: 4,
        name: "Think and Grow Rich",
        price: 11.99,
        imageUrl: alchemistImg,
    },
    {
        id: 5,
        name: "Atomic Habits",
        price: 10.5,
        imageUrl: alchemistImg,
    },
];

const HomePage = () => {
    return (
        <div className="home-page">
            <h1 className="home-title">
                Welkom bij <span>WebBazar 📚</span>
            </h1>
            <p className="home-subtitle">Top 5 aanbevolen boeken voor jou:</p>

            <div className="book-grid">
                {topBooks.map((book) => (
                    <ProductItem key={book.id} product={book} />
                ))}
            </div>
        </div>
    );
};

export default HomePage;
