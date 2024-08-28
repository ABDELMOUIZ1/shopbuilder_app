import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import './Form2.css'; // Ensure this path is correct

const Form2 = () => {
    const [productName, setProductName] = useState('');
    const [price, setPrice] = useState('');
    const [description, setDescription] = useState('');
    const [shortDescription, setShortDescription] = useState('');
    const [categories, setCategories] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [newCategory, setNewCategory] = useState({ name: '', slug: '', description: '' });
    const [showCategoryPopup, setShowCategoryPopup] = useState(false);
    const [selectedImages, setSelectedImages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const jwtToken = Cookies.get('jwt');

                if (!jwtToken) {
                    console.log('No JWT token found, redirecting to login...');
                    navigate('/login');
                    return;
                }

                const response = await axios.get('http://localhost:8088/api/categories', {
                    headers: {
                        Authorization: `Bearer ${jwtToken}`,
                    },
                });

                // Ensure the response is an array before setting it
                if (Array.isArray(response.data)) {
                    setCategories(response.data);
                } else {
                    console.error('Unexpected response format:', response.data);
                    setCategories([]); // Set an empty array if the response is not an array
                }
            } catch (error) {
                console.error('Error fetching categories:', error);
                setCategories([]); // Ensure categories is always an array
            }
        };

        fetchCategories();
    }, [navigate]);

    const handleCategoryChange = (e) => {
        const { value, checked } = e.target;
        setSelectedCategories((prev) => {
            if (checked) {
                return [...prev, { id: parseInt(value) }];
            } else {
                return prev.filter((category) => category.id !== parseInt(value));
            }
        });
    };

    const handleFileChange = (e) => {
        const files = Array.from(e.target.files);
        setSelectedImages(files);

        // Revoke previous object URLs to avoid memory leaks
        files.forEach(file => {
            URL.revokeObjectURL(file.preview);
            file.preview = URL.createObjectURL(file);
        });
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

    const handleAddCategory = async () => {
        if (newCategory.name && newCategory.slug && newCategory.description) {
            try {
                const jwtToken = Cookies.get('jwt');

                if (!jwtToken) {
                    console.log('No JWT token found, redirecting to login...');
                    navigate('/login');
                    return;
                }

                const categoryData = {
                    ...newCategory,
                };

                const response = await axios.post('http://localhost:8088/api/categories', categoryData, {
                    headers: {
                        Authorization: `Bearer ${jwtToken}`,
                        'Content-Type': 'application/json',
                    },
                    withCredentials: true,
                });

                console.log('Category added:', response.data);
                setCategories((prevCategories) => [...prevCategories, response.data]);
                setShowCategoryPopup(false);
                setNewCategory({ name: '', slug: '', description: '' });
            } catch (error) {
                console.error('Error adding category:', error);
            }
        }
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

            const productData = {
                name: productName,
                type: 'simple',
                regular_price: price,
                description,
                short_description: shortDescription,
                categories: selectedCategories,
                images: uploadedUrls.map(url => ({ src: url })),
                stock_quantity: 10,
            };

            console.log('Sending product data:', productData);

            await axios.post('http://localhost:8088/api/addProduct', productData, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'application/json',
                },
                withCredentials: true,
            });

            setMessage('Product added successfully');
            setTimeout(() => {
                navigate('/profile');
            }, 2000); // Navigate to profile after 2 seconds

        } catch (error) {
            console.error('Error adding product:', error);
            setMessage('Error adding product');
        } finally {
            setLoading(false);
        }
    };

    const toggleDropdown = () => {
        console.log('Toggling dropdown');
        setDropdownOpen(prev => !prev);
    };

    return (
        <div>
            <form className="product-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Nom du produit :</label>
                    <input
                        type="text"
                        value={productName}
                        onChange={(e) => setProductName(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Prix:</label>
                    <input
                        type="number"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Description:</label>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Courte description :</label>
                    <textarea
                        value={shortDescription}
                        onChange={(e) => setShortDescription(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label >Catégories :</label>
                    <div className="dropdown-container">
                        <button type="button" className="dropdown-button" onClick={toggleDropdown}>
                            {selectedCategories.length ? `${selectedCategories.length} Category(s) Selected` : 'Select Categories'}
                        </button>
                        {dropdownOpen && (
                            <div className="dropdown-content">
                                {categories.length > 0 ? (
                                    categories.map(category => (
                                        <div key={category.idCategory} className="checkbox-item">
                                            <label>
                                                <input
                                                    type="checkbox"
                                                    id={`category-${category.idCategory}`}
                                                    value={category.idCategory}
                                                    checked={selectedCategories.some(selected => selected.id === category.idCategory)}
                                                    onChange={handleCategoryChange}
                                                />
                                                <label htmlFor={`category-${category.idCategory}`}>{category.name}</label>
                                            </label>
                                        </div>
                                    ))
                                ) : (
                                    <p>Aucune catégorie disponible</p>
                                )}
                                <button type="button" className="add-category-button" onClick={() => setShowCategoryPopup(true)}>
                                    Ajouter une nouvelle catégorie
                                </button>
                            </div>
                        )}
                    </div>
                </div>
                <div className="form-group">
                    <label>Images:</label>
                    <input
                        type="file"
                        multiple
                        onChange={handleFileChange}
                    />
                    {selectedImages.length > 0 && (
                        <div className="image-preview">
                            {selectedImages.map((image, index) => (
                                <img key={index} src={image.preview} alt="Preview" />
                            ))}
                        </div>
                    )}
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? 'Soumission en cours...' : 'Ajouter un produit'}
                </button>
                {message && <div className="message">{message}</div>}
            </form>

            {showCategoryPopup && (
                <div className="popup">
                    <div className="popup-content">
                        <h2>Ajouter une Nouvelle Categorie: </h2>
                        <div className="form-group">
                            <label>Nom :</label>
                            <input
                                type="text"
                                value={newCategory.name}
                                onChange={(e) => setNewCategory(prev => ({ ...prev, name: e.target.value }))}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Slug :</label>
                            <input
                                type="text"
                                value={newCategory.slug}
                                onChange={(e) => setNewCategory(prev => ({ ...prev, slug: e.target.value }))}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Description :</label>
                            <textarea
                                value={newCategory.description}
                                onChange={(e) => setNewCategory(prev => ({ ...prev, description: e.target.value }))}
                                required
                            />
                        </div>
                        <button onClick={handleAddCategory}>Ajouter</button>
                        <button onClick={() => setShowCategoryPopup(false)}>Annuler</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Form2;
