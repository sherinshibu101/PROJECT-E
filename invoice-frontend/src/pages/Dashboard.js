import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { customerAPI, productAPI, orderAPI } from '../services/api';
import '../styles/Dashboard.css';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalCustomers: 0,
    totalProducts: 0,
    totalOrders: 0,
    pendingOrders: 0,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      console.log('🚀 Starting to fetch dashboard data...');
      console.log('🔗 Backend URL:', 'http://localhost:8080/api');

      console.log('📞 Calling customerAPI.getAll()...');
      const customersResponse = await customerAPI.getAll();
      console.log('✅ Customers response:', customersResponse.data);

      console.log('📞 Calling productAPI.getAll()...');
      const productsResponse = await productAPI.getAll();
      console.log('✅ Products response:', productsResponse.data);

      console.log('📞 Calling orderAPI.getAll()...');
      const ordersResponse = await orderAPI.getAll();
      console.log('✅ Orders response:', ordersResponse.data);

      const pendingOrders = ordersResponse.data.filter(order => order.status === 'pending').length;

      setStats({
        totalCustomers: customersResponse.data.length,
        totalProducts: productsResponse.data.length,
        totalOrders: ordersResponse.data.length,
        pendingOrders: pendingOrders,
      });

      console.log('🎉 Dashboard data loaded successfully!');
    } catch (error) {
      console.error('❌ DETAILED ERROR:', error);
      console.error('❌ Error message:', error.message);
      console.error('❌ Error response:', error.response);

      if (error.response) {
        console.error('❌ Status:', error.response.status);
        console.error('❌ Data:', error.response.data);
      }

      setError(`Failed to load dashboard data. Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="container">
          <div className="text-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-2">Loading dashboard data...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container">
        <div className="container">
          <div className="alert alert-danger" role="alert">
            <h4 className="alert-heading">Connection Error</h4>
            <p>{error}</p>
            <button className="btn btn-dark-blue" onClick={fetchDashboardData}>
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <h1 className="emart-title text-center">EMART INVOICE SYSTEM</h1>
      <div className="container">
        <div className="dashboard-content">
          <h2 className="text-center mb-4">DASHBOARD</h2>

          {/* Statistics Cards */}
          <div className="row mb-5">
            <div className="col-md-3 mb-3">
              <div className="stat-card">
                <h5>TOTAL CUSTOMERS</h5>
                <h2>{stats.totalCustomers}</h2>
              </div>
            </div>

            <div className="col-md-3 mb-3">
              <div className="stat-card">
                <h5>TOTAL PRODUCTS</h5>
                <h2>{stats.totalProducts}</h2>
              </div>
            </div>

            <div className="col-md-3 mb-3">
              <div className="stat-card">
                <h5>TOTAL ORDERS</h5>
                <h2>{stats.totalOrders}</h2>
              </div>
            </div>

            <div className="col-md-3 mb-3">
              <div className="stat-card">
                <h5>PENDING ORDERS</h5>
                <h2>{stats.pendingOrders}</h2>
              </div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="row justify-content-center mt-4">
            <div className="col-md-4 mb-2">
              <Link to="/customers/add" className="btn btn-dark-blue w-100">ADD CUSTOMER</Link>
            </div>
            <div className="col-md-4 mb-2">
              <Link to="/products/add" className="btn btn-dark-blue w-100">ADD PRODUCT</Link>
            </div>
            <div className="col-md-4 mb-2">
              <Link to="/orders/create" className="btn btn-dark-blue w-100">CREATE ORDER</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;