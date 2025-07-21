# 🚀 EMART Quick Deployment Guide

## For Local Network (Office/Shop) Deployment

### Prerequisites ✅
- [ ] MySQL installed and running
- [ ] Java 17+ installed
- [ ] Node.js installed
- [ ] Maven installed (or use IDE)

### Step 1: Find Your Server IP 🌐
1. Open Command Prompt
2. Type: `ipconfig`
3. Note your IPv4 Address (e.g., 192.168.1.100)

### Step 2: Update Configuration 🔧
1. Edit `invoice-frontend/.env.production`
2. Replace `YOUR_SERVER_IP` with your actual IP:
   ```
   REACT_APP_API_URL=http://192.168.1.100:8080/api
   ```

### Step 3: Deploy Backend 🖥️
1. Double-click `deploy-backend.bat`
2. Wait for "Started InvoiceGeneratorApplication"
3. Backend runs on: `http://YOUR_IP:8080`

### Step 4: Deploy Frontend 🌐
1. Open new Command Prompt
2. Double-click `deploy-frontend.bat`
3. Frontend runs on: `http://YOUR_IP:3000`

### Step 5: Configure Firewall 🔒
1. Open Windows Defender Firewall
2. Allow Java and Node.js through firewall
3. Ensure ports 3000 and 8080 are open

### Step 6: Access from Other Computers 💻
- Open browser on any computer in the network
- Go to: `http://YOUR_SERVER_IP:3000`
- Login: admin / emartadmin@123

---

## ☁️ Cloud Deployment (Railway - Recommended)

### Backend on Railway:
1. Create account at railway.app
2. Connect GitHub repository
3. Deploy `invoice-generator` folder
4. Add MySQL database
5. Set environment variables

### Frontend on Netlify:
1. Build: `npm run build`
2. Drag `build` folder to netlify.com
3. Update API URL to Railway backend

---

## 📱 Access Points

Once deployed, access from:
- **Desktop computers**
- **Laptops** 
- **Tablets**
- **Mobile phones**

---

## 🆘 Troubleshooting

### Can't access from other computers?
- Check Windows Firewall
- Verify IP address
- Ensure both servers are running

### Backend errors?
- Check MySQL is running
- Verify database exists
- Check port 8080 is free

### Frontend errors?
- Check API URL in .env.production
- Verify backend is accessible
- Check port 3000 is free

---

## 🎉 Success!

Your EMART Invoice System is now deployed and ready for business use!
