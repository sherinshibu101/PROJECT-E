import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { orderAPI, invoiceAPI } from '../services/api';
import '../styles/Pages.css';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await orderAPI.getAll();
      setOrders(response.data);
    } catch (error) {
      console.error('Error fetching orders:', error);
      setError('Failed to load orders. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (orderId, newStatus) => {
    try {
      await orderAPI.updateStatus(orderId, newStatus);
      setOrders(orders.map(order => 
        order.id === orderId ? { ...order, status: newStatus } : order
      ));
    } catch (error) {
      console.error('Error updating order status:', error);
      alert('Failed to update order status. Please try again.');
    }
  };

  const handleGenerateInvoice = async (orderId) => {
    try {
      const response = await invoiceAPI.generatePDF(orderId);
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `invoice-${orderId}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error generating invoice:', error);
      alert('Failed to generate invoice. Please try again.');
    }
  };

  const getStatusBadge = (status) => {
    const statusClass = `status-badge status-${status}`;
    return <span className={statusClass}>{status.toUpperCase()}</span>;
  };

  const filteredOrders = orders.filter(order => 
    order.id.toString().includes(searchTerm) ||
    order.customer?.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    order.status.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="page-container">
        <div className="container">
          <div className="text-center p-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-2">Loading orders...</p>
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
            <h2 className="page-title">Orders</h2>
            <Link to="/orders/create" className="btn btn-dark-blue">
              <i className="bi bi-plus-circle me-2"></i> Create New Order
            </Link>
          </div>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
              <button className="btn btn-sm btn-outline-danger ms-2" onClick={fetchOrders}>
                Try Again
              </button>
            </div>
          )}

          <div className="mb-3">
            <input
              type="text"
              className="form-control search-input"
              placeholder="Search orders..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="table-responsive">
            <table className="table table-hover">
              <thead className="table-dark-blue">
                <tr>
                  <th>Order ID</th>
                  <th>Customer</th>
                  <th>Date</th>
                  <th>Total Amount</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredOrders.length > 0 ? (
                  filteredOrders.map((order) => (
                    <tr key={order.id}>
                      <td>#{order.id}</td>
                      <td>{order.customer?.name || 'N/A'}</td>
                      <td>{new Date(order.orderDate).toLocaleDateString()}</td>
                      <td>${order.totalAmount?.toFixed(2) || '0.00'}</td>
                      <td>{getStatusBadge(order.status)}</td>
                      <td>
                        <div className="btn-group" role="group">
                          <Link to={`/orders/view/${order.id}`} className="btn btn-sm btn-outline-info">
                            View
                          </Link>
                          <button
                            className="btn btn-sm btn-outline-success"
                            onClick={() => handleGenerateInvoice(order.id)}
                          >
                            Invoice
                          </button>
                          {order.status === 'pending' && (
                            <button
                              className="btn btn-sm btn-outline-primary"
                              onClick={() => handleStatusUpdate(order.id, 'completed')}
                            >
                              Complete
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="6" className="text-center">
                      {searchTerm ? 'No orders match your search.' : 'No orders found.'}
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

export default Orders;
