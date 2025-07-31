# 🚀 EMART - Vercel + PlanetScale Deployment Guide

## 🎯 What You'll Get (100% FREE)
- ✅ **Frontend:** `https://emart-invoice.vercel.app`
- ✅ **Backend:** `https://emart-api.vercel.app`
- ✅ **Database:** PlanetScale MySQL (FREE tier)
- ✅ **Cost:** $0 forever!
- ✅ **Perfect for Resume!**

---

## 📋 Prerequisites
- ✅ GitHub account
- ✅ Your EMART code pushed to GitHub
- ✅ Gmail account for email functionality

---

## Step 1: Setup PlanetScale Database (FREE)

### 1.1: Create PlanetScale Account
1. Go to [planetscale.com](https://planetscale.com)
2. Click **"Sign up"**
3. Sign up with GitHub (easiest)
4. Choose **FREE plan** (no credit card required)

### 1.2: Create Database
1. Click **"Create database"**
2. **Database name:** `emart-invoices`
3. **Region:** Choose closest to you
4. Click **"Create database"**

### 1.3: Get Database Connection Details
1. Go to your database dashboard
2. Click **"Connect"**
3. Select **"Java"** framework
4. Copy the connection details:
   ```
   DATABASE_URL=mysql://username:password@host/database?sslMode=REQUIRE
   DATABASE_USERNAME=your_username
   DATABASE_PASSWORD=your_password
   ```

### 1.4: Create Database Tables
1. Click **"Console"** in PlanetScale
2. Run this SQL to create tables:

```sql
-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Order Items table
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLyifkPiuGHG', 'admin@emart.com');
```

---

## Step 2: Deploy Backend to Vercel

### 2.1: Create Vercel Account
1. Go to [vercel.com](https://vercel.com)
2. Click **"Sign up"**
3. Sign up with GitHub
4. Import your repository

### 2.2: Deploy Backend
1. In Vercel dashboard, click **"New Project"**
2. Import your GitHub repository
3. **Framework Preset:** Other
4. **Root Directory:** `invoice-generator`
5. **Build Command:** `mvn clean package -DskipTests`
6. **Output Directory:** `target`

### 2.3: Set Environment Variables
In Vercel project settings, add these environment variables:

```
DATABASE_URL=mysql://your_planetscale_connection_string
DATABASE_USERNAME=your_planetscale_username  
DATABASE_PASSWORD=your_planetscale_password
EMAIL_USERNAME=your-gmail@gmail.com
EMAIL_PASSWORD=your-gmail-app-password
FRONTEND_URL=https://emart-invoice.vercel.app
SPRING_PROFILES_ACTIVE=production
```

### 2.4: Deploy
1. Click **"Deploy"**
2. Wait for deployment (5-10 minutes)
3. Your backend will be at: `https://emart-api.vercel.app`

---

## Step 3: Deploy Frontend to Vercel

### 3.1: Create New Project
1. Click **"New Project"** again
2. Import same GitHub repository
3. **Framework Preset:** React
4. **Root Directory:** `invoice-frontend`
5. **Build Command:** `npm run build`
6. **Output Directory:** `build`

### 3.2: Set Environment Variables
```
REACT_APP_API_URL=https://emart-api.vercel.app/api
```

### 3.3: Deploy
1. Click **"Deploy"**
2. Your frontend will be at: `https://emart-invoice.vercel.app`

---

## Step 4: Test Your Deployment

### 4.1: Test Backend
Visit: `https://emart-api.vercel.app/api/customers`
Should return: `[]` (empty array)

### 4.2: Test Frontend
1. Visit: `https://emart-invoice.vercel.app`
2. Login with: `admin` / `admin123`
3. Try creating customers, products, orders

### 4.3: Test Email (Optional)
1. Create an order
2. Try sending invoice email
3. Check if email arrives

---

## 🎯 Resume Entry

```
EMART Invoice Management System
Live Demo: https://emart-invoice.vercel.app
GitHub: https://github.com/sherinshibu101/PROJECT-E
Technologies: React.js, Spring Boot, MySQL, Vercel, PlanetScale

• Deployed full-stack application using modern cloud platforms (Vercel + PlanetScale)
• Implemented CI/CD pipeline with automatic deployments from GitHub
• Configured production environment with secure database connections
• Achieved 100% uptime with serverless architecture
```

---

## 🔧 Troubleshooting

### Backend Issues:
- Check Vercel function logs
- Verify environment variables
- Test database connection

### Frontend Issues:
- Check browser console
- Verify API URL in environment variables
- Test CORS configuration

### Database Issues:
- Check PlanetScale connection string
- Verify tables exist
- Test queries in PlanetScale console

---

## 🎉 Success!

Your EMART Invoice System is now:
- ✅ **Live on the internet**
- ✅ **100% FREE hosting**
- ✅ **Professional URLs**
- ✅ **Perfect for resume**
- ✅ **Ready for interviews**

**Show it off to employers!** 🚀
