import React, { useState, useEffect } from 'react';
import './NavBar.css';
import logo from '../../assets/logo.png';
import { useNavigate } from 'react-router-dom';
import {jwtDecode} from 'jwt-decode';
import Cookies from 'js-cookie';

const NavBar = ({ onLogout, isLoggedIn }) => {
    const navigate = useNavigate();
    const [isDropdownOpen, setDropdownOpen] = useState(false);
    const [userInfo, setUserInfo] = useState(null);

    useEffect(() => {
        const token = Cookies.get('jwt');
        if (token) {
            try {
                const decodedToken = jwtDecode(token);
                setUserInfo({
                    firstName: decodedToken.firstName || 'User',
                    lastName: decodedToken.lastName || ''
                });
            } catch (error) {
                console.error('Failed to decode JWT token:', error);
                setUserInfo(null);
            }
        } else {
            setUserInfo(null);
        }
    }, [isLoggedIn]); // Update user info when isLoggedIn changes

    const handleLogout = () => {
        onLogout();
        setDropdownOpen(false); // Close the dropdown menu
    };

    return (
        <nav className="navbar">
            <div className="logo">
                <img src={logo} alt="ShopBuilder Logo" />
            </div>
            <ul className="nav-links">
                <li><button className='Nav-button' onClick={() => navigate('/dashboard')}>Page D'accueil</button></li>
                {isLoggedIn ? (
                    <>
                        <li><button className='Nav-button' onClick={() => navigate('/profile')}>Profil</button></li>
                        <li className='user-info'>
                            {userInfo?.firstName} {userInfo?.lastName}
                            <button className='dropdown-toggle' onClick={() => setDropdownOpen(!isDropdownOpen)}>
                                ▼
                            </button>
                            {isDropdownOpen && (
                                <div className='dropdown-menu'>
                                    <button onClick={handleLogout}>Se Déconnecter</button>
                                </div>
                            )}
                        </li>
                    </>
                ) : (
                    <>
                        <li><button className='Nav-button' onClick={() => navigate('/register')}>S'inscrire</button></li>
                        <li><button className='Nav-button' onClick={() => navigate('/login')}>Se Connecter</button></li>
                    </>
                )}
            </ul>
        </nav>
    );
}

export default NavBar;
