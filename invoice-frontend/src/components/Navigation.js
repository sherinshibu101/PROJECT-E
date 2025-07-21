import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navigation = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark-blue">
      <div className="container">
        <Link className="navbar-brand" to="/">
          <strong>Dashboard</strong>
        </Link>

        <div className="navbar-nav ms-auto d-flex align-items-center">
          <Link className="nav-link" to="/customers">Customers</Link>
          <Link className="nav-link" to="/products">Products</Link>
          <Link className="nav-link" to="/orders">Orders</Link>

          <div className="nav-item dropdown">
            <span className="navbar-text me-3">
              Welcome, <strong>{user?.username || 'User'}</strong>
            </span>
            <button
              className="btn btn-outline-light btn-sm"
              onClick={handleLogout}
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;