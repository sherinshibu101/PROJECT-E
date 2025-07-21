import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { productAPI } from '../services/api';
import '../styles/Pages.css';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await productAPI.getAll();
      setProducts(response.data);
    } catch (error) {
      console.error('Error fetching products:', error);
      setError('Failed to load products. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      try {
        await productAPI.delete(id);
        setProducts(products.filter(product => product.id !== id));
      } catch (error) {
        console.error('Error deleting product:', error);
        alert('Failed to delete product. Please try again.');
      }
    }
  };

  const filteredProducts = products.filter(product => 
    product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.description?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="page-container">
        <div className="container">
          <div className="text-center p-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-2">Loading products...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h1 className="emart-title text-center">EMART INVOICE SYSTEM</h1>
      <div className="container">
        <div className="content-card">
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="page-title">Products</h2>
            <Link to="/products/add" className="btn btn-dark-blue">
              <i className="bi bi-plus-circle me-2"></i> Add New Product
            </Link>
          </div>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
              <button className="btn btn-sm btn-outline-danger ms-2" onClick={fetchProducts}>
                Try Again
              </button>
            </div>
          )}

          <div className="mb-3">
            <input
              type="text"
              className="form-control search-input"
              placeholder="Search products..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="table-responsive">
            <table className="table table-hover">
              <thead className="table-dark-blue">
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Price</th>
                  <th>Stock</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredProducts.length > 0 ? (
                  filteredProducts.map((product) => (
                    <tr key={product.id}>
                      <td>{product.id}</td>
                      <td>{product.name}</td>
                      <td>{product.description || 'N/A'}</td>
                      <td>${product.currentPrice?.toFixed(2) || '0.00'}</td>
                      <td>{product.stockQuantity || 0}</td>
                      <td>
                        <div className="btn-group" role="group">
                          <Link to={`/products/edit/${product.id}`} className="btn btn-sm btn-outline-primary">
                            Edit
                          </Link>
                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleDelete(product.id)}
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="6" className="text-center">
                      {searchTerm ? 'No products match your search.' : 'No products found.'}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Products;
