import { useEffect, useState } from "react";
import api from "../../api/axios";
import "../../styles/Admin.css";

const emptyForm = {
    title: "",
    author: "",
    description: "",
    price: "",
    rentalPrice: "",
};

export default function AdminProducts() {
    const [products, setProducts] = useState([]);
    const [form, setForm] = useState(emptyForm);
    const [file, setFile] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");

    const loadProducts = async () => {
        try {
            const response = await api.get("/api/products");
            setProducts(response.data || []);
        } catch (err) {
            console.error("Failed to load products", err);
        }
    };

    useEffect(() => {
        loadProducts();
    }, []);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((previous) => ({
            ...previous,
            [name]: value,
        }));
    };

    const handleFileChange = (event) => {
        const selectedFile = event.target.files?.[0] || null;
        setFile(selectedFile);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");

        // Prijsvalidatie
        const numericPrice = Number(form.price);
        const numericRentalPrice = Number(form.rentalPrice);

        if (Number.isNaN(numericPrice)) {
            setError("Prijs moet een geldig getal zijn.");
            return;
        }

        if (numericPrice < 0) {
            setError("Prijs mag niet lager zijn dan €0,00.");
            return;
        }

        if (Number.isNaN(numericRentalPrice)) {
            setError("Huurprijs moet een geldig getal zijn.");
            return;
        }

        if (numericRentalPrice < 0) {
            setError("Huurprijs mag niet lager zijn dan €0,00.");
            return;
        }

        setIsSubmitting(true);

        try {
            const product = {
                title: form.title.trim(),
                author: form.author.trim(),
                description: form.description.trim(),
                price: numericPrice,
                rentalPrice: numericRentalPrice,
            };

            const formData = new FormData();

            formData.append(
                "product",
                new Blob([JSON.stringify(product)], {
                    type: "application/json",
                })
            );

            if (file) {
                formData.append("file", file);
            }

            await api.post("/api/products", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });

            setForm(emptyForm);
            setFile(null);
            await loadProducts();
        } catch (err) {
            console.error("Failed to create product", err);
            setError(
                "Product toevoegen mislukt. Probeer het later nog een keer."
            );
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleRemove = async (id) => {
        try {
            await api.delete(`/api/products/${id}`);
            await loadProducts();
        } catch (err) {
            console.error("Failed to delete product", err);
            setError(
                "Verwijderen is mislukt. Controleer de console voor meer informatie."
            );
        }
    };

    return (
        <main className="admin-page">
            <header className="admin-header">
                <h1 className="admin-title">Productbeheer</h1>
                <p className="admin-subtitle">
                    Voeg nieuwe producten toe of beheer bestaande items.
                </p>
            </header>

            <section className="admin-section">
                <h2 className="admin-section-title">Nieuw product</h2>

                <form
                    className="admin-form"
                    onSubmit={handleSubmit}
                    encType="multipart/form-data"
                >
                    <div className="admin-form-grid">
                        <div className="admin-form-group">
                            <label htmlFor="title">Titel</label>
                            <input
                                id="title"
                                name="title"
                                type="text"
                                value={form.title}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="admin-form-group">
                            <label htmlFor="author">Auteur</label>
                            <input
                                id="author"
                                name="author"
                                type="text"
                                value={form.author}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="admin-form-group admin-form-group-full">
                            <label htmlFor="description">Beschrijving</label>
                            <textarea
                                id="description"
                                name="description"
                                rows={3}
                                value={form.description}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="admin-form-group">
                            <label htmlFor="price">Prijs (kopen)</label>
                            <input
                                id="price"
                                name="price"
                                type="number"
                                min="0"
                                step="0.01"
                                value={form.price}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="admin-form-group">
                            <label htmlFor="rentalPrice">Huurprijs</label>
                            <input
                                id="rentalPrice"
                                name="rentalPrice"
                                type="number"
                                min="0"
                                step="0.01"
                                value={form.rentalPrice}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="admin-form-group admin-form-group-full">
                            <label htmlFor="file">
                                Bestand (pdf/png, optioneel)
                            </label>
                            <input
                                id="file"
                                name="file"
                                type="file"
                                onChange={handleFileChange}
                            />
                        </div>
                    </div>

                    {error && <p className="form-error">{error}</p>}

                    <button
                        className="btn btn-green"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? "Bezig met opslaan..."
                            : "Product toevoegen"}
                    </button>
                </form>
            </section>

            <section className="admin-section">
                <h2 className="admin-section-title">Bestaande producten</h2>

                {products.length === 0 ? (
                    <p>Er zijn nog geen producten toegevoegd.</p>
                ) : (
                    <ul className="admin-list">
                        {products.map((product) => (
                            <li
                                key={product.id}
                                className="admin-list-item"
                            >
                                <div className="admin-list-content">
                                    <h3 className="product-title">
                                        {product.title}
                                    </h3>
                                    <p className="product-meta">
                                        {product.author && (
                                            <span className="product-author">
                                                {product.author}
                                            </span>
                                        )}
                                        <span className="product-price">
                                            €{product.price?.toFixed(2)} kopen /
                                            €
                                            {product.rentalPrice?.toFixed(
                                                2
                                            )}{" "}
                                            huren
                                        </span>
                                    </p>
                                    {product.description && (
                                        <p className="product-description">
                                            {product.description}
                                        </p>
                                    )}
                                </div>
                                <div className="admin-list-actions">
                                    <button
                                        type="button"
                                        className="btn btn-red"
                                        onClick={() =>
                                            handleRemove(product.id)
                                        }
                                    >
                                        Verwijderen
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </main>
    );
}
