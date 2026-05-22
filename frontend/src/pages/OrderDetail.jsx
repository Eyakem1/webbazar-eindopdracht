import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../utils/axios";

export default function OrderDetail() {
    const { id } = useParams();
    const [order, setOrder] = useState(null);
    const [err, setErr] = useState("");

    useEffect(() => {
        api
            .get(`/api/orders/${id}`)
            .then((r) => setOrder(r.data))
            .catch((e) =>
                setErr(e?.response?.data?.message || "Kon order niet laden")
            );
    }, [id]);

    const downloadItem = async (itemId) => {
        try {
            const res = await api.get(`/api/downloads/${itemId}`, {
                responseType: "blob",
            });
            const url = URL.createObjectURL(res.data);
            const a = document.createElement("a");
            a.href = url;
            a.download = `item-${itemId}`;
            a.click();
            URL.revokeObjectURL(url);
        } catch (e) {
            alert(
                e?.response?.status === 423
                    ? "Nog niet beschikbaar (huur startdatum)"
                    : "Download niet beschikbaar"
            );
        }
    };

    if (err) return <p style={{ color: "crimson" }}>{err}</p>;
    if (!order) return <p>Laden…</p>;

    const items = order.items || [];

    // Onderscheid tussen kopen en huren
    const buyItems = items.filter((it) => it.type === "BUY");
    const rentItems = items.filter((it) => it.type === "RENT");
    const otherItems = items.filter(
        (it) => it.type !== "BUY" && it.type !== "RENT"
    );

    return (
        <section>
            <h1>Order #{order.id}</h1>
            <p>Status: {order.status}</p>

            {/* Gekochte boeken */}
            {buyItems.length > 0 && (
                <>
                    <h2>Gekochte boeken</h2>
                    <ul>
                        {buyItems.map((it) => (
                            <li key={it.id}>
                                {it.title} — €{Number(it.price || 0).toFixed(2)}{" "}
                                <span>(kopen)</span>
                                <button
                                    onClick={() => downloadItem(it.id)}
                                    style={{ marginLeft: 8 }}
                                >
                                    Download
                                </button>
                            </li>
                        ))}
                    </ul>
                </>
            )}

            {/* Gehuurde boeken */}
            {rentItems.length > 0 && (
                <>
                    <h2>Gehuurde boeken</h2>
                    <ul>
                        {rentItems.map((it) => (
                            <li key={it.id}>
                                {it.title} — €{Number(it.price || 0).toFixed(2)}{" "}
                                <span>(huren)</span>
                                <button
                                    onClick={() => downloadItem(it.id)}
                                    style={{ marginLeft: 8 }}
                                >
                                    Download
                                </button>
                            </li>
                        ))}
                    </ul>
                </>
            )}

            {/* Overige items  */}
            {otherItems.length > 0 && (
                <>
                    <h2>Overige items</h2>
                    <ul>
                        {otherItems.map((it) => (
                            <li key={it.id}>
                                {it.title} — €{Number(it.price || 0).toFixed(2)}{" "}
                                <span>({it.type})</span>
                                <button
                                    onClick={() => downloadItem(it.id)}
                                    style={{ marginLeft: 8 }}
                                >
                                    Download
                                </button>
                            </li>
                        ))}
                    </ul>
                </>
            )}
        </section>
    );
}
