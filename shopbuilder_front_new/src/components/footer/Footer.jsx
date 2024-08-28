import React from 'react';
import './Footer.css';
import logoFooter from '../../assets/logo_footer.png';
import logo from '../../assets/logo.png';
 
const Footer = () => {
    const getCurrentYear = () => {
        return new Date().getFullYear();
    };

    return (
        <div>
            <div className="footer-container">
                <div className="subscription-box">
                    <h2>S'abonner
                        <span className="highlight">dernières actualités</span>
                    </h2>
                    <form>
                        <input type="email" placeholder="Votre e-mail" required />
                        <button type="submit">S'abonner</button>
                    </form>
                </div>
                <div className="footer-logo">
                    <img src={logo} alt="Logo" />
                </div>
            </div>
            <footer className="footerr">
                <div className="footer-content-wrapper">
                    <div className="footer-content">
                        <img src={logoFooter} alt="ShopBuilder Logo" />
                        <p>
                            Ce site web facilite la création de sites e-commerce pour ceux qui<br />
                            n'ont aucune formation officielle. Ventes faciles et simples,<br />
                            avec des milliers de produits de commerce en gros. Accès en continu,<br />
                            mise en page premium, et une équipe compétente en programmation.
                        </p>
                    </div>
                    <div className="contact-info">
                        <p>E-mail: info@shopbuilder.com</p>
                        <p>Tel: +212 610573493</p>
                    </div>
                </div>
                <div className="footer-bottom">
                    <p>Copyright © {getCurrentYear()} Powered by <a href="https://pulse.digital/"> PULSE.digital</a></p>
                </div>
            </footer>
        </div>
    );
}

export default Footer;
