import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderAPI, customerAPI, productAPI, invoiceAPI } from '../services/api';
import '../styles/Pages.css';

const OrderForm = ({ isViewOnly = false }) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = !!id;

  const [order, setOrder] = useState({
    customerId: '',
    orderItems: [{ productId: '', productName: '', quantity: 1, price: 0, subtotal: 0 }],
    totalAmount: 0,
    status: 'pending'
  });
  
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [emailData, setEmailData] = useState({ recipientEmail: '', subject: '', message: '' });
  const [showEmailForm, setShowEmailForm] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch customers
        const customersResponse = await customerAPI.getAll();
        setCustomers(customersResponse.data);
        
        // Fetch products if not in view mode
        if (!isViewOnly) {
          const productsResponse = await productAPI.getAll();
          setProducts(productsResponse.data);
        }
        
        // Fetch order if in edit/view mode
        if (isEditMode) {
          const orderResponse = await orderAPI.getById(id);
          const orderData = orderResponse.data;
          
          // Format order data
          setOrder({
            customerId: orderData.customer?.id || '',
            orderItems: orderData.orderItems || [],
            totalAmount: orderData.totalAmount || 0,
            status: orderData.status || 'pending',
            orderDate: orderData.orderDate
          });
          
          // Set email data
          if (orderData.customer) {
            setEmailData({
              recipientEmail: orderData.customer.email,
              subject: `Your Invoice #${orderData.id} from EMART`,
              message: `Thank you for your order. Please find your invoice attached.`
            });
          }
        }
      } catch (error) {
        console.error('Error fetching data:', error);
        setError('Failed to load data. Please try again.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, [id, isEditMode, isViewOnly]);

  const handleCustomerChange = (e) => {
    const customerId = e.target.value;
    setOrder({ ...order, customerId });
    
    // Update email if customer changes
    const selectedCustomer = customers.find(c => c.id.toString() === customerId);
    if (selectedCustomer) {
      setEmailData({
        ...emailData,
        recipientEmail: selectedCustomer.email
      });
    }
  };

  const handleProductSelect = (index, productId) => {
    const selectedProduct = products.find(p => p.id.toString() === productId);
    if (!selectedProduct) return;

    const updatedItems = [...order.orderItems];
    updatedItems[index] = {
      ...updatedItems[index],
      productId: selectedProduct.id,
      productName: selectedProduct.name,
      price: selectedProduct.currentPrice,
      subtotal: selectedProduct.currentPrice * updatedItems[index].quantity
    };

    setOrder({
      ...order,
      orderItems: updatedItems,
      totalAmount: calculateTotal(updatedItems)
    });
  };

  const handleQuantityChange = (index, quantity) => {
    const updatedItems = [...order.orderItems];
    const item = updatedItems[index];
    const newQuantity = parseInt(quantity) || 0;
    
    updatedItems[index] = {
      ...item,
      quantity: newQuantity,
      subtotal: item.price * newQuantity
    };
    
    setOrder({
      ...order,
      orderItems: updatedItems,
      totalAmount: calculateTotal(updatedItems)
    });
  };

  const addOrderItem = () => {
    setOrder({
      ...order,
      orderItems: [
        ...order.orderItems,
        { productId: '', productName: '', quantity: 1, price: 0, subtotal: 0 }
      ]
    });
  };

  const removeOrderItem = (index) => {
    const updatedItems = order.orderItems.filter((_, i) => i !== index);
    setOrder({
      ...order,
      orderItems: updatedItems,
      totalAmount: calculateTotal(updatedItems)
    });
  };

  const calculateTotal = (items) => {
    return items.reduce((sum, item) => sum + (item.subtotal || 0), 0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!order.customerId || order.orderItems.length === 0) {
      setError('Please select a customer and add at least one item.');
      return;
    }
    
    try {
      setSaving(true);

      const orderData = {
        customerId: parseInt(order.customerId),
        items: order.orderItems.map(item => ({
          productId: parseInt(item.productId),
          productName: item.productName,
          quantity: parseInt(item.quantity),
          price: parseFloat(item.price),
          subtotal: parseFloat(item.subtotal)
        })),
        totalAmount: parseFloat(order.totalAmount),
        status: order.status
      };

      console.log('Sending order data:', orderData);

      await orderAPI.create(orderData);
      navigate('/orders');
    } catch (error) {
      console.error('Error creating order:', error);
      console.error('Error response:', error.response?.data);

      let errorMessage = 'Failed to create order. Please try again.';
      if (error.response?.data) {
        errorMessage = `Failed to create order: ${error.response.data}`;
      } else if (error.response?.status === 400) {
        errorMessage = 'Invalid order data. Please check all fields and try again.';
      }

      setError(errorMessage);
      setSaving(false);
    }
  };

  const handleSendEmail = async (e) => {
    e.preventDefault();

    try {
      console.log('Sending email with data:', emailData);
      console.log('Order ID:', id);

      const response = await invoiceAPI.sendEmail(id, emailData);
      console.log('Email response:', response);

      alert('Invoice sent successfully!');
      setShowEmailForm(false);
    } catch (error) {
      console.error('Error sending email:', error);
      console.error('Error details:', error.response?.data);

      let errorMessage = 'Failed to send email. Please try again.';
      if (error.response?.data) {
        errorMessage = `Failed to send email: ${error.response.data}`;
      }

      alert(errorMessage);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="container">
          <div className="text-center p-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-2">Loading data...</p>
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
          <h2 className="page-title">
            {isViewOnly ? `Order #${id} Details` : 'Create New Order'}
          </h2>
          
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          {isViewOnly ? (
            // View Order Mode
            <div>
              <div className="detail-section">
                <h3>Order Information</h3>
                <div className="row">
                  <div className="col-md-6">
                    <p><strong>Order ID:</strong> #{id}</p>
                    <p><strong>Date:</strong> {new Date(order.orderDate).toLocaleString()}</p>
                  </div>
                  <div className="col-md-6">
                    <p><strong>Status:</strong> <span className={`status-badge status-${order.status}`}>{order.status.toUpperCase()}</span></p>
                    <p><strong>Total Amount:</strong> ${order.totalAmount.toFixed(2)}</p>
                  </div>
                </div>
              </div>
              
              <div className="detail-section">
                <h3>Customer Information</h3>
                {customers.find(c => c.id.toString() === order.customerId.toString()) && (
                  <div>
                    <p><strong>Name:</strong> {customers.find(c => c.id.toString() === order.customerId.toString()).name}</p>
                    <p><strong>Email:</strong> {customers.find(c => c.id.toString() === order.customerId.toString()).email}</p>
                    <p><strong>Phone:</strong> {customers.find(c => c.id.toString() === order.customerId.toString()).phone || 'N/A'}</p>
                    <p><strong>Address:</strong> {customers.find(c => c.id.toString() === order.customerId.toString()).address}</p>
                  </div>
                )}
              </div>
              
              <div className="detail-section">
                <h3>Order Items</h3>
                <div className="table-responsive">
                  <table className="table">
                    <thead className="table-dark-blue">
                      <tr>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Price</th>
                        <th>Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                      {order.orderItems.map((item, index) => (
                        <tr key={index}>
                          <td>{item.productName}</td>
                          <td>{item.quantity}</td>
                          <td>${item.price.toFixed(2)}</td>
                          <td>${item.subtotal.toFixed(2)}</td>
                        </tr>
                      ))}
                      <tr className="table-active">
                        <td colSpan="3" className="text-end"><strong>Total:</strong></td>
                        <td><strong>${order.totalAmount.toFixed(2)}</strong></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              
              <div className="d-flex justify-content-between mt-4">
                <button
                  className="btn btn-secondary"
                  onClick={() => navigate('/orders')}
                >
                  Back to Orders
                </button>
                <div>
                  <button
                    className="btn btn-success me-2"
                    onClick={() => window.open(`/api/invoices/pdf/${id}`, '_blank')}
                  >
                    View Invoice
                  </button>
                  <button
                    className="btn btn-dark-blue"
                    onClick={() => setShowEmailForm(!showEmailForm)}
                  >
                    Send Invoice
                  </button>
                </div>
              </div>
              
              {showEmailForm && (
                <div className="mt-4 p-3 border rounded">
                  <h4>Send Invoice Email</h4>
                  <form onSubmit={handleSendEmail}>
                    <div className="mb-3">
                      <label htmlFor="recipientEmail" className="form-label">Recipient Email</label>
                      <input
                        type="email"
                        className="form-control"
                        id="recipientEmail"
                        value={emailData.recipientEmail}
                        onChange={(e) => setEmailData({...emailData, recipientEmail: e.target.value})}
                        placeholder="Enter email address to send invoice"
                        required
                      />
                      <small className="text-muted">
                        You can change this email address to test with different recipients
                      </small>
                    </div>
                    <div className="mb-3">
                      <label htmlFor="subject" className="form-label">Subject</label>
                      <input
                        type="text"
                        className="form-control"
                        id="subject"
                        value={emailData.subject}
                        onChange={(e) => setEmailData({...emailData, subject: e.target.value})}
                        required
                      />
                    </div>
                    <div className="mb-3">
                      <label htmlFor="message" className="form-label">Message</label>
                      <textarea
                        className="form-control"
                        id="message"
                        rows="3"
                        value={emailData.message}
                        onChange={(e) => setEmailData({...emailData, message: e.target.value})}
                        required
                      ></textarea>
                    </div>
                    <button type="submit" className="btn btn-dark-blue">Send Email</button>
                  </form>
                </div>
              )}
            </div>
          ) : (
            // Create Order Mode
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="customerId" className="form-label">Customer *</label>
                <select
                  className="form-select"
                  id="customerId"
                  value={order.customerId}
                  onChange={handleCustomerChange}
                  required
                >
                  <option value="">Select a customer</option>
                  {customers.map(customer => (
                    <option key={customer.id} value={customer.id}>
                      {customer.name} ({customer.email})
                    </option>
                  ))}
                </select>
              </div>
              
              <h4 className="mt-4 mb-3">Order Items</h4>
              
              {order.orderItems.map((item, index) => (
                <div key={index} className="card mb-3">
                  <div className="card-body">
                    <div className="row">
                      <div className="col-md-5 mb-2">
                        <label className="form-label">Product</label>
                        <select
                          className="form-select"
                          value={products.find(p => p.name === item.productName)?.id || ''}
                          onChange={(e) => handleProductSelect(index, e.target.value)}
                          required
                        >
                          <option value="">Select a product</option>
                          {products.map(product => (
                            <option key={product.id} value={product.id}>
                              {product.name} (${product.currentPrice.toFixed(2)})
                            </option>
                          ))}
                        </select>
                      </div>
                      
                      <div className="col-md-2 mb-2">
                        <label className="form-label">Quantity</label>
                        <input
                          type="number"
                          className="form-control"
                          min="1"
                          value={item.quantity}
                          onChange={(e) => handleQuantityChange(index, e.target.value)}
                          required
                        />
                      </div>
                      
                      <div className="col-md-2 mb-2">
                        <label className="form-label">Price</label>
                        <div className="input-group">
                          <span className="input-group-text">$</span>
                          <input
                            type="text"
                            className="form-control"
                            value={item.price.toFixed(2)}
                            readOnly
                          />
                        </div>
                      </div>
                      
                      <div className="col-md-2 mb-2">
                        <label className="form-label">Subtotal</label>
                        <div className="input-group">
                          <span className="input-group-text">$</span>
                          <input
                            type="text"
                            className="form-control"
                            value={item.subtotal.toFixed(2)}
                            readOnly
                          />
                        </div>
                      </div>
                      
                      <div className="col-md-1 d-flex align-items-end mb-2">
                        {order.orderItems.length > 1 && (
                          <button
                            type="button"
                            className="btn btn-outline-danger"
                            onClick={() => removeOrderItem(index)}
                          >
                            <i className="bi bi-trash"></i>
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
              
              <div className="mb-4">
                <button
                  type="button"
                  className="btn btn-outline-primary"
                  onClick={addOrderItem}
                >
                  <i className="bi bi-plus-circle me-2"></i> Add Item
                </button>
              </div>
              
              <div className="row mb-4">
                <div className="col-md-6 offset-md-6">
                  <div className="card bg-light">
                    <div className="card-body">
                      <h5 className="card-title">Order Summary</h5>
                      <div className="d-flex justify-content-between">
                        <span>Total:</span>
                        <strong>${order.totalAmount.toFixed(2)}</strong>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div className="d-flex justify-content-between">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => navigate('/orders')}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-dark-blue"
                  disabled={saving}
                >
                  {saving ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                      Creating Order...
                    </>
                  ) : (
                    'Create Order'
                  )}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default OrderForm;
