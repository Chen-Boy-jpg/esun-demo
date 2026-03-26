#!/bin/bash

echo "=========================================="
echo "  🌐 正在啟動 Vue 前端開發環境 (Mac/Linux)"
echo "=========================================="

# 1. 檢查 node_modules 是否存在
if [ ! -d "node_modules" ]; then
    echo "📦 偵測到尚未安裝套件，正在執行 npm install..."
    npm install
fi

# 2. 啟動開發伺服器
echo "🚀 正在啟動 Vite / Dev Server..."
npm run dev