# EMART Invoice System - Deployment Guide

## 🚀 Local Network Deployment (Recommended for Small Business)

This guide helps you deploy EMART on your local network so multiple computers can access it.

### Prerequisites
- Windows computer to act as server
- MySQL installed and running
- Java 17+ installed
- Node.js installed

### Step 1: Find Your Server IP Address
1. Open Command Prompt (cmd)
2. Type: `ipconfig`
3. Look for "IPv4 Address" (usually something like 192.168.1.100)
4. Write down this IP address

### Step 2: Configure Backend for Network Access

1. **Update application.properties:**
   ```properties
   # Change server host to accept connections from any IP
   server.address=0.0.0.0
   server.port=8080
   
   # Update CORS to allow your network
   # Add this to your existing configuration
   ```

2. **Update CORS Configuration:**
   - Edit `CorsConfig.java`
   - Change `allowedOrigins` to include your network

### Step 3: Configure Frontend

1. **Update .env.production file:**
   ```
   REACT_APP_API_URL=http://YOUR_SERVER_IP:8080/api
   ```
   Replace `YOUR_SERVER_IP` with the IP from Step 1

### Step 4: Build Frontend for Production

1. Open terminal in `invoice-frontend` folder
2. Run: `npm run build`
3. This creates a `build` folder with production files

### Step 5: Deploy Backend

1. **Package the Spring Boot application:**
   ```bash
   cd invoice-generator
   mvn clean package
   ```

2. **Run the JAR file:**
   ```bash
   java -jar target/invoice-generator-1.0-SNAPSHOT.jar
   ```

### Step 6: Serve Frontend

**Option A: Using Node.js serve (Recommended)**
1. Install serve globally: `npm install -g serve`
2. In `invoice-frontend` folder: `serve -s build -l 3000`

**Option B: Using Python (if you have Python)**
1. In `invoice-frontend/build` folder
2. Run: `python -m http.server 3000`

### Step 7: Configure Windows Firewall

1. Open Windows Defender Firewall
2. Click "Allow an app or feature through Windows Defender Firewall"
3. Click "Change Settings" → "Allow another app"
4. Add Java and Node.js to allowed apps
5. Make sure ports 3000 and 8080 are open

### Step 8: Access from Other Computers

From any computer on the same network:
- Open browser
- Go to: `http://YOUR_SERVER_IP:3000`
- Login with: admin / emartadmin@123

---

## ☁️ Cloud Deployment Options

### Option 1: Railway (Easiest Cloud Deployment)

**Backend Deployment:**
1. Create account at railway.app
2. Connect your GitHub repository
3. Deploy Spring Boot app
4. Add MySQL database service
5. Configure environment variables

**Frontend Deployment:**
1. Build frontend: `npm run build`
2. Deploy to Netlify or Vercel
3. Update API URL to Railway backend URL

### Option 2: Heroku

**Backend:**
1. Create Heroku account
2. Install Heroku CLI
3. Create new app: `heroku create emart-backend`
4. Add MySQL addon: `heroku addons:create jawsdb:kitefin`
5. Deploy: `git push heroku main`

**Frontend:**
1. Build and deploy to Netlify/Vercel
2. Update API URL to Heroku backend

### Option 3: AWS (Advanced)

**Backend:**
- Deploy to AWS Elastic Beanstalk
- Use AWS RDS for MySQL database

**Frontend:**
- Deploy to AWS S3 + CloudFront

---

## 🔧 Production Configuration Checklist

### Backend Security
- [ ] Change default passwords
- [ ] Enable HTTPS
- [ ] Configure proper CORS origins
- [ ] Set up database backups
- [ ] Configure logging

### Frontend Security
- [ ] Build for production
- [ ] Enable HTTPS
- [ ] Configure proper API URLs
- [ ] Remove debug logs

### Database
- [ ] Create production database
- [ ] Run setup.sql script
- [ ] Create admin user
- [ ] Set up regular backups

---

## 📱 Mobile Access

Once deployed, users can access EMART from:
- Desktop computers
- Laptops
- Tablets
- Mobile phones (responsive design)

---

## 🆘 Troubleshooting

### Common Issues:

1. **Can't access from other computers:**
   - Check Windows Firewall settings
   - Verify IP address is correct
   - Ensure server is running

2. **Backend connection errors:**
   - Check if Spring Boot is running on port 8080
   - Verify database connection
   - Check CORS configuration

3. **Frontend not loading:**
   - Verify frontend server is running on port 3000
   - Check if build was successful
   - Verify API URL configuration

### Support Commands:

```bash
# Check if ports are open
netstat -an | findstr :8080
netstat -an | findstr :3000

# Check Java processes
jps -l

# Check Node processes
tasklist | findstr node
```

---

## 📞 Need Help?

If you encounter issues:
1. Check the troubleshooting section
2. Verify all prerequisites are installed
3. Ensure firewall settings are correct
4. Check server logs for error messages
