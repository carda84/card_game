@echo off
echo starting backend...
start "Backend" powershell -ExecutionPolicy Bypass -File "c:\Users\Lenovo\Desktop\card_game\run_backend.ps1"

echo wait for backend to start（5秒）...
timeout /t 5 /nobreak >nul

echo starting frontend service...\r
cd /d "c:\Users\Lenovo\Desktop\card_game\card\frontend"
start "Frontend" npx vite --host

echo started!\r
pause