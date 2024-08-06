import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useNavigate
import './Login.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock, faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import Cookies from 'js-cookie';

const Login = ({ onLogin }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const navigate = useNavigate(); // Initialize useNavigate

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8088/api/login', formData);
      const { wpToken, jwt } = response.data;

      // Store the tokens in cookies
      Cookies.set('jwt', jwt, { expires: 7 });
      Cookies.set('wpToken', wpToken, { expires: 7 });

      onLogin(); // Notify parent about login success
      navigate('/profile'); // Redirect to profile page
    } catch (error) {
      alert(error.response ? error.response.data.message : "Erreur de connexion");
    }
  };

  return (
      <div className="login-box">
        <div className="login-container">
          <h2>J'ai déjà un compte ShopBuilder</h2>
          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <FontAwesomeIcon icon={faUser} className="icon" />
              <input type="text" id="username" required placeholder='Nom d’utilisateur' value={formData.username} onChange={handleChange} />
            </div>
            <div className="input-group password-group">
              <FontAwesomeIcon icon={faLock} className="icon" />
              <input type={showPassword ? 'text' : 'password'} id="password" required placeholder='Mot De Passe' value={formData.password} onChange={handleChange} />
              <FontAwesomeIcon
                  icon={showPassword ? faEyeSlash : faEye}
                  className="toggle-password"
                  onClick={togglePasswordVisibility}
              />
            </div>
            <div className="remember-me">
              <input type="checkbox" id="remember" />
              <label htmlFor="remember">Se souvenir de mon identifiant</label>
            </div>
            <button type="submit" className='Button'>SE CONNECTER</button>
          </form>
          <a href="/forgot-password" className="forgot-password">MOT DE PASSE OUBLIÉ ?</a>
        </div>

        <div className="signup-box">
          <h3>Nouveau Sur ShopBuilder ?</h3>
          <button className='Button_sign_up' onClick={() => navigate('/register')}>S'INSCRIRE</button>
        </div>
      </div>
  );
}

export default Login;
