import React, { useEffect, useState } from "react";
import axios from "axios";
import "../styles/UserDashboard.css";

function UserDashboard() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            setError("Je bent niet ingelogd.");
            return;
        }

        axios
            .get("http://localhost:8080/api/orders", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then((res) => setOrders(res.data))
            .catch(() => setError("Kan je bestellingen niet ophalen."));
    }, []);

    return (
        <div className="dashboard-container">
            <h2>Mijn Dashboard</h2>
            <p>Hier zie je je gekochte of gehuurde boeken.</p>

            {error && <p className="error">{error}</p>}

            <table className="dashboard-table">
                <thead>
                <tr>
                    <th>Boektitel</th>
                    <th>Type</th>
                    <th>Datum</th>
                    <th>Download</th>
                </tr>
                </thead>
                <tbody>
                {orders.flatMap((order, index) =>
                    order.products.map((product, i) => (
                        <tr key={`${index}-${i}`}>
                            <td>{product.title}</td>
                            <td>{order.type}</td>
                            <td>{new Date(order.orderDate).toLocaleDateString()}</td>
                            <td>
                                <a
                                    href={product.imageUrl || "#"}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="btn-download"
                                >
                                    Download
                                </a>
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
        </div>
    );
}

export default UserDashboard;
