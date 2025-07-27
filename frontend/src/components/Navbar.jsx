import React from "react";
import { Link } from "react-router-dom";
import "../styles/Navbar.css";

const Navbar = () => {
    return (
        <nav className="navbar">
            <h1 className="navbar__logo">WebBazar</h1>
            <ul className="navbar__links">
                <li><Link to="/">Home</Link></li>
                <li><Link to="/books">Boeken</Link></li> {/* aangepaste route */}
                <li><Link to="/checkout">Winkelmand</Link></li>
                <li><Link to="/login">Login</Link></li>
            </ul>
        </nav>
    );
};

export default Navbar;
