# 🚀 EMART Invoice System - Deployment Guide

## 📋 Prerequisites

### Required Software:
- ✅ Java 17 or higher
- ✅ Node.js 16 or higher
- ✅ MySQL 8.0 or higher
- ✅ Git (for cloning)

### Check Your Versions:
```bash
java -version
node -version
npm -version
mysql --version
```

---

## 🏠 Option 1: Local Network Deployment (FREE)

### Step 1: Database Setup

1. **Start MySQL Service:**
   ```bash
   # Windows (Run as Administrator)
   net start mysql80
   
   # Or start MySQL Workbench and connect
   ```

2. **Create Database:**
   ```sql
   CREATE DATABASE emart_invoices;
   USE emart_invoices;
   
   -- Run the setup.sql file from database folder
   SOURCE C:/Users/HP/Desktop/PROJECTS/EMART/database/setup.sql;
   ```

### Step 2: Backend Configuration

1. **Create application.properties:**
   ```bash
   cd invoice-generator/src/main/resources/
   copy application.properties.template application.properties
   ```

2. **Edit application.properties:**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/emart_invoices
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   
   # Server Configuration for Network Access
   server.port=8080
   server.address=0.0.0.0
   
   # Email Configuration (Optional - for invoice emails)
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

### Step 3: Build and Run Backend

```bash
# Navigate to backend folder
cd invoice-generator

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

**Backend will be available at:** `http://YOUR_IP_ADDRESS:8080`

### Step 4: Frontend Configuration

1. **Find Your IP Address:**
   ```bash
   # Windows
   ipconfig
   # Look for IPv4 Address (e.g., 192.168.1.100)
   ```

2. **Update Frontend API URL:**
   ```bash
   cd invoice-frontend/src
   ```

3. **Create .env file:**
   ```env
   REACT_APP_API_URL=http://YOUR_IP_ADDRESS:8080
   ```

### Step 5: Build and Run Frontend

```bash
# Navigate to frontend folder
cd invoice-frontend

# Install dependencies
npm install

# Build for production
npm run build

# Serve the built files
npx serve -s build -l 3000
```

**Frontend will be available at:** `http://YOUR_IP_ADDRESS:3000`

### Step 6: Access from Other Devices

1. **Find your computer's IP address** (e.g., 192.168.1.100)
2. **On any device on same network:**
   - Open browser
   - Go to `http://192.168.1.100:3000`
   - Use the invoice system!

---

## ☁️ Option 2: Cloud Deployment (PROFESSIONAL)

### Recommended Platforms:

#### A. Railway (Easiest)
- **Cost:** $5/month
- **Features:** Auto-deployment from GitHub
- **Database:** Included MySQL

#### B. Heroku (Popular)
- **Cost:** $7/month
- **Features:** Easy deployment
- **Database:** Add-on required

#### C. DigitalOcean (Most Control)
- **Cost:** $6/month
- **Features:** Full server control
- **Database:** Self-managed

---

## 🔧 Troubleshooting

### Common Issues:

#### Backend won't start:
- ✅ Check MySQL is running
- ✅ Verify database credentials
- ✅ Ensure port 8080 is free

#### Frontend can't connect:
- ✅ Check backend is running on correct IP
- ✅ Update REACT_APP_API_URL
- ✅ Check firewall settings

#### Database connection failed:
- ✅ Verify MySQL service is running
- ✅ Check username/password
- ✅ Ensure database exists

---

## 🎯 Production Checklist

### Before Going Live:
- [ ] Change default passwords
- [ ] Enable HTTPS
- [ ] Set up regular backups
- [ ] Configure proper logging
- [ ] Test all functionality
- [ ] Document admin procedures

---

## 📞 Support

If you encounter issues:
1. Check the logs in terminal
2. Verify all services are running
3. Test database connection
4. Check network connectivity

**Your EMART system is ready for deployment!** 🚀
