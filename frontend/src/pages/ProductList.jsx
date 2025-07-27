import ProductCard from "../components/ProductCard";

function ProductList() {
  const books = [
    // ✅ ... je boekenlijst is prima
    {
      id: 1,
      title: "React voor Starters",
      author: "Jan de Code",
      price: 13.99,
      rentalPrice: 3.49,
      image: "https://m.media-amazon.com/images/I/41+eK8zBwQL._SY445_SX342_.jpg",
    },
    // ... de rest van je boeken
    {
      id: 10,
      title: "Secure Coding",
      author: "Eve Encrypt",
      price: 23.5,
      rentalPrice: 5.49,
      image: "https://m.media-amazon.com/images/I/41+eK8zBwQL._SY445_SX342_.jpg",
    },
  ];

  return (
      <div className="product-list-container" style={{ padding: "1.5rem" }}>
        <h2 className="product-list-heading" style={{ color: "#0057b8", marginBottom: "1rem" }}>
          📚 Boekenoverzicht
        </h2>
        <div className="product-grid" style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
          gap: "1.5rem"
        }}>
          {books.map((book) => (
              <ProductCard key={book.id} book={book} />
          ))}
        </div>
      </div>
  );
}

export default ProductList;
