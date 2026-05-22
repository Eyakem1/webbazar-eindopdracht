import { createContext, useContext, useEffect, useMemo, useState } from "react";

const CartContext = createContext(null);
const STORAGE_KEY = "wb_cart";

export function CartProvider({ children }) {
    const [items, setItems] = useState(() => {
        try {
            const raw = localStorage.getItem(STORAGE_KEY);
            const parsed = raw ? JSON.parse(raw) : [];

            return Array.isArray(parsed)
                ? parsed.map((item, index) => ({
                    ...item,
                    cartId:
                        item.cartId ??
                        `${item.product?.id ?? "unknown"}-${
                            item.type ?? "BUY"
                        }-${index}`,
                }))
                : [];
        } catch (err) {
            console.warn("Kon winkelwagen niet laden:", err);
            return [];
        }
    });

    useEffect(() => {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
        } catch (err) {
            console.warn("Kon winkelwagen niet opslaan:", err);
        }
    }, [items]);

    const createCartId = (product, type) => {
        return `${product.id}-${type}-${Date.now()}-${Math.random()
            .toString(16)
            .slice(2)}`;
    };


      //Item toevoegen winkelwagen
    const add = (product, options) => {
        let type = "BUY";
        let quantity = 1;
        let rental = null;

        if (typeof options === "string") {
            // Oude add-vorm
            type = options;
        } else if (typeof options === "object" && options !== null) {
            // Nieuwe add-vorm
            type = options.type || "BUY";
            quantity =
                typeof options.quantity === "number" && options.quantity > 0
                    ? options.quantity
                    : 1;
            rental = options.rental || null;
        }

        setItems((previousItems) => {
            const index = previousItems.findIndex(
                (item) =>
                    item.product.id === product.id &&
                    item.type === type
            );

            if (index >= 0) {
                const updated = [...previousItems];
                const existing = updated[index];

                updated[index] = {
                    ...existing,
                    quantity: existing.quantity + quantity,
                };

                return updated;
            }

            return [
                ...previousItems,
                {
                    cartId: createCartId(product, type),
                    product,
                    type, // "Koop" of "Huur"
                    quantity,
                    rental,
                },
            ];
        });
    };

    const remove = (productId, type = "BUY") => {
        setItems((previousItems) =>
            previousItems.filter(
                (item) => !(item.product.id === productId && item.type === type)
            )
        );
    };

    const updateQuantity = (productId, type = "BUY", quantity) => {
        setItems((previousItems) => {
            if (quantity <= 0) {
                return previousItems.filter(
                    (item) =>
                        !(item.product.id === productId && item.type === type)
                );
            }

            return previousItems.map((item) => {
                if (item.product.id === productId && item.type === type) {
                    return { ...item, quantity };
                }
                return item;
            });
        });
    };

    const clear = () => setItems([]);

    const total = useMemo(() => {
        return items.reduce((sum, item) => {
            const price =
                item.type === "BUY"
                    ? Number(item.product.price || 0)
                    : Number(item.product.rentalPrice || 0);
            return sum + price * item.quantity;
        }, 0);
    }, [items]);

    return (
        <CartContext.Provider
            value={{ items, add, remove, updateQuantity, clear, total }}
        >
            {children}
        </CartContext.Provider>
    );
}

export function useCart() {
    return useContext(CartContext);
}
