import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../contexts/AuthContext";
import "../styles/UserDashboard.css";
import buttonStyles from "../styles/buttons.module.css";

export default function UserDashboard() {
    const { user, setUser } = useAuth();

    const [profile, setProfile] = useState({
        email: "",
        name: "",
        address: "",
        newPassword: "",
    });
    const [originalProfile, setOriginalProfile] = useState(null);
    const [profileLoading, setProfileLoading] = useState(true);
    const [profileError, setProfileError] = useState("");
    const [profileOk, setProfileOk] = useState("");
    const [editMode, setEditMode] = useState(false);

    const [orders, setOrders] = useState([]);
    const [ordersError, setOrdersError] = useState("");

    useEffect(() => {
        const loadProfile = async () => {
            setProfileLoading(true);
            try {
                const res = await api.get("/api/auth/me");
                const base = {
                    email: res.data.email,
                    name: res.data.name || "",
                    address: res.data.address || "",
                    newPassword: "",
                };
                setProfile(base);
                setOriginalProfile(base);
            } catch {
                setProfileError("Kon je profiel niet laden.");
            }
            setProfileLoading(false);
        };
        loadProfile();
    }, []);

    useEffect(() => {
        api.get("/api/orders")
            .then(res => setOrders(res.data || []))
            .catch(e => setOrdersError(e?.response?.data?.message || "Kan je bestellingen niet ophalen."));
    }, []);

    const rows = useMemo(() => {
        return orders.flatMap(o => {
            const items = o.items || o.orderItems || [];
            return items.map(it => ({
                id: it.id,
                title: it.productTitle || it.product?.title || "Product",
                type: it.type,
                date: o.createdAt,
            }));
        });
    }, [orders]);

    const handleProfileChange = e => {
        if (!editMode) return;
        const { name, value } = e.target;
        setProfile(prev => ({ ...prev, [name]: value }));
    };

    const handleProfileSubmit = async e => {
        e.preventDefault();
        setProfileError("");
        setProfileOk("");

        try {
            await api.put("/api/auth/me", {
                name: profile.name,
                address: profile.address,
                newPassword: profile.newPassword || null,
            });

            setProfileOk("Je gegevens zijn opgeslagen.");
            setOriginalProfile({
                email: profile.email,
                name: profile.name,
                address: profile.address,
                newPassword: "",
            });
            setEditMode(false);

            if (user && setUser) {
                const updated = { ...user, name: profile.name };
                setUser(updated);
                localStorage.setItem("wb_auth", JSON.stringify(updated));
            }
        } catch {
            setProfileError("Opslaan van je gegevens is mislukt.");
        }
    };

    const handleCancelEdit = () => {
        setEditMode(false);
        setProfile({
            ...originalProfile,
            newPassword: "",
        });
        setProfileError("");
        setProfileOk("");
    };

    return (
        <div className="dashboard-page">
            <div className="dashboard-header">
                <h1>Mijn dashboard</h1>
                <p>Beheer je gegevens en bekijk je digitale aankopen.</p>
            </div>

            <div className="dashboard-grid">

                <section className="card card-fixed">
                    <h2 className="card-title">Mijn gegevens</h2>

                    {profileLoading ? (
                        <p>Profiel wordt geladen...</p>
                    ) : (
                        <form className="profile-form" onSubmit={handleProfileSubmit}>

                            {!editMode && (
                                <div className="action-button-center">
                                    <button
                                        type="button"
                                        className={`${buttonStyles.btn} ${buttonStyles.blue}`}
                                        onClick={() => setEditMode(true)}
                                    >
                                        Gegevens wijzigen
                                    </button>
                                </div>
                            )}

                            {profileError && <p className="message message-error">{profileError}</p>}
                            {profileOk && <p className="message message-success">{profileOk}</p>}

                            <div className="form-row">
                                <label>
                                    E-mailadres
                                    <input type="email" value={profile.email} disabled />
                                </label>
                            </div>

                            <div className="form-row">
                                <label>
                                    Naam
                                    <input
                                        type="text"
                                        name="name"
                                        disabled={!editMode}
                                        value={profile.name}
                                        onChange={handleProfileChange}
                                    />
                                </label>
                            </div>

                            <div className="form-row">
                                <label>
                                    Adres
                                    <input
                                        type="text"
                                        name="address"
                                        disabled={!editMode}
                                        value={profile.address}
                                        onChange={handleProfileChange}
                                    />
                                </label>
                            </div>

                            <div className="form-row">
                                <label>
                                    Nieuw wachtwoord
                                    <input
                                        type="password"
                                        name="newPassword"
                                        disabled={!editMode}
                                        value={profile.newPassword}
                                        onChange={handleProfileChange}
                                        placeholder="Laat dit veld leeg om je huidige wachtwoord te behouden"
                                    />
                                </label>
                            </div>

                            {editMode && (
                                <div className="edit-buttons">
                                    <button
                                        type="button"
                                        className={`${buttonStyles.btn}`}
                                        style={{ background: "#e5e7eb", color: "#111827" }}
                                        onClick={handleCancelEdit}
                                    >
                                        Annuleren
                                    </button>
                                    <button
                                        type="submit"
                                        className={`${buttonStyles.btn} ${buttonStyles.blue}`}
                                    >
                                        Opslaan
                                    </button>
                                </div>
                            )}
                        </form>
                    )}
                </section>

                {/* BESTELLINGEN */}
                <section className="card">
                    <h2 className="card-title">Mijn bestellingen</h2>

                    <div className="action-button-center">
                        <Link
                            to="/orders"
                            className={`${buttonStyles.btn} ${buttonStyles.green}`}
                        >
                            Detail bestellingen
                        </Link>
                    </div>

                    {ordersError && <p className="message message-error">{ordersError}</p>}
                    {!ordersError && rows.length === 0 && <p className="muted">Je hebt nog geen bestellingen.</p>}

                    {rows.length > 0 && (
                        <div className="table-wrapper">
                            <table className="dashboard-table">
                                <thead>
                                <tr>
                                    <th>Boektitel</th>
                                    <th>Type</th>
                                    <th>Datum</th>
                                </tr>
                                </thead>
                                <tbody>
                                {rows.map(row => (
                                    <tr key={row.id}>
                                        <td>{row.title}</td>
                                        <td>{row.type}</td>
                                        <td>
                                            {row.date
                                                ? new Date(row.date).toLocaleDateString()
                                                : "-"}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
}
