import React from "react";
import ProductItem from "../components/ProductItem";
import "../styles/HomePage.css";

const topBooks = [
  {
    id: 1,
    name: "De Alchemist",
    price: 9.99,
    imageUrl: "https://images.unsplash.com/photo-1529655683826-aba9b3e77383?fit=crop&w=300&q=80"
  },
  {
    id: 2,
    name: "Sapiens",
    price: 12.49,
    imageUrl: "https://images.unsplash.com/photo-1532012197267-da84d127e765?fit=crop&w=300&q=80"
  },
  {
    id: 3,
    name: "De Zeven Zussen",
    price: 8.75,
    imageUrl: "https://images.unsplash.com/photo-1512820790803-83ca734da794?fit=crop&w=300&q=80"
  },
  {
    id: 4,
    name: "Think and Grow Rich",
    price: 11.99,
    imageUrl: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?fit=crop&w=300&q=80"
  },
  {
    id: 5,
    name: "Atomic Habits",
    price: 10.50,
    imageUrl: "https://images.unsplash.com/photo-1553729459-efe14ef6055d?fit=crop&w=300&q=80"
  }
];

const HomePage = () => {
  return (
      <div className="home-page">
        <h1 className="home-title">Welkom bij <span>WebBazar 📚</span></h1>
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
