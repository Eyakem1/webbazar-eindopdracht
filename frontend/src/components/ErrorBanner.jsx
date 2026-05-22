import React from "react";
import "../styles/ErrorBanner.css";

const ErrorBanner = ({ message }) => {
    return <div className="error-banner">{message}</div>;
};

export default ErrorBanner;
