
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    company_name VARCHAR(100),
    tax_id VARCHAR(20) COMMENT 'RFC/CIF/NIT según país',
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    mobile VARCHAR(20),
    address_street VARCHAR(100),
    address_city VARCHAR(50),
    address_state VARCHAR(50),
    address_postal_code VARCHAR(20),
    address_country VARCHAR(50) DEFAULT 'México',
    customer_type ENUM('individual', 'business') NOT NULL,
    status ENUM('active', 'inactive', 'lead') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    notes TEXT
);

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('admin', 'sales', 'support', 'manager', 'warehouse') NOT NULL,
    department VARCHAR(50),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id INT,
    brand_id INT,
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2),
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    is_service BOOLEAN DEFAULT FALSE,
    is_digital BOOLEAN DEFAULT FALSE,
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 5,
    weight DECIMAL(10,2) COMMENT 'En gramos',
    dimensions VARCHAR(50) COMMENT 'Formato: Largo x Ancho x Alto en cm',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES product_categories(category_id),
    FOREIGN KEY (brand_id) REFERENCES product_brands(brand_id)
);

CREATE TABLE product_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    parent_id INT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (parent_id) REFERENCES product_categories(category_id)
);

CREATE TABLE product_brands (
    brand_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    website VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id INT,
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2),
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    duration_minutes INT COMMENT 'Duración estimada en minutos',
    is_preventive BOOLEAN DEFAULT FALSE,
    is_corrective BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES service_categories(category_id)
);

CREATE TABLE service_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    parent_id INT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (parent_id) REFERENCES service_categories(category_id)
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    user_id INT COMMENT 'Usuario que creó/atiende la orden',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'processing', 'completed', 'cancelled', 'refunded') DEFAULT 'pending',
    total_amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    payment_method ENUM('cash', 'credit_card', 'debit_card', 'bank_transfer', 'paypal', 'other'),
    payment_status ENUM('pending', 'paid', 'partially_paid', 'refunded') DEFAULT 'pending',
    shipping_address TEXT,
    notes TEXT,
    source ENUM('web', 'phone', 'in_person', 'crm') NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT,
    service_id INT,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    discount DECIMAL(5,2) DEFAULT 0.00,
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id),
    CHECK (product_id IS NOT NULL OR service_id IS NOT NULL)
);

CREATE TABLE quotes (
    quote_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    user_id INT COMMENT 'Usuario que creó la cotización',
    quote_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiration_date DATE,
    status ENUM('draft', 'sent', 'accepted', 'rejected', 'expired') DEFAULT 'draft',
    total_amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE quote_items (
    quote_item_id INT PRIMARY KEY AUTO_INCREMENT,
    quote_id INT NOT NULL,
    product_id INT,
    service_id INT,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    discount DECIMAL(5,2) DEFAULT 0.00,
    notes TEXT,
    FOREIGN KEY (quote_id) REFERENCES quotes(quote_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id),
    CHECK (product_id IS NOT NULL OR service_id IS NOT NULL)
);

CREATE TABLE support_tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    user_id INT COMMENT 'Usuario asignado',
    subject VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('open', 'in_progress', 'on_hold', 'resolved', 'closed') DEFAULT 'open',
    priority ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at DATETIME,
    resolution TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE activity_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    customer_id INT,
    activity_type VARCHAR(50) NOT NULL,
    description TEXT,
    related_table VARCHAR(50),
    related_id INT,
    ip_address VARCHAR(45),a
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE api_integrations (
    integration_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    api_key VARCHAR(255),
    api_secret VARCHAR(255),
    base_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    settings JSON,
    last_sync DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

