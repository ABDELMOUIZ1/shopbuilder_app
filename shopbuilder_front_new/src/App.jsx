import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import './App.css';
import Dashboard from './components/dashboard/Dashboard.jsx';
import Login from './components/login/Login.jsx';
import Footer from './components/footer/Footer.jsx';
import NavBar from './components/navigation/NavBar.jsx';
import Register from './components/registration/Register.jsx';
import Prof from './components/prof/Prof.jsx';
import Cookies from 'js-cookie';
import { isTokenExpired } from './utils/jwt';
import Form from './components/form/Form';
import Form2 from "./components/form2/Form2";
import Form3 from "./components/form3/Form3";
import Form4 from "./components/form4/Form4";

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
                    <NavBar onLogout={handleLogout} isLoggedIn={isLoggedIn} />
                    <Routes>
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/login" element={!isLoggedIn ? <Login onLogin={handleLogin} /> : <Navigate to="/profile" />} />
                        <Route path="/register" element={!isLoggedIn ? <Register onLogin={handleLogin} /> : <Navigate to="/profile" />} />
                        <Route path="/profile" element={isLoggedIn ? <Prof onLogout={handleLogout} /> : <Navigate to="/login" />} />
                        <Route path="/form" element={isLoggedIn ? <Form /> : <Navigate to="/login" />} />
                        <Route path="/form2" element={isLoggedIn ? <Form2 /> : <Navigate to="/login" />} />
                        <Route path="/form3" element={isLoggedIn ? <Form3 /> : <Navigate to="/login" />} />
                        <Route path="/form4" element={isLoggedIn ? <Form4 /> : <Navigate to="/login" />} />
                        <Route path="/" element={<Navigate to="/dashboard" />} />
                    </Routes>
                    <Footer />
                </div>
            </div>
        </Router>
    );
}

export default App;
