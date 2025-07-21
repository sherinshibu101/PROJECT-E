# 🔒 EMART Security Setup Guide

## ⚠️ IMPORTANT: Complete this setup before deployment!

### Step 1: Configure Database Credentials

1. **Copy the template:**
   ```bash
   cp invoice-generator/src/main/resources/application.properties.template invoice-generator/src/main/resources/application.properties
   ```

2. **Edit application.properties and replace:**
   - `YOUR_DB_USERNAME` → Your MySQL username (usually `root`)
   - `YOUR_DB_PASSWORD` → Your MySQL password
   - `YOUR_GMAIL_ADDRESS` → Your Gmail address
   - `YOUR_GMAIL_APP_PASSWORD` → Your Gmail app password

### Step 2: Configure Frontend Environment

1. **For local development:**
   ```bash
   cp invoice-frontend/.env.template invoice-frontend/.env.local
   ```

2. **For production deployment:**
   ```bash
   cp invoice-frontend/.env.template invoice-frontend/.env.production
   ```

3. **Edit the environment file and replace:**
   - `YOUR_SERVER_IP` → Your actual server IP address

### Step 3: Set Up Gmail App Password

1. **Enable 2-Factor Authentication:**
   - Go to Google Account settings
   - Security → 2-Step Verification → Turn On

2. **Generate App Password:**
   - Security → App passwords
   - Select "Mail" and generate password
   - Use this 16-character password in application.properties

### Step 4: Environment Variables (Alternative)

Instead of editing application.properties, you can set environment variables:

**Windows:**
```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_password
set EMAIL_USERNAME=your-email@gmail.com
set EMAIL_PASSWORD=your-app-password
```

**Linux/Mac:**
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export EMAIL_USERNAME=your-email@gmail.com
export EMAIL_PASSWORD=your-app-password
```

### Step 5: Verify Security

✅ **Check these files are NOT in your repository:**
- `application.properties` (with real credentials)
- `.env.local`
- `.env.production` (with real IPs)
- Any files with passwords or sensitive data

✅ **Check these files ARE in your repository:**
- `application.properties.template`
- `.env.template`
- `.gitignore` files
- This security guide

### Step 6: Test Configuration

1. **Start backend:**
   ```bash
   cd invoice-generator
   mvn spring-boot:run
   ```

2. **Start frontend:**
   ```bash
   cd invoice-frontend
   npm start
   ```

3. **Test login:**
   - Username: admin
   - Password: emartadmin@123

---

## 🚨 Security Checklist

Before pushing to GitHub:

- [ ] Removed real passwords from all files
- [ ] Created .gitignore files
- [ ] Created template files
- [ ] Tested with environment variables
- [ ] Verified sensitive files are ignored
- [ ] Updated deployment scripts to use templates

---

## 🔐 Production Security Recommendations

1. **Change default admin password**
2. **Use HTTPS in production**
3. **Set up database backups**
4. **Use strong database passwords**
5. **Regularly update dependencies**
6. **Monitor access logs**
7. **Use environment variables for all secrets**

---

## 📞 Need Help?

If you encounter issues:
1. Check all template files are copied correctly
2. Verify environment variables are set
3. Ensure Gmail app password is correct
4. Check database connection settings
