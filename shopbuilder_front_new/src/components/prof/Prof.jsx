import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode';
import img1 from '../../assets/hihi.png';
import img2 from '../../assets/po.jpg';
import webcamIcon from '../../assets/vv.jpg';
import './Prof.css';

function UserProfileCard({ onClose, firstName, lastName }) {
    const handleWebcamClick = () => {
        console.log('Webcam icon clicked!');
    };

    return (
        <div className="user-profile-card">
            <button className="close-button" onClick={onClose} aria-label="Close">&times;</button>
            <div className="profile-container">
                <img src={img2} alt="Profile" className="profile-pic" />
                <button className="webcam-button" onClick={handleWebcamClick} aria-label="Webcam">
                    <img src={webcamIcon} alt="Webcam Icon" className="webcam-icon" />
                </button>
            </div>
            <div className="user-info">
                <h2>{`${firstName} ${lastName}`}</h2>
                <p>Nomprenom@gmail.com</p>
                <div className="user-actions">
                    <button>Informations</button>
                    <button>Aide</button>
                    <button>Déconnexion</button>
                    <button>Mode Sombre</button>
                </div>
            </div>
        </div>
    );
}

function Prof() {
    const [isCardVisible, setIsCardVisible] = useState(false);
    const [userName, setUserName] = useState({ firstName: '', lastName: '' });
    const navigate = useNavigate();

    useEffect(() => {
        // Check for JWT token in cookies
        const token = Cookies.get('jwt');
        if (!token) {
            // Redirect to login if no token is found
            navigate('/login');
            return;
        }

        try {
            // Decode the token to extract user info
            const decodedToken = jwtDecode(token);
            // Assuming the token payload contains firstName and lastName
            setUserName({
                firstName: decodedToken.firstName || '',
                lastName: decodedToken.lastName || ''
            });
        } catch (error) {
            console.error('Failed to decode token:', error);
            navigate('/login');
        }
    }, [navigate]);

    const handleShowCard = () => {
        setIsCardVisible(true);
    };

    const handleCloseCard = () => {
        setIsCardVisible(false);
    };


    const handleRedirect = () => {
        navigate('/form');
    };
    const handleRedirect2 = () => {
        navigate('/form2');
    };
    return (
        <div className="container">
            {isCardVisible && <UserProfileCard onClose={handleCloseCard} firstName={userName.firstName} lastName={userName.lastName} />}
            <header className="header">
                <h1>Votre Espace Client</h1>
                <div className="greeting-stats-container">
                    <img src={img1} alt="Descriptive Text" className="left-image" />
                    <div className="greeting-stats">
                        <p className="greeting">Bonjour, {userName.firstName} {userName.lastName}</p>
                    </div>
                </div>
            </header>
            <main className="main">
                <section className="personal-info">
                    <button className="button3" onClick={handleShowCard}>Infos Personnelles</button>
                </section>
                <section className="config-panel">
                    <div className="config-header">
                        <h2>Panneau de configurations</h2>
                    </div>
                    <div className="content">
                        <div className="buttons">
                            <div className="button-container">
                                <button className="button1" onClick={handleRedirect}>Modifier le contenu du site web</button>
                            </div>
                            <div className="button-container">
                                <button className="button2" onClick={handleRedirect2}>Ajouter un produit</button>
                            </div>
                            <div className="button-container">
                                <button className="button3">Ajouter un package</button>
                            </div>
                            <div className="button-container">
                                <button className="button4">Ajouter une promotion</button>
                            </div>
                        </div>
                    </div>
                </section>
            </main>
            <footer className="footer">
                <p>Besoin d'aide ? Consultez notre guide d'utilisation.</p>
            </footer>
        </div>
    );
}

export default Prof;
