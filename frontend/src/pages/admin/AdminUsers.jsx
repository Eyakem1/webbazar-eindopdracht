import { useEffect, useState, useRef } from "react";
import api from "../../api/axios";
import "../../styles/Admin.css";

const emptyCreateForm = {
    email: "",
    password: "",
    name: "",
    address: "",
    enabled: true,
};

const emptyEditForm = {
    name: "",
    address: "",
    enabled: true,
    password: "",
};

export default function AdminUsers() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [createForm, setCreateForm] = useState(emptyCreateForm);
    const [isCreating, setIsCreating] = useState(false);

    const [editingId, setEditingId] = useState(null);
    const [editForm, setEditForm] = useState(emptyEditForm);
    const [isSavingEdit, setIsSavingEdit] = useState(false);

    const editSectionRef = useRef(null);

    // ---------- Gebruikers laden ----------
    const loadUsers = async () => {
        setLoading(true);
        setError("");

        try {
            const response = await api.get("/api/admin/users");
            setUsers(response.data || []);
        } catch (err) {
            console.error("Kon gebruikers niet laden", err);
            setError("Kon gebruikers niet laden. Probeer het later opnieuw.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadUsers();
    }, []);

    // ---------- Scroll naar formulier ----------
    useEffect(() => {
        if (editingId && editSectionRef.current) {
            editSectionRef.current.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
        }
    }, [editingId]);

    // ---------- Gebruiker aanmaken ----------
    const handleCreateChange = (event) => {
        const { name, value, type, checked } = event.target;

        setCreateForm((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value,
        }));
    };

    const handleCreateSubmit = async (event) => {
        event.preventDefault();
        setError("");

        const email = createForm.email.trim();
        const password = createForm.password.trim();

        if (!email || !password) {
            setError("E-mail en wachtwoord zijn verplicht.");
            return;
        }

        setIsCreating(true);

        try {
            await api.post("/api/admin/users", {
                email,
                password,
                name: createForm.name.trim(),
                address: createForm.address.trim(),
                enabled: createForm.enabled,
            });

            setCreateForm(emptyCreateForm);
            await loadUsers();
        } catch (err) {
            console.error("Aanmaken van gebruiker mislukt", err);
            setError("Het aanmaken van de gebruiker is mislukt.");
        } finally {
            setIsCreating(false);
        }
    };

    // ---------- Gebruiker bewerken ----------
    const startEdit = (user) => {
        setEditingId(user.id);

        setEditForm({
            name: user.name || "",
            address: user.address || "",
            enabled: user.enabled,
            password: "",
        });
    };

    const cancelEdit = () => {
        setEditingId(null);
        setEditForm(emptyEditForm);
    };

    const handleEditChange = (event) => {
        const { name, value, type, checked } = event.target;

        setEditForm((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value,
        }));
    };

    const handleEditSubmit = async (event) => {
        event.preventDefault();

        if (!editingId) return;

        setIsSavingEdit(true);
        setError("");

        try {
            await api.put(`/api/admin/users/${editingId}`, {
                name: editForm.name.trim(),
                address: editForm.address.trim(),
                enabled: editForm.enabled,
                ...(editForm.password.trim() !== "" && {
                    password: editForm.password.trim(),
                }),
            });

            setEditingId(null);
            setEditForm(emptyEditForm);

            await loadUsers();
        } catch (err) {
            console.error("Updaten van gebruiker mislukt", err);
            setError("Het opslaan van de wijzigingen is mislukt.");
        } finally {
            setIsSavingEdit(false);
        }
    };

    // ---------- Gebruiker verwijderen ----------
    const handleDelete = async (userId) => {
        setError("");

        try {
            await api.delete(`/api/admin/users/${userId}`);

            setUsers((prev) =>
                prev.filter((u) => u.id !== userId)
            );
        } catch (err) {
            console.error("Verwijderen van gebruiker mislukt", err);

            const message =
                err.response?.data?.message ||
                err.response?.data?.error ||
                (typeof err.response?.data === "string"
                    ? err.response.data
                    : null) ||
                "Gebruiker kan niet verwijderd worden omdat er bestellingen aan gekoppeld zijn. Deactiveer de gebruiker in plaats daarvan.";

            setError(message);
        }
    };

    // ---------- Render ----------
    return (
        <main className="admin-page">
            <header className="admin-header">
                <h1 className="admin-title">Gebruikersbeheer</h1>

                <p className="admin-subtitle">
                    Bekijk, voeg toe, bewerk en beheer accounts voor WebBazar.
                </p>
            </header>

            {error && (
                <p className="form-error">
                    {error}
                </p>
            )}

            {loading ? (
                <p>Bezig met laden...</p>
            ) : (
                <>
                    {/* Nieuwe gebruiker */}
                    <section className="admin-section">
                        <h2 className="admin-section-title">
                            Nieuwe gebruiker
                        </h2>

                        <form
                            className="admin-form"
                            onSubmit={handleCreateSubmit}
                        >
                            <div className="admin-form-grid">
                                <div className="admin-form-group">
                                    <label htmlFor="email">
                                        E-mail
                                    </label>

                                    <input
                                        id="email"
                                        name="email"
                                        type="email"
                                        value={createForm.email}
                                        onChange={handleCreateChange}
                                        required
                                    />
                                </div>

                                <div className="admin-form-group">
                                    <label htmlFor="password">
                                        Wachtwoord
                                    </label>

                                    <input
                                        id="password"
                                        name="password"
                                        type="password"
                                        value={createForm.password}
                                        onChange={handleCreateChange}
                                        required
                                    />
                                </div>

                                <div className="admin-form-group">
                                    <label htmlFor="name">
                                        Naam
                                    </label>

                                    <input
                                        id="name"
                                        name="name"
                                        type="text"
                                        value={createForm.name}
                                        onChange={handleCreateChange}
                                    />
                                </div>

                                <div className="admin-form-group admin-form-group-full">
                                    <label htmlFor="address">
                                        Adres
                                    </label>

                                    <input
                                        id="address"
                                        name="address"
                                        type="text"
                                        value={createForm.address}
                                        onChange={handleCreateChange}
                                    />
                                </div>

                                <div className="admin-form-group">
                                    <label className="checkbox-label">
                                        <input
                                            type="checkbox"
                                            name="enabled"
                                            checked={createForm.enabled}
                                            onChange={handleCreateChange}
                                        />{" "}
                                        Account actief
                                    </label>
                                </div>
                            </div>

                            <button
                                type="submit"
                                className="btn btn-green"
                                disabled={isCreating}
                            >
                                {isCreating
                                    ? "Bezig met aanmaken..."
                                    : "Gebruiker aanmaken"}
                            </button>
                        </form>
                    </section>

                    {/* Bestaande gebruikers */}
                    <section className="admin-section">
                        <h2 className="admin-section-title">
                            Bestaande gebruikers
                        </h2>

                        {users.length === 0 ? (
                            <p>
                                Er zijn nog geen gebruikers gevonden.
                            </p>
                        ) : (
                            <ul className="admin-list">
                                {users.map((user) => (
                                    <li
                                        key={user.id}
                                        className="admin-list-item"
                                    >
                                        <div className="admin-list-content">
                                            <h3 className="product-title">
                                                {user.email}
                                            </h3>

                                            <p className="product-meta">
                                                {user.name && (
                                                    <span className="product-author">
                                                        {user.name}
                                                    </span>
                                                )}

                                                <span className="product-price">
                                                    {user.enabled
                                                        ? "Actief"
                                                        : "Geblokkeerd"}
                                                </span>
                                            </p>

                                            <p className="product-description">
                                                <strong>Adres:</strong>{" "}
                                                {user.address &&
                                                user.address.trim() !== ""
                                                    ? user.address
                                                    : "Geen adres ingevuld"}
                                            </p>

                                            {user.roles &&
                                                user.roles.length > 0 && (
                                                    <p className="product-meta">
                                                        Rollen:{" "}
                                                        {user.roles.join(", ")}
                                                    </p>
                                                )}
                                        </div>

                                        <div className="admin-list-actions">
                                            <button
                                                type="button"
                                                className="btn"
                                                onClick={() =>
                                                    startEdit(user)
                                                }
                                            >
                                                Bewerken
                                            </button>

                                            <button
                                                type="button"
                                                className="btn btn-red"
                                                onClick={() =>
                                                    handleDelete(user.id)
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

                    {/* Edit-sectie */}
                    {editingId && (
                        <section
                            className="admin-section"
                            ref={editSectionRef}
                        >
                            <h2 className="admin-section-title">
                                Gebruiker bewerken
                            </h2>

                            <form
                                className="admin-form"
                                onSubmit={handleEditSubmit}
                            >
                                <div className="admin-form-grid">
                                    <div className="admin-form-group">
                                        <label htmlFor="edit-name">
                                            Naam
                                        </label>

                                        <input
                                            id="edit-name"
                                            name="name"
                                            type="text"
                                            value={editForm.name}
                                            onChange={handleEditChange}
                                        />
                                    </div>

                                    <div className="admin-form-group admin-form-group-full">
                                        <label htmlFor="edit-address">
                                            Adres
                                        </label>

                                        <input
                                            id="edit-address"
                                            name="address"
                                            type="text"
                                            value={editForm.address}
                                            onChange={handleEditChange}
                                        />
                                    </div>

                                    <div className="admin-form-group">
                                        <label htmlFor="edit-password">
                                            Nieuw wachtwoord
                                        </label>

                                        <input
                                            id="edit-password"
                                            name="password"
                                            type="password"
                                            value={editForm.password}
                                            onChange={handleEditChange}
                                            placeholder="Laat leeg om niet te wijzigen"
                                        />
                                    </div>

                                    <div className="admin-form-group">
                                        <label className="checkbox-label">
                                            <input
                                                type="checkbox"
                                                name="enabled"
                                                checked={editForm.enabled}
                                                onChange={handleEditChange}
                                            />{" "}
                                            Account actief
                                        </label>
                                    </div>
                                </div>

                                <div className="admin-list-actions">
                                    <button
                                        type="submit"
                                        className="btn btn-green"
                                        disabled={isSavingEdit}
                                    >
                                        {isSavingEdit
                                            ? "Bezig met opslaan..."
                                            : "Wijzigingen opslaan"}
                                    </button>

                                    <button
                                        type="button"
                                        className="btn"
                                        onClick={cancelEdit}
                                    >
                                        Annuleren
                                    </button>
                                </div>
                            </form>
                        </section>
                    )}
                </>
            )}
        </main>
    );
}