import React, { useState } from 'react';
import './Form.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';

const Form = () => {
    const [title, setTitle] = useState('');
    const [logo, setLogo] = useState(null);
    const [backgroundColor, setBackgroundColor] = useState('');
    const [fontFamily, setFontFamily] = useState('');
    const [backgroundImageHome, setBackgroundImageHome] = useState(null);
    const [backgroundImageContact, setBackgroundImageContact] = useState(null);
    const [backgroundImageAbout, setBackgroundImageAbout] = useState(null);
    const [homePic, setHomePic] = useState(null); // Fichier pour l'image d'accueil
    const [homeDesc, setHomeDesc] = useState(''); // Description de l'accueil
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleTitleChange = (e) => {
        setTitle(e.target.value);
    };

    const handleLogoChange = (e) => {
        setLogo(e.target.files[0]);
    };

    const handleBackgroundColorChange = (e) => {
        setBackgroundColor(e.target.value);
    };

    const handleFontFamilyChange = (e) => {
        setFontFamily(e.target.value);
    };

    const handleBackgroundImageChange = (page, e) => {
        if (page === 'home') {
            setBackgroundImageHome(e.target.files[0]);
        } else if (page === 'contact') {
            setBackgroundImageContact(e.target.files[0]);
        } else if (page === 'about') {
            setBackgroundImageAbout(e.target.files[0]);
        }
    };

    const handleHomePicChange = (e) => {
        setHomePic(e.target.files[0]); // Gérer le fichier pour l'image d'accueil
    };

    const handleHomeDescChange = (e) => {
        setHomeDesc(e.target.value); // Gérer la description de l'accueil
    };

    const uploadImage = async (file) => {
        const formData = new FormData();
        formData.append('file', file);

        try {
            const jwtToken = Cookies.get('jwt');
            const response = await axios.post('http://localhost:8088/api/upload/image', formData, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'multipart/form-data',
                },
                withCredentials: true,
            });

            return response.data; // ID of the uploaded image
        } catch (error) {
            console.error('Error uploading image', error);
            throw error;
        }
    };

    const uploadImages = async (files) => {
        const formData = new FormData();
        files.forEach(file => {
            formData.append('files', file);
        });

        try {
            const jwtToken = Cookies.get('jwt');
            const response = await axios.post('http://localhost:8088/api/upload/images', formData, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'multipart/form-data',
                },
                withCredentials: true,
            });

            return response.data; // List of URLs for the uploaded images
        } catch (error) {
            console.error('Error uploading images', error);
            throw error;
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const jwtToken = Cookies.get('jwt');
            const wpToken = Cookies.get('wpToken');

            if (!jwtToken || !wpToken) {
                console.log('No tokens found, redirecting to login...');
                navigate('/login');
                return;
            }

            let logoId = null;
            if (logo) {
                logoId = await uploadImage(logo);
                console.log('Logo uploaded, received image ID:', logoId);
            }

            const backgroundImages = [
                backgroundImageHome,
                backgroundImageContact,
                backgroundImageAbout,
                homePic
            ].filter(Boolean);

            let backgroundImageUrls = [];
            if (backgroundImages.length > 0) {
                backgroundImageUrls = await uploadImages(backgroundImages);
                console.log('Background images uploaded, received image URLs:', backgroundImageUrls);
            }



            // Prepare data for updating the website
            const updateData = {
                title,
                site_logo: logoId, // Use ID of the logo
                backgroundColor,
                fontFamily,
                homeDesc, // Description de l'accueil
                backgroundImageHome: backgroundImageUrls[0]?.src || null, // URL for home background
                backgroundImageContact: backgroundImageUrls[1]?.src || null, // URL for contact background
                backgroundImageAbout: backgroundImageUrls[2]?.src || null, // URL for about background
                homePic: backgroundImageUrls[3]?.src || null,
            };

            // Update website with title, logo, background color, font family, and image URLs
            console.log('Updating website...');
            const updateResponse = await axios.post('http://localhost:8088/api/updateWebsite', updateData, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'application/json',
                },
                withCredentials: true,
            });

            console.log('Website updated:', updateResponse);
            setMessage('Website updated successfully');
            setTimeout(() => {
                navigate('/profile');
            }, 2000); // Navigate to profile after 2 seconds

        } catch (error) {
            console.error('Error updating website content', error);
            setMessage('Error updating website content');
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="form" onSubmit={handleSubmit}>
            <div className="form-group">
                <label>Nom du Website</label>
                <input
                    type="text"
                    placeholder="Nom du Site Web"
                    value={title}
                    onChange={handleTitleChange}
                    required
                />
            </div>
            <div className="form-group">
                <label>Logo du Website</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleLogoChange}
                />
            </div>
            <div className="form-group">
                <label>Palette de Couleurs</label>
                <input
                    type="color"
                    value={backgroundColor}
                    onChange={handleBackgroundColorChange}
                />
            </div>
            <div className="form-group">
                <label>Police d'écriture</label>
                <input
                    type="text"
                    placeholder="Font Family"
                    value={fontFamily}
                    onChange={handleFontFamilyChange}
                />
            </div>
            <div className="form-group">
                <label>Image d'accueil</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleHomePicChange}
                />
            </div>
            <div className="form-group">
                <label>Description d'accueil</label>
                <textarea
                    placeholder="Description d'accueil"
                    value={homeDesc}
                    onChange={handleHomeDescChange}
                />
            </div>
            <div className="form-group">
                <label>Image d'arrière-plan (Home)</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => handleBackgroundImageChange('home', e)}
                />
            </div>
            <div className="form-group">
                <label>Image d'arrière-plan (Contact)</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => handleBackgroundImageChange('contact', e)}
                />
            </div>
            <div className="form-group">
                <label>Image d'arrière-plan (About)</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => handleBackgroundImageChange('about', e)}
                />
            </div>
            {message && <div className="message">{message}</div>}
            <div className="form-buttons">
                <button type="submit" className="next-button" disabled={loading}>
                    {loading ? 'Submitting...' : 'Submit'}
                </button>
            </div>
        </form>
    );
};

export default Form;