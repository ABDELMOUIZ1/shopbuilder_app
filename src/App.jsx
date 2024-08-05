import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import './App.css';
import Login from './components/login/Login.jsx';
import Footer from './components/footer/Footer.jsx';
import NavBar from './components/navigation/NavBar.jsx';
import Register from './components/registration/Register.jsx';
import Prof from './components/prof/Prof.jsx';
import Form from './components/form/Form';
import Cookies from 'js-cookie';
import { isTokenExpired } from './utils/jwt';
import Form2 from "./components/form2/Form2";

const App = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    useEffect(() => {
        const token = Cookies.get('jwt');
        if (token && !isTokenExpired(token)) {
            setIsLoggedIn(true);
        } else {
            setIsLoggedIn(false);
        }
    }, []);

    const handleLogin = () => {
        setIsLoggedIn(true);
    };

    const handleLogout = () => {
        Cookies.remove('jwt');
        Cookies.remove('wpToken');
        setIsLoggedIn(false);
    };

    return (
        <Router>
            <div className="App">
                <div className="main-content">
                    <NavBar onLogout={handleLogout} />
                    <Routes>
                        <Route path="/login" element={!isLoggedIn ? <Login onLogin={handleLogin} /> : <Navigate to="/profile" />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/profile" element={isLoggedIn ? <Prof /> : <Navigate to="/login" />} />
                        <Route path="/form" element={isLoggedIn ? <Form /> : <Navigate to="/login" />} />
                        <Route path="/form2" element={isLoggedIn ? <Form2 /> : <Navigate to="/login" />} />
                        <Route path="/" element={<Navigate to="/login" />} />
                    </Routes>
                    <Footer />
                </div>
            </div>
        </Router>
    );
}

export default App;
