@echo off
chcp 65001 >nul
TITLE 前端開發伺服器啟動器

echo ==========================================
echo   🌐 正在啟動 Vue 前端開發環境 (Windows)
echo ==========================================

:: 1. 檢查 node_modules 是否存在，若無則自動安裝
if not exist "node_modules\" (
    echo 📦 偵測到尚未安裝套件，正在執行 npm install...
    call npm install
)

:: 2. 啟動開發伺服器
echo 🚀 正在啟動 Vite / Dev Server...
call npm run dev

pause