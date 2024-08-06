import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import './Form2.css'; // Adjust as needed

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
    const [imageUrls, setImageUrls] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await axios.get('http://localhost:8088/api/categories');
                setCategories(response.data);
            } catch (error) {
                console.error('Error fetching categories:', error);
            }
        };

        fetchCategories();
    }, []);

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
        setSelectedImages(Array.from(e.target.files));
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

    return (
        <div>
            <form className="product-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Product Name:</label>
                    <input
                        type="text"
                        value={productName}
                        onChange={(e) => setProductName(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Price:</label>
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
                    <label>Short Description:</label>
                    <textarea
                        value={shortDescription}
                        onChange={(e) => setShortDescription(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Categories:</label>
                    <div className="checkbox-list">
                        {categories.map(category => (
                            <div key={category.idCategory} className="checkbox-item">
                                <input
                                    type="checkbox"
                                    id={`category-${category.idCategory}`}
                                    value={category.idCategory}
                                    checked={selectedCategories.some(selected => selected.id === category.idCategory)}
                                    onChange={handleCategoryChange}
                                />
                                <label htmlFor={`category-${category.idCategory}`}>{category.name}</label>
                            </div>
                        ))}
                        <button type="button" onClick={() => setShowCategoryPopup(true)}>Add New Category</button>
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
                            {Array.from(selectedImages).map((image, index) => (
                                <img key={index} src={URL.createObjectURL(image)} alt="Preview" />
                            ))}
                        </div>
                    )}
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? 'Submitting...' : 'Add Product'}
                </button>
                {message && <div className="message">{message}</div>}
            </form>

            {showCategoryPopup && (
                <div className="popup">
                    <div className="popup-content">
                        <h2>Add New Category</h2>
                        <div className="form-group">
                            <label>Name:</label>
                            <input
                                type="text"
                                value={newCategory.name}
                                onChange={(e) => setNewCategory({ ...newCategory, name: e.target.value })}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Slug:</label>
                            <input
                                type="text"
                                value={newCategory.slug}
                                onChange={(e) => setNewCategory({ ...newCategory, slug: e.target.value })}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Description:</label>
                            <textarea
                                value={newCategory.description}
                                onChange={(e) => setNewCategory({ ...newCategory, description: e.target.value })}
                                required
                            />
                        </div>
                        <button onClick={handleAddCategory}>Add Category</button>
                        <button onClick={() => setShowCategoryPopup(false)}>Cancel</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Form2;
