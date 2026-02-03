# 🛒 ShopCRM - 電商進銷存後台管理系統

> 一個專為電商設計的全方位後台管理系統，整合多平台進銷存數據，提供從商品上架、訂單處理到財務分析的一站式解決方案。

## 📖 專案簡介 (Introduction)

本專案是一個基於 **Spring Boot** 開發的企業級後台管理系統。
旨在解決電商營運中「商品資訊分散」與「庫存管理困難」的痛點。
系統前端採用 **Tailwind CSS** 與 **Alpine.js** 打造響應式介面，支援 **Excel 拖曳批量上架**，並提供即時的儀表板數據分析。

## 🔑 測試帳號 (Demo Account)

如果您下載了專案想快速體驗，請使用以下測試帳號登入：

|   角色     |     Email       |  密碼  |
| ----------| --------------- | ------ |
| **管理員** | `test@mail.com` | `1234` |
----------------------------------------

## ✨ 核心功能 (Key Features)

### 1. 📊 儀表板 (Dashboard)
*   **營運摘要**：即時顯示本月訂單數、總收入、售出商品數與待處理訂單。
*   **數據視覺化**：使用 **Chart.js** 繪製銷售趨勢圖，一目了然掌握業績走勢。
*   **熱銷排行**：自動列出 Top 5 熱銷商品清單。

### 2. 📦 商品管理 (Product Management)
*   **CRUD 功能**：完整的商品列表、狀態管理 (上架/下架) 與編輯功能。
*   **Excel 批量匯入**：
  *   支援 **Drag & Drop (拖曳上傳)** 介面。
  *   即時上傳進度條顯示。
  *   自動驗證檔案格式與大小限制 (5MB)。
*   **範本下載**：提供標準 Excel 範本供使用者下載填寫。

### 3. 📝 訂單管理 (Order Management)
*   **狀態追蹤**：提供訂單列表與狀態篩選。
*   **訂單詳情**：可查看單一訂單的完整資訊。

### 4. 👥 顧客與分析 (Analytics)
*   **顧客分析**：展示顧客列表及購買紀錄。
*   **財務報表**：自動計算收益、成本與淨利，輔助營運決策。

---

## 🛠️ 技術棧 (Tech Stack)

本專案採用前後端整合架構 (Monolithic)，確保系統穩定性與開發效率。

### ☕ 後端核心 (Backend)
|       類別     |        技術 / 套件     |             說明           |
| --------------| --------------------- | ------------------------- |
| **Framework** | Spring Boot 3.2.5     | 核心應用程式框架             |
| **Language**  | Java 17               | 開發語言                    |
| **Database**  | MySQL 8.0             | 資料庫 (Schema: `shopcrm`)  |
| **ORM**       | Spring Data JPA       | 資料庫操作與交易管理          |
| **Security**  | Spring Security + JWT | 身份驗證與 API 保護          |
| **Excel Tool**| Apache POI            | 處理 Excel 報表匯入匯出       |

### 🎨 前端介面 (Frontend Architecture)
|        類別          |     技術 / 套件    |               說明                  |
| ------------------- | ----------------- | ---------------------------------- |
| **Template Engine** | **Thymeleaf**     | 伺服器端畫面渲染 (SSR)                |
| **CSS Framework**   | **Tailwind CSS**  | 現代化 Utility-first 樣式框架 (CDN)   |
| **Interactivity**   | **Alpine.js**     | 輕量級框架，處理 Sidebar/Dropdown 互動 |
| **Charts**          | **Chart.js**      | 數據視覺化圖表繪製                     |
| **Scripting**       | Vanilla JS (ES6+) | 處理檔案拖曳上傳與 AJAX 請求            |
| **Icons**           | FontAwesome       | 介面圖示庫                            |



## 🚀 如何執行 (Getting Started)

### 1. 環境準備
*   Java JDK 17+
*   Maven 3.6+
*   MySQL Server

### 2. 資料庫設定
請在 MySQL 中建立資料庫 `shopcrm`，並確認 `src/main/resources/application.properties` 設定：

```properties
# 資料庫連線設定
spring.datasource.url=jdbc:mysql://localhost:3306/shopcrm
spring.datasource.username=sabrina
spring.datasource.password=你的資料庫密碼

# JPA 設定
spring.jpa.hibernate.ddl-auto=update
```

### 3. 啟動專案

```bash
# 下載專案
git clone https://github.com/sabrina33333-hub/product-management.git

# 進入目錄
cd product-management

# 啟動 Spring Boot
mvn spring-boot:run
```

啟動成功後，請開啟瀏覽器前往：`http://localhost:8080`

---

## 👤 作者 (Author)

**Sabrina**
*   Github: [@sabrina33333-hub](https://github.com/sabrina33333-hub)
*   Project Link: [Product Management Repo](https://github.com/sabrina33333-hub/product-management/tree/main/product-management)

---

## 📄 授權 (License)

本專案僅供學習與展示使用。