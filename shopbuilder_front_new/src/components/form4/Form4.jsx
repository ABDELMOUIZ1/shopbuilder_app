import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import './Form4.css';

const Form4 = () => {
    const [products, setProducts] = useState([]);
    const [selectedProductIds, setSelectedProductIds] = useState([]);
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [formData, setFormData] = useState({
        code: '',
        amount: '',
        individual_use: false,
        exclude_sale_items: false,
        minimum_amount: '',
        description: '',
        usage_limit: '',
        date_expires: ''
    });
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
                    withCredentials: true
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
                // Handle redirection or error display as needed
                if (error.response && error.response.status === 401) {
                    // Token might be expired or invalid
                    console.log('Unauthorized, redirecting to login...');
                    navigate('/login');
                }
            }
        };

        fetchProducts();
    }, [navigate]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: type === 'checkbox' ? checked : value
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
        console.log('Toggling dropdown');
        setDropdownOpen(prevState => !prevState);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const jwtToken = Cookies.get('jwt');

            if (!jwtToken) {
                console.log('No JWT token found, redirecting to login...');
                navigate('/login');
                return;
            }

            const response = await axios.post('http://localhost:8088/api/coupons/add', {
                ...formData,
                product_ids: selectedProductIds,
                discount_type: 'percent'
            }, {
                headers: {
                    Authorization: `Bearer ${jwtToken}`,
                    'Content-Type': 'application/json'
                },
                withCredentials: true
            });

            setMessage('Discount added successfully');
            setTimeout(() => navigate('/profile'), 2000);
        } catch (error) {
            if (error.response && error.response.data) {
                setMessage(error.response.data);
            } else {
                setMessage('Discount Already Exists, Try Another Code Name');
            }
            console.error('Error adding discount:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <form className="form4-container" onSubmit={handleSubmit}>
                <div className="form4-group">
                    <label>Code :</label>
                    <input
                        type="text"
                        id="code"
                        name="code"
                        value={formData.code}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form4-group">
                    <label>Montant :</label>
                    <input
                        type="number"
                        id="amount"
                        name="amount"
                        value={formData.amount}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form4-group">
                    <label>Utilisation individuelle :</label>
                    <input
                        type="checkbox"
                        id="individual_use"
                        name="individual_use"
                        checked={formData.individual_use}
                        onChange={handleChange}
                    />
                </div>

                <div className="form4-group">
                    <label>Exclure les articles en solde :</label>
                    <input
                        type="checkbox"
                        id="exclude_sale_items"
                        name="exclude_sale_items"
                        checked={formData.exclude_sale_items}
                        onChange={handleChange}
                    />
                </div>

                <div className="form4-group">
                    <label>Montant minimum :</label>
                    <input
                        type="number"
                        id="minimum_amount"
                        name="minimum_amount"
                        value={formData.minimum_amount}
                        onChange={handleChange}
                    />
                </div>

                <div className="form4-group">
                    <label>Produits :</label>
                    <div className="dropdown-container">
                        <button type="button" className="dropdown-button" onClick={handleDropdownToggle}>
                            {selectedProductIds.length > 0 ? `${selectedProductIds.length} produit(s) sélectionné(s)` : 'Sélectionner les produits'}
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

                <div className="form4-group">
                    <label>Description :</label>
                    <textarea
                        id="description"
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                    />
                </div>

                <div className="form4-group">
                    <label>Limite d'utilisation :</label>
                    <input
                        type="number"
                        id="usage_limit"
                        name="usage_limit"
                        value={formData.usage_limit}
                        onChange={handleChange}
                    />
                </div>

                <div className="form4-group">
                    <label>Date d'expiration :</label>
                    <input
                        type="datetime-local"
                        id="date_expires"
                        name="date_expires"
                        value={formData.date_expires}
                        onChange={handleChange}
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? 'Envoi en cours...' : 'Ajouter une réduction'}
                </button>
                {message && <div className="form4-message">{message}</div>}
            </form>
        </div>
    );
};

export default Form4;
