import "../styles/Contact.css";
import buttonStyles from "../styles/buttons.module.css";

export default function Contact() {
    return (
        <section className="contact-page">
            <h1 className="contact-title">Neem gerust contact met ons op</h1>

            <div className="contact-layout">
                <div className="contact-left">
                    <div className="contact-block">
                        <div className="contact-line">
                            <span className="contact-icon contact-icon-location">📍</span>
                            <div>
                                <div>Amsterdamstraat 11, 1111 AA</div>
                                <div>Amsterdam</div>
                            </div>
                        </div>

                        <div className="contact-line">
                            <span className="contact-icon contact-icon-phone">📞</span>
                            <a href="tel:020445566" className="contact-link">
                                020 445566
                            </a>
                        </div>

                        <div className="contact-line">
                            <span className="contact-icon contact-icon-mail">✉️</span>
                            <a
                                href="mailto:Contact@Webbazar.com"
                                className="contact-link"
                            >
                                Contact@Webbazar.com
                            </a>
                        </div>
                    </div>

                    <div className="contact-block">
                        <p>Telefonisch contact is mogelijk op de onderstaande tijden</p>
                        <p>Maandag t/m vrijdag</p>
                        <p>08:30 - 19:00</p>
                        <p>zaterdag</p>
                        <p>08:30 - 18:00</p>
                        <p>Zondag:</p>
                        <p>gesloten</p>
                    </div>
                </div>

                <div className="contact-right">
                    <div className="contact-form-card">
                        <p className="contact-form-intro">
                            Vragen, opmerkingen of suggesties? Vul het formulier in en we
                            nemen binnenkort contact met u op.
                        </p>

                        <form className="contact-form">
                            <div className="contact-row contact-row-two">
                                <label className="contact-field">
                                    Voornaam*
                                    <input
                                        type="text"
                                        name="firstName"
                                        placeholder="Voornaam*"
                                    />
                                </label>
                                <label className="contact-field">
                                    Achternaam*
                                    <input
                                        type="text"
                                        name="lastName"
                                        placeholder="Achternaam*"
                                    />
                                </label>
                            </div>

                            <div className="contact-row">
                                <label className="contact-field">
                                    Email*
                                    <input
                                        type="email"
                                        name="email"
                                        placeholder="Email*"
                                    />
                                </label>
                            </div>

                            <div className="contact-row">
                                <label className="contact-field">
                                    Telefoon nummer*
                                    <input
                                        type="tel"
                                        name="phone"
                                        placeholder="Telefoon nummer*"
                                    />
                                </label>
                            </div>

                            <div className="contact-row">
                                <label className="contact-field">
                                    Bericht
                                    <textarea
                                        name="message"
                                        rows={4}
                                        placeholder="Bericht..."
                                    />
                                </label>
                            </div>

                            <div className="contact-actions">
                                <button
                                    type="submit"
                                    className={`${buttonStyles.btn} ${buttonStyles.blue} contact-submit`}
                                >
                                    Send Message
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    );
}
