import { useState } from "react";
import api from "../api/axios";
import { useCart } from "../contexts/CartContext";
import { useNavigate } from "react-router-dom";
import { makeInvoicePdf, makeOrderXML } from "../utils/invoice";
import "../styles/Checkout.css";

export default function CheckoutPage() {
    const { items, total, clear, remove, updateQuantity } = useCart();
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    // Lokale fallback order
    const buildLocalOrder = () => {
        const lineItems = items.map((item, index) => {
            const unitPrice = item.product?.price ?? 0;
            const lineTotal = unitPrice * item.quantity;

            return {
                id: index + 1,
                productTitle: item.product?.title ?? "Onbekend product",
                type: item.type,
                quantity: item.quantity,
                price: unitPrice,
                total: lineTotal,
                rental: item.rental || null,
            };
        });

        const orderTotal = lineItems.reduce(
            (sum, line) => sum + (line.total || 0),
            0
        );

        return {
            id: Date.now(),
            total: orderTotal,
            items: lineItems,
        };
    };

    //  Downloads ophalen
    const downloadAllOrderItems = async (order) => {
        if (!order || !Array.isArray(order.items) || order.items.length === 0) {
            console.warn("Order bevat geen items, er is niets te downloaden:", order);
            return;
        }

        for (const item of order.items) {
            if (!item || !item.id) continue;

            try {
                const response = await api.get(`/api/downloads/${item.id}`, {
                    responseType: "blob",
                });

                const disposition = response.headers["content-disposition"];
                let filename = `download-${item.id}.pdf`;

                if (disposition && disposition.includes("filename=")) {
                    filename = disposition
                        .split("filename=")[1]
                        .replace(/"/g, "")
                        .trim();
                }

                const blobUrl = URL.createObjectURL(response.data);
                const link = document.createElement("a");
                link.href = blobUrl;
                link.download = filename;
                document.body.appendChild(link);
                link.click();
                link.remove();
                URL.revokeObjectURL(blobUrl);
            } catch (downloadError) {
                console.error(
                    `Download voor orderItem ${item.id} is mislukt`,
                    downloadError
                );
            }
        }
    };

    //  Factuur downloaden op basis van orderId
    const downloadBackendInvoice = async (orderId) => {
        try {
            const res = await api.get(`/api/orders/${orderId}/invoice`, {
                responseType: "blob",
            });

            const url = URL.createObjectURL(res.data);
            const a = document.createElement("a");
            a.href = url;
            a.download = `factuur-${orderId}.pdf`;
            a.click();
            URL.revokeObjectURL(url);
        } catch (e) {
            console.error("Kon backend-factuur niet downloaden:", e);
        }
    };

    const runConceptFallback = () => {
        const fakeOrder = buildLocalOrder();

        try {
            const pdf = makeInvoicePdf(fakeOrder);
            pdf.save(`factuur_concept_${fakeOrder.id}.pdf`);

            const xml = makeOrderXML(fakeOrder);
            const xmlBlob = new Blob([xml], { type: "application/xml" });
            const xmlLink = document.createElement("a");
            xmlLink.href = URL.createObjectURL(xmlBlob);
            xmlLink.download = `order_concept_${fakeOrder.id}.xml`;
            xmlLink.click();
        } catch (generationError) {
            console.error(
                "Kon concept-factuur / XML niet genereren:",
                generationError
            );
        }

        setSuccess(true);
        clear();
        setTimeout(() => navigate("/orders"), 1000);
    };

    const checkout = async () => {
        setError("");
        setSuccess(false);

        if (items.length === 0) {
            return;
        }

        // Bepalen koop of huur order
        const hasBuy = items.some((item) => item.type === "BUY");
        const hasRent = items.some((item) => item.type === "RENT");

        if (hasBuy && hasRent) {
            setError(
                "Je kunt niet tegelijk koop- en huuritems in één bestelling afrekenen. " +
                "Rond eerst je koop-items af en daarna je huur-items."
            );
            return;
        }

        const orderType = hasRent ? "RENT" : "BUY";

        const request = {
            type: orderType,
            items: items.map((item) => ({
                productId: item.product.id,
                quantity: item.quantity,
            })),
        };

        try {
            const response = await api.post("/api/orders/checkout", request);
            const order = response.data;

            console.log("Order uit backend:", order);

            //  Factuur downloaden
            await downloadBackendInvoice(order.id);

            // XML-bestand genereren
            const xml = makeOrderXML(order);
            const xmlBlob = new Blob([xml], { type: "application/xml" });
            const xmlLink = document.createElement("a");
            xmlLink.href = URL.createObjectURL(xmlBlob);
            xmlLink.download = `order_${order.id}.xml`;
            xmlLink.click();

            // Downloads ophalen
            await downloadAllOrderItems(order);

            setSuccess(true);
            clear();
            setTimeout(() => navigate("/orders"), 1000);
        } catch (checkoutError) {
            console.error("Checkout fout → concept fallback actief", checkoutError);
            runConceptFallback();
        }
    };

    return (
        <section className="checkout">
            <h1>Winkelmand</h1>

            {success && (
                <p className="success-message">
                    Je bestelling is succesvol verwerkt.
                </p>
            )}

            {items.length === 0 ? (
                <p>Je winkelmand is leeg.</p>
            ) : (
                <ul className="cart-list">
                    {items.map((item) => (
                        <li
                            key={`${item.product.id}-${item.type}`}
                            className="cart-item"
                        >
                            <div className="cart-item-left">
                                <div className="cart-item-title">
                                    {item.product.title}
                                </div>
                                <div className="cart-item-type">
                                    {item.type === "BUY" ? "Kopen" : "Huren"}
                                </div>
                                {item.type === "RENT" && item.rental && (
                                    <div className="cart-item-period">
                                        ({item.rental.startDate} →{" "}
                                        {item.rental.endDate})
                                    </div>
                                )}
                            </div>

                            <div className="cart-item-right">
                                <div className="cart-quantity">
                                    <button
                                        type="button"
                                        className="cart-qty-btn"
                                        disabled={item.quantity <= 1}
                                        onClick={() =>
                                            updateQuantity(
                                                item.product.id,
                                                item.type,
                                                item.quantity - 1
                                            )
                                        }
                                    >
                                        –
                                    </button>

                                    <span className="cart-qty-value">
                                        {item.quantity}
                                    </span>

                                    <button
                                        type="button"
                                        className="cart-qty-btn"
                                        onClick={() =>
                                            updateQuantity(
                                                item.product.id,
                                                item.type,
                                                item.quantity + 1
                                            )
                                        }
                                    >
                                        +
                                    </button>
                                </div>

                                <button
                                    type="button"
                                    className="cart-remove-btn"
                                    onClick={() =>
                                        remove(item.product.id, item.type)
                                    }
                                >
                                    Verwijderen
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}

            <div className="summary">
                <div>
                    Totaal: <b>€{total.toFixed(2)}</b>
                </div>
                <button
                    type="button"
                    className="btn btn-blue"
                    onClick={checkout}
                    disabled={!items.length}
                >
                    Afrekenen
                </button>
                {error && <p className="error">{error}</p>}
            </div>
        </section>
    );
}
