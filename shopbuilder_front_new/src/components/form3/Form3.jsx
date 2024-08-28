import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import './Form3.css'; // Ensure this path is correct

const Form3 = () => {
    const [products, setProducts] = useState([]);
    const [selectedProductIds, setSelectedProductIds] = useState([]);
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [formData, setFormData] = useState({
        packName: '',
        price: '',
        description: '',
        shortDescription: ''
    });
    const [selectedImages, setSelectedImages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchProducts = async () => {
            const jwtToken = Cookies.get('jwt');

            if (!jwtToken) {
                console.log('No JWT token found, redirecting to login...');
                navigate('/login');
                return;
            }

            try {
                const response = await axios.get('http://localhost:8088/api/getAllProducts', {
                    headers: {
                        Authorization: `Bearer ${jwtToken}`,
                    },
                    withCredentials: true,
                });

                if (Array.isArray(response.data)) {
                    setProducts(response.data.map(product => ({
                        id: product.idProduct,
                        name: product.productName
                    })));
                } else {
                    console.error('Expected an array of products, got:', response.data);
                }
            } catch (error) {
                console.error('Error fetching products:', error);
            }
        };

        fetchProducts();
    }, [navigate]);

    useEffect(() => {
        // Clean up object URLs to avoid memory leaks
        return () => {
            selectedImages.forEach(file => URL.revokeObjectURL(file.preview));
        };
    }, [selectedImages]);

    const handleFileChange = (e) => {
        const files = Array.from(e.target.files);
        setSelectedImages(files.map(file => {
            file.preview = URL.createObjectURL(file);
            return file;
        }));
    };

    const uploadImages = async () => {
        const jwtToken = Cookies.get('jwt');
        const wpToken = Cookies.get('wpToken');

        if (!jwtToken || !wpToken) {
            console.log('No tokens found, redirecting to login...');
            navigate('/login');
            return [];
        }

        const formData = new FormData();
        selectedImages.forEach((file) => formData.append('files', file));

        try {
            const response = await axios.post('http://localhost:8088/api/upload/images', formData, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'multipart/form-data',
                },
                withCredentials: true,
            });
            console.log('Images uploaded:', response.data);
            return response.data.map(img => img.src);
        } catch (error) {
            console.error('Error uploading images:', error);
            return [];
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleProductCheckboxChange = (id) => {
        setSelectedProductIds(prevSelected =>
            prevSelected.includes(id)
                ? prevSelected.filter(productId => productId !== id)
                : [...prevSelected, id]
        );
    };

    const handleDropdownToggle = () => {
        setDropdownOpen(prevState => !prevState);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const uploadedUrls = await uploadImages();
            const jwtToken = Cookies.get('jwt');
            const wpToken = Cookies.get('wpToken');

            if (!jwtToken || !wpToken) {
                console.log('No tokens found, redirecting to login...');
                navigate('/login');
                return;
            }

            const packDto = {
                name: formData.packName,
                type: 'grouped', // Assuming 'grouped' as default
                description: formData.description,
                short_description: formData.shortDescription,
                images: uploadedUrls.map(url => ({ src: url })),
                grouped_products: selectedProductIds,
                meta_data: [
                    { key: 'prix_global', value: formData.price }
                ]
            };

            await axios.post('http://localhost:8088/api/packs/add', packDto, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'application/json',
                },
                withCredentials: true
            });

            setMessage('Pack added successfully');
            setTimeout(() => navigate('/profile'), 2000);
        } catch (error) {
            setMessage('Error adding pack');
            console.error('Error adding pack:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <form className="product-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Nom du Pack :</label>
                    <input
                        type="text"
                        name="packName"
                        value={formData.packName}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Prix :</label>
                    <input
                        type="number"
                        name="price"
                        value={formData.price}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Description :</label>
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Description courte :</label>
                    <textarea
                        name="shortDescription"
                        value={formData.shortDescription}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Produits :</label>
                    <div className="dropdown-container">
                        <button type="button" className="dropdown-button" onClick={handleDropdownToggle}>
                            {selectedProductIds.length > 0 ? `${selectedProductIds.length} Produit(s) sélectionné(s)` : 'Sélectionner des produits'}
                        </button>
                        {dropdownOpen && (
                            <div className="dropdown-content">
                                {products.length > 0 ? (
                                    products.map(product => (
                                        <div key={product.id} className="checkbox-item">
                                            <label>
                                                <input
                                                    type="checkbox"
                                                    checked={selectedProductIds.includes(product.id)}
                                                    onChange={() => handleProductCheckboxChange(product.id)}
                                                />
                                                {product.name}
                                            </label>
                                        </div>
                                    ))
                                ) : (
                                    <p>Aucun produit disponible</p>
                                )}
                            </div>
                        )}
                    </div>
                </div>

                <div className="form-group">
                    <label>Images :</label>
                    <input
                        type="file"
                        multiple
                        onChange={handleFileChange}
                    />
                    {selectedImages.length > 0 && (
                        <div className="image-preview">
                            {selectedImages.map((image, index) => (
                                <img key={index} src={image.preview} alt="Aperçu"/>
                            ))}
                        </div>
                    )}
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? 'Envoi en cours...' : 'Ajouter le Pack'}
                </button>
                {message && <div className="form-message">{message}</div>}
            </form>
        </div>
    );
};

export default Form3;
