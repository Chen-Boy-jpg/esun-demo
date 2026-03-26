#!/bin/bash

echo "=========================================="
echo "  🚀 正在啟動 Spring Boot 後端服務 (Mac/Linux)"
echo "=========================================="

# 1. 檢查並關閉佔用 8080 埠號的程序
PORT=8080
PID=$(lsof -t -i:$PORT)

if [ -z "$PID" ]; then
    echo "✅ 埠號 $PORT 目前是空閒的"
else
    echo "⚠️ 偵測到 $PORT 已被佔用 (PID: $PID)，正在關閉舊服務..."
    kill -9 $PID
    sleep 1
fi

# 2. 賦予 mvnw 執行權限並打包
echo "📦 正在編譯並打包專案 (跳過測試)..."
chmod +x mvnw
./mvnw clean package -DskipTests

# 檢查打包是否成功
if [ $? -ne 0 ]; then
    echo "❌ 打包失敗，請檢查程式碼！"
    exit 1
fi

# 3. 執行產出的 JAR 檔
echo "🌟 專案啟動中..."
java -jar target/*.jar