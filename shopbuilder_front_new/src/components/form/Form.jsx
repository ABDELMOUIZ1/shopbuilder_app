import React, { useState } from 'react';
import './Form.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';

const Form = () => {
    const [title, setTitle] = useState('');
    const [logo, setLogo] = useState(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleTitleChange = (e) => {
        setTitle(e.target.value);
    };

    const handleLogoChange = (e) => {
        setLogo(e.target.files[0]);
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

            // Upload the image and get the image ID
            const formData = new FormData();
            formData.append('file', logo);
            console.log('Uploading image...');

            const uploadResponse = await axios.post('http://localhost:8088/api/upload/image', formData, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'multipart/form-data',
                },
                withCredentials: true,
            });

            // Extract the image ID and ensure it's an Integer
            const imageId = parseInt(uploadResponse.data, 10);
            console.log('Image uploaded, received image ID:', imageId);

            // Update website with title and image ID
            console.log('Updating website...');
            const updateResponse = await axios.post('http://localhost:8088/api/updateWebsite', {
                title,
                site_logo: imageId // Correct field name
            }, {
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
            if (error.response) {
                console.log('Error response data:', error.response.data);
                console.log('Error response status:', error.response.status);
                console.log('Error response headers:', error.response.headers);
            }
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
                    required
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
}

export default Form;
