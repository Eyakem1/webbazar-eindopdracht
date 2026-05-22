import { useEffect, useState } from "react";
import api from "../api/axios";
import "../styles/Orders.css";
import buttonStyles from "../styles/buttons.module.css";
import { makeInvoicePdf } from "../utils/invoice";

export default function Orders() {
    const [orders, setOrders] = useState([]);
    const [err, setErr] = useState("");

    useEffect(() => {
        api.get("/api/orders")
            .then(res => setOrders(res.data || []))
            .catch(e => setErr(e?.response?.data?.message || "Kon bestellingen niet laden."));
    }, []);

    const displayStatus = (status) => {
        if (!status) return "-";
        if (status.toUpperCase() === "PENDING") return "Betaald";
        return status;
    };

    const statusClass = (status) => {
        if (!status) return "";
        const upper = status.toUpperCase();
        if (upper === "PENDING") return "paid";
        if (upper === "CANCELLED") return "cancelled";
        return "pending";
    };

    if (err) return <p className="orders-error">{err}</p>;

    return (
        <section className="orders">
            <h1>Mijn bestellingen</h1>

            {!orders.length ? (
                <p className="orders-empty">Je hebt nog geen bestellingen.</p>
            ) : (
                orders.map((o) => {
                    const items = o.items || o.orderItems || [];

                    const buyItems = items.filter(it => it.type === "BUY");
                    const rentItems = items.filter(it => it.type === "RENT");
                    const otherItems = items.filter(
                        it => it.type !== "BUY" && it.type !== "RENT"
                    );

                    return (
                        <div className="order-card" key={o.id}>
                            <div className="head">
                                <h3>Bestelling #{o.id}</h3>

                                <div className="head-meta">
                                    {o.createdAt && (
                                        <span className="order-date">
                                            {new Date(o.createdAt).toLocaleString()}
                                        </span>
                                    )}
                                    <span className={`status ${statusClass(o.status)}`}>
                                        {displayStatus(o.status)}
                                    </span>
                                </div>
                            </div>

                            {buyItems.length > 0 && (
                                <>
                                    <h4 className="order-subtitle">Gekochte boeken</h4>
                                    <ul className="order-items">
                                        {buyItems.map((it) => (
                                            <li key={it.id}>
                                                <div className="order-item-info">
                                                    <span className="order-item-title">
                                                        {(it.productTitle || it.product?.title || "Product")}
                                                    </span>
                                                    <span className="order-item-meta">
                                                        x{it.quantity} (kopen)
                                                    </span>
                                                </div>

                                                {it.downloadable && (
                                                    <button
                                                        type="button"
                                                        className={`${buttonStyles.btn} ${buttonStyles.blue} order-download-btn`}
                                                        onClick={() => downloadItem(it.id)}
                                                    >
                                                        Download
                                                    </button>
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                </>
                            )}

                            {rentItems.length > 0 && (
                                <>
                                    <h4 className="order-subtitle">Gehuurde boeken</h4>
                                    <ul className="order-items">
                                        {rentItems.map((it) => (
                                            <li key={it.id}>
                                                <div className="order-item-info">
                                                    <span className="order-item-title">
                                                        {(it.productTitle || it.product?.title || "Product")}
                                                    </span>
                                                    <span className="order-item-meta">
                                                        x{it.quantity} (huren)
                                                    </span>
                                                </div>

                                                {it.downloadable && (
                                                    <button
                                                        type="button"
                                                        className={`${buttonStyles.btn} ${buttonStyles.blue} order-download-btn`}
                                                        onClick={() => downloadItem(it.id)}
                                                    >
                                                        Download
                                                    </button>
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                </>
                            )}

                            {otherItems.length > 0 && (
                                <>
                                    <h4 className="order-subtitle">Overige items</h4>
                                    <ul className="order-items">
                                        {otherItems.map((it) => (
                                            <li key={it.id}>
                                                <div className="order-item-info">
                                                    <span className="order-item-title">
                                                        {(it.productTitle || it.product?.title || "Product")}
                                                    </span>
                                                    <span className="order-item-meta">
                                                        x{it.quantity} ({it.type})
                                                    </span>
                                                </div>

                                                {it.downloadable && (
                                                    <button
                                                        type="button"
                                                        className={`${buttonStyles.btn} ${buttonStyles.blue} order-download-btn`}
                                                        onClick={() => downloadItem(it.id)}
                                                    >
                                                        Download
                                                    </button>
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                </>
                            )}

                            <div className="order-total">
                                Totaal: €{(o.total ?? 0).toFixed(2)}
                            </div>

                            <div
                                className="order-actions"
                                style={{ display: "flex", gap: "8px", marginTop: "8px" }}
                            >
                                <button
                                    type="button"
                                    className={`${buttonStyles.btn} ${buttonStyles.green}`}
                                    onClick={() => downloadInvoice(o.id)}
                                >
                                    Factuur
                                </button>

                                <button
                                    type="button"
                                    className={`${buttonStyles.btn} ${buttonStyles.blue}`}
                                    onClick={() => generateNicePdf(o)}
                                >
                                    Factuur detail
                                </button>
                            </div>
                        </div>
                    );
                })
            )}
        </section>
    );

    async function downloadItem(orderItemId) {
        try {
            const res = await api.get(`/api/downloads/${orderItemId}`, { responseType: "blob" });
            const filename =
                res.headers["content-disposition"]?.split("filename=")[1]?.replace(/"/g, "") ||
                "download.bin";
            const url = URL.createObjectURL(res.data);
            const a = document.createElement("a");
            a.href = url;
            a.download = filename;
            a.click();
            URL.revokeObjectURL(url);
        } catch (e) {
            alert("Download niet beschikbaar (huurperiode?).");
        }
    }

    async function downloadInvoice(orderId) {
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
            alert("Kon factuur niet downloaden.");
        }
    }

    function generateNicePdf(order) {
        try {
            const pdf = makeInvoicePdf(order);
            pdf.save(`Factuur detail ${order.id}.pdf`);
        } catch (e) {
            console.error("Kon mooie bon niet genereren:", e);
        }
    }
}
