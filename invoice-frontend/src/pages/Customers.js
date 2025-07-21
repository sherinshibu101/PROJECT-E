import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { customerAPI } from '../services/api';
import '../styles/Pages.css';

const Customers = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await customerAPI.getAll();
      setCustomers(response.data);
    } catch (error) {
      console.error('Error fetching customers:', error);
      setError('Failed to load customers. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this customer?')) {
      try {
        await customerAPI.delete(id);
        setCustomers(customers.filter(customer => customer.id !== id));
      } catch (error) {
        console.error('Error deleting customer:', error);
        alert('Failed to delete customer. Please try again.');
      }
    }
  };

  const handleResetIds = async () => {
    if (window.confirm('Are you sure you want to reset customer IDs? This will restart ID numbering from 1.')) {
      try {
        const response = await fetch('http://localhost:8080/api/customers/reset-ids', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
        });

        if (response.ok) {
          alert('Customer IDs reset successfully! New customers will start from ID 1.');
          fetchCustomers(); // Refresh the list
        } else {
          throw new Error('Failed to reset IDs');
        }
      } catch (error) {
        console.error('Error resetting customer IDs:', error);
        setError('Failed to reset customer IDs. Please try again.');
      }
    }
  };

  const filteredCustomers = customers.filter(customer => 
    customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    customer.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    customer.phone?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="page-container">
        <div className="container">
          <div className="text-center p-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-2">Loading customers...</p>
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
            <h2 className="page-title">Customers</h2>
            <div className="btn-group">
              <Link to="/customers/add" className="btn btn-dark-blue">
                <i className="bi bi-plus-circle me-2"></i> Add New Customer
              </Link>
              <button
                className="btn btn-outline-warning"
                onClick={handleResetIds}
                title="Reset customer ID numbering to start from 1"
              >
                <i className="bi bi-arrow-clockwise me-2"></i> Reset IDs
              </button>
            </div>
          </div>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
              <button className="btn btn-sm btn-outline-danger ms-2" onClick={fetchCustomers}>
                Try Again
              </button>
            </div>
          )}

          <div className="mb-3">
            <input
              type="text"
              className="form-control search-input"
              placeholder="Search customers..."
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
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Address</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredCustomers.length > 0 ? (
                  filteredCustomers.map((customer) => (
                    <tr key={customer.id}>
                      <td>{customer.id}</td>
                      <td>{customer.name}</td>
                      <td>{customer.email}</td>
                      <td>{customer.phone || 'N/A'}</td>
                      <td>{customer.address}</td>
                      <td>
                        <div className="btn-group" role="group">
                          <Link to={`/customers/edit/${customer.id}`} className="btn btn-sm btn-outline-primary">
                            Edit
                          </Link>
                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleDelete(customer.id)}
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
                      {searchTerm ? 'No customers match your search.' : 'No customers found.'}
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

export default Customers;
