# 電商後台管理系統 (E-commerce Backend Management System)

這是一個功能完整的電商後台管理系統，基於 Java Spring Boot 和 Thymeleaf 技術棧構建。系統旨在為電商管理員提供一個強大、直觀的平台，用於管理商品、訂單、客戶、供應商，並提供數據分析儀表板。

## ✨ 主要功能 (Features)

-   **📊 儀表板 (Dashboard)**: 視覺化呈現關鍵業務指標 (KPIs)，如總收入、訂單數、新客戶等。
-   **📦 商品管理 (Product Management)**:
    -   完整的商品 CRUD (新增、讀取、更新、刪除) 功能。
    -   支援商品分類、庫存和價格管理。
    -   商品列表搜尋與分頁。
-   **🛒 訂單管理 (Order Management)**:
    -   查看所有訂單列表與狀態。
    -   檢視單一訂單的詳細資訊，包括商品項目和客戶資料。
    -   訂單狀態追蹤。
-   **👥 客戶管理 (Customer Management)**: 統一管理客戶資料。
-   **🏭 供應商管理 (Vendor Management)**: 管理供應商資訊。
-   **🔐 使用者認證 (Authentication)**:
    -   基於 JWT (JSON Web Token) 的安全登入/登出機制。
    -   區分 API 和 Web 視圖的安全性設定。
-   **📈 收益分析 (Analytics)**: 提供基於銷售數據的收益、成本和利潤分析報告。

## 🛠️ 技術棧 (Technology Stack)

-   **後端 (Backend)**:
    -   Java 17+
    -   Spring Boot 3.x
    -   Spring MVC
    -   Spring Data JPA (Hibernate)
    -   Spring Security
-   **前端 (Frontend)**:
    -   Thymeleaf (伺服器端渲染)
    -   Tailwind CSS & Bootstrap (用於頁面樣式)
    -   JavaScript
-   **資料庫 (Database)**:
    -   MySQL (可透過 `application.properties` 輕鬆更換為其他關聯式資料庫)
-   **建置工具 (Build Tool)**:
    -   Maven

## 🏛️ 架構亮點 (Architecture Highlights)

本專案在架構設計上採用了幾個最佳實踐，以提高可維護性和擴展性：

1.  **API 與視圖控制器分離**:
    -   `com.example.productmanagement.controller`: 此套件下的控制器 (`@RestController`) 專門提供 RESTful API，用於未來與 App 客戶端或現代前端框架 (如 React, Vue) 的整合。
    -   `com.example.productmanagement.controller.view`: 此套件下的控制器 (`@Controller`) 專門處理伺服器端渲染的 Thymeleaf 視圖，服務於傳統的 Web 頁面。
    這種分離設計使得系統能同時支持兩種模式，且職責清晰。

2.  **DTO 模式 (Data Transfer Object)**:
    -   系統廣泛使用 DTO (如 `ProductRequest`) 來接收和傳輸資料，有效隔離了資料庫實體 (Entity) 與外部世界的交互，提高了 API 的穩定性和安全性。

3.  **服務層介面化**:
    -   所有核心業務邏輯 (Service) 都定義了介面，便於進行單元測試 (Mocking) 和未來的功能擴展 (如引入快取)。

## 🚀 快速開始 (Getting Started)

請遵循以下步驟在您的本機環境中啟動專案。

### 1. 環境準備

請確保您已安裝以下軟體：
-   JDK 17 或更高版本
-   Maven 3.6+
-   MySQL 8.0+ (或其他關聯式資料庫)

### 2. 複製專案

```bash
git clone https://your-repository-url.git
cd your-project-directory
```

### 3. 資料庫設定

1.  登入您的 MySQL 資料庫。
2.  建立一個新的資料庫。

    ```sql
    CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```

### 4. 專案配置

1.  開啟 `src/main/resources/application.properties` 文件。
2.  修改資料庫連線設定以符合您的環境。

    ```properties
    # DataSource settings
    spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=Asia/Taipei
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password

    # JPA settings
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

    # JWT Secret Key (請務必更換為一個複雜且隨機的字串)
    application.security.jwt.secret-key=your-super-secret-and-long-random-key
    ```

### 5. 啟動應用程式

使用 Maven 啟動 Spring Boot 應用程式。

```bash
mvn spring-boot:run
```

應用程式啟動後，預設將在 `8080` 連接埠上運行。

### 6. 訪問系統

-   **登入頁面**: [http://localhost:8080/login](http://localhost:8080/login)
-   **主儀表板**: [http://localhost:8080/main](http://localhost:8080/main) (登入後)

## 📁 專案結構

