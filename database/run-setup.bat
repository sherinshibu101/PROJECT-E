@echo off
echo Setting up eMart Invoice Database...
echo.
echo Please enter your MySQL root password when prompted.
echo.
mysql -u root -p < setup.sql
echo.
echo Database setup complete!
echo.
pause
