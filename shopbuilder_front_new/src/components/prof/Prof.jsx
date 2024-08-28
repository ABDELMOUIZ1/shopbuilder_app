import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import {jwtDecode} from 'jwt-decode';
import img1 from '../../assets/hihi.png';
import img2 from '../../assets/po.jpg';
import webcamIcon from '../../assets/vv.jpg';
import './Prof.css';

function UserProfileCard({ onClose, firstName, lastName, email, onLogout }) {
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
                <p>{email}</p>
                <div className="user-actions">
                    <button onClick={onLogout}>Déconnexion</button>
                </div>
            </div>
        </div>
    );
}

function Prof({ onLogout }) {
    const [isCardVisible, setIsCardVisible] = useState(false);
    const [userInfo, setUserInfo] = useState({ firstName: '', lastName: '', email: '' });
    const navigate = useNavigate();

    useEffect(() => {
        // Check for JWT token in cookies
        const token = Cookies.get('jwt');
        if (!token) {
            navigate('/login');
            return;
        }

        try {
            const decodedToken = jwtDecode(token);
            setUserInfo({
                firstName: decodedToken.firstName || '',
                lastName: decodedToken.lastName || '',
                email: decodedToken.email || 'No email provided'
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

    const handleRedirect = (path) => {
        navigate(path);
    };

    return (
        <div className="container">
            {isCardVisible && (
                <UserProfileCard
                    onClose={handleCloseCard}
                    firstName={userInfo.firstName}
                    lastName={userInfo.lastName}
                    email={userInfo.email}
                    onLogout={onLogout} // Use the onLogout prop passed from App
                />
            )}
            <header className="header">
                <h1>Votre Espace Client</h1>
                <div className="greeting-stats-container">
                    <img src={img1} alt="Descriptive Text" className="left-image" />
                    <div className="greeting-stats">
                        <p className="greeting">Bonjour, {userInfo.firstName} {userInfo.lastName}</p>
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
                                <button className="button1" onClick={() => handleRedirect('/form')}>Modifier le contenu du site web</button>
                            </div>
                            <div className="button-container">
                                <button className="button2" onClick={() => handleRedirect('/form2')}>Ajouter un produit</button>
                            </div>
                            <div className="button-container">
                                <button className="button3" onClick={() => handleRedirect('/form3')}>Ajouter un package</button>
                            </div>
                            <div className="button-container">
                                <button className="button4" onClick={() => handleRedirect('/form4')}>Ajouter une promotion</button>
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
