    import { useEffect, useState } from "react";
    import api from "../../api/axios";
    import "../../styles/Admin.css";
    import buttonStyles from "../../styles/buttons.module.css";
    import { makeInvoicePdf } from "../../utils/invoice";

    export default function AdminOrders() {
        const [orders, setOrders] = useState([]);

        useEffect(() => {
            api.get("/api/orders")
                .then(res => setOrders(res.data || []))
                .catch(err => console.error("Fout bij ophalen bestellingen", err));
        }, []);

        const displayStatus = (status) => {
            if (!status) return "-";
            if (status.toUpperCase() === "PENDING") return "Betaald";
            return status;
        };

        return (
            <section className="admin">
                <h1>Alle bestellingen</h1>

                <table className="tbl">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Datum</th>
                        <th>Klant</th>
                        <th>E-mail</th>
                        <th>Status</th>
                        <th>Totaal</th>
                        <th># items</th>
                        <th>Boeken</th>
                        <th>Acties</th>
                    </tr>
                    </thead>

                    <tbody>
                    {orders.map(o => {
                        const customerName =
                            o.customerName ||
                            o.userName ||
                            (o.user && o.user.name) ||
                            "Onbekend";

                        const customerEmail =
                            o.customerEmail ||
                            o.userEmail ||
                            (o.user && o.user.email) ||
                            "Onbekend";

                        const dateValue = o.createdAt || o.orderDate;
                        const formattedDate = dateValue
                            ? new Date(dateValue).toLocaleString()
                            : "-";

                        const items = o.items || o.orderItems || [];

                        const totalQuantity =
                            items.reduce((sum, it) => sum + (it.quantity || 1), 0);

                        const bookTitles = items.map(it => {
                            const title =
                                it.productTitle ||
                                (it.product && it.product.title) ||
                                it.title ||
                                it.name ||
                                "Onbekend boek";

                            const qty = it.quantity || 1;

                            return `${title} (x${qty})`;
                        }).join(", ");

                        return (
                            <tr key={o.id}>
                                <td>{o.id}</td>
                                <td>{formattedDate}</td>
                                <td>{customerName}</td>
                                <td>{customerEmail}</td>
                                <td>{displayStatus(o.status)}</td>
                                <td>
                                    {typeof o.total === "number" ? `€${o.total.toFixed(2)}` : "-"}
                                </td>
                                <td>{totalQuantity}</td>
                                <td>{bookTitles}</td>
                                <td>
                                    <div style={{ display: "flex", gap: "6px", flexWrap: "wrap" }}>
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
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </section>
        );

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
