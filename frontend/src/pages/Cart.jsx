import { useCart } from "../contexts/CartContext";
import { Link } from "react-router-dom";
import "../styles/Checkout.css";

export default function Cart() {
    const { items, remove, updateQuantity, total } = useCart();

    if (!items.length) {
        return (
            <p className="cart-empty">
                Je winkelmand is leeg. <Link to="/boeken">Ga naar boeken</Link>
            </p>
        );
    }

    return (
        <section className="checkout">
            <h1>Winkelmand</h1>

            <ul className="cart-list">
                {items.map((i) => (
                    <li key={i.cartId ?? `${i.product.id}-${i.type}`}>
                        <div className="cart-item-left">
                            <div className="cart-item-title">{i.product.title}</div>
                            <div className="cart-item-type">
                                {i.type === "BUY" ? "Kopen" : "Huren"}
                            </div>
                        </div>

                        <div className="cart-item-right">
                            <div className="cart-quantity">
                                <button
                                    type="button"
                                    className="cart-qty-btn"
                                    onClick={() =>
                                        updateQuantity(i.product.id, i.type, i.quantity - 1)
                                    }
                                    disabled={i.quantity <= 1}
                                >
                                    –
                                </button>

                                <span className="cart-qty-value">{i.quantity}</span>

                                <button
                                    type="button"
                                    className="cart-qty-btn"
                                    onClick={() =>
                                        updateQuantity(i.product.id, i.type, i.quantity + 1)
                                    }
                                >
                                    +
                                </button>
                            </div>

                            <button
                                type="button"
                                className="cart-remove-btn"
                                onClick={() => remove(i.product.id, i.type)}
                            >
                                Verwijderen
                            </button>
                        </div>
                    </li>
                ))}
            </ul>

            <div className="summary">
                <div className="total">Totaal: €{total.toFixed(2)}</div>
                <div className="actions">
                    <Link to="/boeken" className="linkBtn">
                        Verder winkelen
                    </Link>
                    <Link to="/checkout" className="linkBtn">
                        Afrekenen
                    </Link>
                </div>
            </div>
        </section>
    );
}
