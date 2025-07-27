import React from "react";
import styles from "../styles/Button.module.css";

const Button = ({ children, onClick, type = "button" }) => {
    return (
        <button type={type} onClick={onClick} className={styles.button}>
            {children}
        </button>
    );
};

export default Button;
