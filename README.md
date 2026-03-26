#  全端社群貼文系統 (Spring Boot + Vue 3)

這是一個採用**前後端分離架構**的簡易社群平台，支援使用者註冊、發布純文字貼文、以及即時留言互動。

系統核心邏輯（CRUD）由 PostgreSQL 的 **Stored Procedures（預存程序）** 驅動，確保資料操作的安全性與一致性。

---

##  技術棧

### 前端

* Vue 3 (Composition API)
* Vite
* TypeScript
* TanStack Query
* Axios

### 後端

* Java 17
* Spring Boot 3.x
* Spring Security（BCrypt）

### 資料庫

* PostgreSQL 16+

### 建構工具

* Maven
* NPM

---

##  快速啟動

### 1️ 資料庫準備 (PostgreSQL)

請先建立資料庫，並依序執行：

```sql
DDL.sql  -- 建立資料表 (DDL)
DML.sql    -- 初始化資料 (DML)
```

#### 🔹 注意事項

* 系統使用 **plpgsql 預存程序**
* 請確保資料庫使用者具備執行權限
* 執行後會建立 3 位測試使用者

---

### 2️⃣ 後端啟動

📁 進入 `backend` 目錄：

#### Windows

```bash
run.bat
```

#### Mac / Linux

```bash
./run.sh
```

👉 功能：

* 檢查 8080 port
* 自動 build
* 啟動 Spring Boot

---

### 3️ 前端啟動

📁 進入 `frontend` 目錄：

#### Windows

```bash
run.bat
```

#### Mac / Linux

```bash
./run.sh
```

👉 功能：

* 檢查 `node_modules`
* 自動執行 `yarn install`

---

## 🔐 測試帳號

| 使用者 | 帳號 (電話)    | 密碼          | 描述       |
| --- | ---------- | ----------- | -------- |
| 美美  | 0922222222 | password123 | 前端工程師    |
| 阿龍  | 0911111111 | password123 | 攝影愛好者    |
| 小傑  | 0933333333 | password123 | Java 工程師 |

> 🔒 密碼皆使用 BCrypt 加密

---


這是一個展示：

* 前後端分離架構
* 安全驗證設計
* 資料庫預存程序應用

的完整實作專案。
