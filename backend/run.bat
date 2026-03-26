@echo off
:: 強制使用 UTF-8 編碼顯示，解決亂碼問題
chcp 65001 >nul
TITLE 後端服務啟動器

echo ==========================================
echo   🚀 正在啟動 Spring Boot 後端服務 (Windows)
echo ==========================================

:: 1. 檢查並關閉佔用 8080 埠號的程序
set PORT=8080
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%PORT%') do (
    echo ⚠️ 偵測到 %PORT% 埠號被佔用，正在關閉 PID: %%a
    taskkill /f /pid %%a
)

:: 2. 使用 Maven Wrapper 進行打包
echo 📦 正在編譯並打包專案 (跳過測試)...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ❌ 打包失敗，請檢查程式碼！
    pause
    exit /b %ERRORLEVEL%
)

:: 3. 執行產出的 JAR 檔 (解決 Windows 不支援 *.jar 的問題)
echo 🌟 專案啟動中...
:: 透過 dir 指令抓取 target 底下最新的 jar 檔名並執行
for /f "delims=" %%i in ('dir /b target\*.jar ^| findstr /v "original"') do (
    set TARGET_JAR=target\%%i
)

if defined TARGET_JAR (
    echo ✅ 找到 JAR 檔: %TARGET_JAR%
    java -jar %TARGET_JAR%
) else (
    echo ❌ 找不到產出的 JAR 檔，請確認 target 資料夾內容。
)

pause