import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

import Home from "./pages/Home";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import Products from "./pages/ProductList";
import ProductDetail from "./pages/ProductDetail";
import Checkout from "./pages/CheckoutPage";
import Orders from "./pages/Orders";
import Cart from "./pages/Cart";

import AdminProducts from "./pages/admin/AdminProducts";
import AdminOrders from "./pages/admin/AdminOrders";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminUsers from "./pages/admin/AdminUsers";

import UserDashboard from "./pages/UserDashboard";
import ProfilePage from "./pages/ProfilePage";
import Contact from "./pages/Contact";

import ProtectedRoute from "./components/ProtectedRoute";
import "./App.css";

export default function App() {
    return (
        <div className="app">
            <Navbar />
            <main className="content">
                <Routes>
                    {/* Home */}
                    <Route path="/" element={<Home />} />

                    {/* Publieke pagina’s */}
                    <Route path="/products" element={<Products />} />
                    <Route path="/products/:id" element={<ProductDetail />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/contact" element={<Contact />} />

                    {/* Winkelmand (client-side, geen login nodig) */}
                    <Route path="/cart" element={<Cart />} />

                    {/* Gebruiker (alleen ingelogde ROLE_USER) */}
                    <Route
                        path="/checkout"
                        element={
                            <ProtectedRoute role="ROLE_USER">
                                <Checkout />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/orders"
                        element={
                            <ProtectedRoute role="ROLE_USER">
                                <Orders />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute role="ROLE_USER">
                                <UserDashboard />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/profile"
                        element={
                            <ProtectedRoute role="ROLE_USER">
                                <ProfilePage />
                            </ProtectedRoute>
                        }
                    />

                    {/* Admin dashboard */}
                    <Route
                        path="/admin"
                        element={
                            <ProtectedRoute role="ROLE_ADMIN">
                                <AdminDashboard />
                            </ProtectedRoute>
                        }
                    />

                    {/* Admin subpagina’s */}
                    <Route
                        path="/admin/products"
                        element={
                            <ProtectedRoute role="ROLE_ADMIN">
                                <AdminProducts />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/admin/orders"
                        element={
                            <ProtectedRoute role="ROLE_ADMIN">
                                <AdminOrders />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/admin/users"
                        element={
                            <ProtectedRoute role="ROLE_ADMIN">
                                <AdminUsers />
                            </ProtectedRoute>
                        }
                    />

                    {/* 404 → terug naar Home */}
                    <Route path="*" element={<Home />} />
                </Routes>
            </main>
            <Footer />
        </div>
    );
}
