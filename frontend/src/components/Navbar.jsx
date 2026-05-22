import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { useCart } from "../contexts/CartContext";
import "../styles/Navbar.css";
import { useState } from "react";

export default function Navbar() {
    const { user, logout, hasRole } = useAuth();
    const { items } = useCart();
    const navigate = useNavigate();
    const [term, setTerm] = useState("");

    function getFirstName(value) {
        if (!value) return "";
        if (value.includes(" ")) return value.split(" ")[0];
        if (value.includes("@")) return value.split("@")[0];
        return value;
    }

    const onLogout = () => {
        logout();
        navigate("/");
    };

    const onSearch = (e) => {
        e.preventDefault();
        const q = term.trim();
        if (q) navigate(`/products?q=${encodeURIComponent(q)}`);
        else navigate("/products");
    };

    return (
        <header className="navbar">
            <div className="nav-inner">

                {/* LINKS */}
                <div className="nav-left">
                    <Link to="/" className="brand">WebBazar</Link>
                    <NavLink to="/products">Boeken</NavLink>

                    {hasRole("ROLE_ADMIN") && (
                        <NavLink to="/admin">Admin</NavLink>
                    )}
                </div>

                {/* MIDDEN (ZOEK) */}
                <div className="nav-center">
                    <form className="nav-search" onSubmit={onSearch}>
                        <input
                            type="search"
                            placeholder="Waar ben je naar op zoek"
                            value={term}
                            onChange={(e) => setTerm(e.target.value)}
                            aria-label="Zoek"
                        />
                    </form>
                </div>

                {/* RECHTS */}
                <div className="nav-right">
                    {user && (
                        <NavLink to="/dashboard">Mijn dashboard</NavLink>
                    )}

                    <NavLink to="/orders">Bestellingen</NavLink>
                    <NavLink to="/checkout">Winkelmand ({items.length})</NavLink>

                    {!user ? (
                        <>
                            <NavLink to="/login">Inloggen</NavLink>
                            <NavLink to="/register" className="btn">Registreren</NavLink>
                        </>
                    ) : (
                        <>
                            <span className="user">
                                Hallo, {getFirstName(user.name || user.email)}
                            </span>
                            <button onClick={onLogout} className="btn btn-green">
                                Uitloggen
                            </button>
                        </>
                    )}
                </div>

            </div>
        </header>
    );
}
