import { Link } from "react-router-dom";
import "../../styles/Admin.css";
import "../../styles/AdminDashboard.css"; //

export default function AdminDashboard() {
    return (
        <section className="admin-dashboard">
            <h1 className="admin-title-colored">Admin</h1>

            <div className="admin-button-grid">
                <Link className="btn btn-blue admin-btn" to="/admin/products">
                    Producten beheren
                </Link>

                <Link className="btn btn-green admin-btn" to="/admin/orders">
                    Bestellingen inzien
                </Link>

                <Link className="btn btn-orange admin-btn" to="/admin/users">
                    Gebruikers beheren
                </Link>
            </div>
        </section>
    );
}
