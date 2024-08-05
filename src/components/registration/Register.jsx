import React, { useState } from 'react';
import './Register.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faLock, faEye, faEyeSlash, faUser, faPhone } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

const Register = ({ showLogin }) => {
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [confirmPasswordVisible, setConfirmPasswordVisible] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    phoneNumber: '',
    domaineName: '',
    password: '',
    confirmPassword: ''
  });

  const togglePasswordVisibility = () => {
    setPasswordVisible(!passwordVisible);
  };

  const toggleConfirmPasswordVisibility = () => {
    setConfirmPasswordVisible(!confirmPasswordVisible);
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      alert("Les mots de passe ne correspondent pas");
      return;
    }
    try {
      const response = await axios.post('http://localhost:8088/api/register', formData);
      alert(response.data);
      showLogin();
    } catch (error) {
      alert(error.response ? error.response.data : "Erreur d'inscription");
    }
  };

  return (
      <div className="register-box">
        <div className="login-container">
          <h3>J'ai déjà un compte ShopBuilder </h3>
          <button className='Button_sign_up' onClick={showLogin}>SE CONNECTER</button>
        </div>
        <div className="register-container">
          <h2>Saisissez vos informations pour continuer.</h2>
          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <FontAwesomeIcon icon={faUser} className="icon" />
              <input type="text" id="firstName" required placeholder='Prénom' value={formData.firstName} onChange={handleChange} />
            </div>
            <div className="input-group">
              <FontAwesomeIcon icon={faUser} className="icon" />
              <input type="text" id="lastName" required placeholder='Nom de famille' value={formData.lastName} onChange={handleChange} />
            </div>
            <div className="input-group">
              <FontAwesomeIcon icon={faUser} className="icon" />
              <input type="text" id="username" required placeholder='Nom d’utilisateur' value={formData.username} onChange={handleChange} />
            </div>
            <div className="input-group">
              <FontAwesomeIcon icon={faEnvelope} className="icon" />
              <input type="email" id="email" required placeholder='Adresse e-mail' value={formData.email} onChange={handleChange} />
            </div>
            <div className="input-group">
              <FontAwesomeIcon icon={faPhone} className="icon" />
              <input type="tel" id="phoneNumber" required placeholder='Téléphone portable' value={formData.phoneNumber} onChange={handleChange} />
            </div>
            <div className="input-group">
              <FontAwesomeIcon icon={faUser} className="icon" />
              <input type="text" id="domaineName" required placeholder='Nom de domaine' value={formData.domaineName} onChange={handleChange} />
            </div>
            <div className="input-group password-group">
              <FontAwesomeIcon icon={faLock} className="icon" />
              <input type={passwordVisible ? 'text' : 'password'} id="password" required placeholder='Mot de passe' value={formData.password} onChange={handleChange} />
              <FontAwesomeIcon
                  icon={passwordVisible ? faEyeSlash : faEye}
                  className="toggle-password"
                  onClick={togglePasswordVisibility}
              />
            </div>
            <div className="input-group password-group">
              <FontAwesomeIcon icon={faLock} className="icon" />
              <input type={confirmPasswordVisible ? 'text' : 'password'} id="confirmPassword" required placeholder='Confirmer le mot de passe' value={formData.confirmPassword} onChange={handleChange} />
              <FontAwesomeIcon
                  icon={confirmPasswordVisible ? faEyeSlash : faEye}
                  className="toggle-password"
                  onClick={toggleConfirmPasswordVisibility}
              />
            </div>
            <div className="remember-me">
              <input type="checkbox" id="terms" required/>
              <label htmlFor="terms">J'accepte les Conditions d'Utilisation</label>
            </div>
            <button type="submit" className='Button'>S'INSCRIRE</button>
          </form>
        </div>
      </div>
  );
};

export default Register;
