# 全端社群貼文系統 (Spring Boot + Vue 3)

這是一個採用**前後端分離架構**的社群平台，支援使用者註冊、個人資料維護、發布圖文貼文以及即時留言互動。

系統核心邏輯（CRUD）由 PostgreSQL 的 **Stored Procedures（預存程序）** 驅動，確保資料操作的安全性與一致性。

---

##  技術棧

###  前端

* Vue 3 (Composition API)
* Vite
* TypeScript
* Axios
* Tailwind CSS（介面美化）

###  後端

* Java 17
* Spring Boot 3.x
* Spring Security（JWT 驗證 + BCrypt 加密）
* Spring Data JPA（呼叫預存程序）

###  基礎設施

* PostgreSQL 16（資料持久化）
* Docker & Docker Compose（環境一鍵建置）

---

##  快速啟動（Docker 模式）

本專案已**完全容器化**，無需在本地安裝 Java、Node.js 或 PostgreSQL。

---

###  啟動環境

在專案根目錄執行：

```bash
docker-compose up --build
```

####  自動完成：

* 啟動 PostgreSQL 並執行 `init.sql`（DDL + 測試資料）
* 編譯並啟動 Spring Boot 後端（Port: 8080）
* 建置並啟動 Vue 3 前端（Port: 5173）

---

###  常用指令

####  停止系統

```bash
docker-compose down
```

####  重置資料庫

```bash
docker-compose down -v
```

> ⚠️ 會刪除所有資料與圖片

####  查看後端日誌

```bash
docker logs -f esun_backend
```

---

##  專案結構

```bash
.
├── backend/        # Spring Boot 後端
├── frontend/       # Vue 3 前端
├── db/
│   └── init.sql    # DDL + Stored Procedures + 測試資料
└── uploads/        # 使用者上傳圖片（Docker Volume）
```

---

##  測試帳號

系統初始化後會建立 3 位測試使用者：

| 使用者 | 帳號 (電話)    | 密碼          | 描述         |
| --- | ---------- | ----------- | ---------- |
| 美美  | 0922222222 | password123 | 前端工程師 / 貓奴 |
| 阿龍  | 0911111111 | password123 | 攝影愛好者      |
| 小傑  | 0933333333 | password123 | Java 工程師   |

>  所有密碼皆使用 BCrypt 加密

---


##  專案說明

本專案展示：

* 前後端分離架構設計
* 安全驗證機制（JWT）
* PostgreSQL 預存程序應用
* Docker 容器化部署


