-- Tabla maestra para multi-tenancy
CREATE TABLE tenant_clients (
    tenant_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Código único del tenant',
    company_name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(20) NOT NULL COMMENT 'RFC/CIF/NIT según país',
    status ENUM('active', 'suspended', 'pending') DEFAULT 'pending',
    db_schema VARCHAR(50) UNIQUE NOT NULL COMMENT 'Esquema de BD asignado',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    config JSON COMMENT 'Configuración específica del tenant',
    INDEX idx_tenant_status (status)
) COMMENT 'Clientes/empresas que usan el sistema';

-- Tabla de usuarios del sistema
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    user_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Formato: USR-0001',
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('admin', 'sales', 'support', 'manager', 'warehouse', 'finance') NOT NULL,
    department VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    INDEX idx_user_role (role),
    INDEX idx_user_active (is_active)
) COMMENT 'Usuarios del sistema';

-- Tabla de sesiones de usuarios
CREATE TABLE user_sessions (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_session_token (token),
    INDEX idx_session_user (user_id)
) COMMENT 'Sesiones activas de usuarios';

-- Tabla de clientes
CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    customer_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Formato: CLI-00001',
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    company_name VARCHAR(100),
    tax_id VARCHAR(20) NOT NULL COMMENT 'RFC/CIF/NIT según país',
    customer_type ENUM('individual', 'business') NOT NULL,
    status ENUM('lead', 'active', 'inactive', 'banned') DEFAULT 'lead',
    credit_limit DECIMAL(12,2) DEFAULT 0.00,
    payment_terms INT DEFAULT 30 COMMENT 'Días de crédito',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    INDEX idx_customer_tax (tax_id),
    INDEX idx_customer_status (status)
) COMMENT 'Clientes del sistema';

-- Tabla de contactos de clientes
CREATE TABLE customer_contacts (
    contact_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    contact_type ENUM('primary', 'billing', 'technical', 'other') NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    mobile VARCHAR(20),
    position VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_customer_contact (customer_id)
) COMMENT 'Contactos asociados a clientes';

-- Tabla de direcciones
CREATE TABLE address_book (
    address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    address_type ENUM('billing', 'shipping', 'primary', 'other') NOT NULL,
    street VARCHAR(100) NOT NULL,
    exterior_number VARCHAR(20) NOT NULL,
    interior_number VARCHAR(20),
    neighborhood VARCHAR(50),
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(50) DEFAULT 'México',
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_customer_address (customer_id, address_type)
) COMMENT 'Direcciones de clientes';

-- Tabla de categorías de productos
CREATE TABLE product_categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    name VARCHAR(50) NOT NULL,
    description TEXT,
    parent_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES product_categories(category_id) ON DELETE SET NULL,
    INDEX idx_category_active (is_active)
) COMMENT 'Categorías de productos';

-- Tabla de marcas de productos
CREATE TABLE product_brands (
    brand_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    name VARCHAR(50) NOT NULL,
    description TEXT,
    website VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    INDEX idx_brand_active (is_active)
) COMMENT 'Marcas de productos';

-- Tabla de productos
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    product_code VARCHAR(50) UNIQUE NOT NULL COMMENT 'Código interno',
    sku VARCHAR(50) UNIQUE NOT NULL COMMENT 'Código de inventario',
    barcode VARCHAR(50) COMMENT 'Código de barras',
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT,
    brand_id BIGINT,
    price DECIMAL(12,2) NOT NULL,
    cost DECIMAL(10,2) COMMENT 'Costo unitario',
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    is_service BOOLEAN DEFAULT FALSE,
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 5,
    reorder_point INT COMMENT 'Punto de reorden',
    weight_grams DECIMAL(10,2),
    dimensions VARCHAR(50) COMMENT 'Largo x Ancho x Alto en cm',
    warranty_months INT COMMENT 'Meses de garantía',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES product_categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (brand_id) REFERENCES product_brands(brand_id) ON DELETE SET NULL,
    INDEX idx_product_sku (sku),
    INDEX idx_product_active (is_active)
) COMMENT 'Productos físicos/digitales';

-- Tabla de categorías de servicios
CREATE TABLE service_categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    name VARCHAR(50) NOT NULL,
    description TEXT,
    parent_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES service_categories(category_id) ON DELETE SET NULL,
    INDEX idx_service_category_active (is_active)
) COMMENT 'Categorías de servicios';

-- Tabla de servicios
CREATE TABLE services (
    service_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    service_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT,
    price DECIMAL(12,2) NOT NULL,
    cost DECIMAL(10,2),
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    duration_minutes INT COMMENT 'Duración estimada',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES service_categories(category_id) ON DELETE SET NULL,
    INDEX idx_service_active (is_active)
) COMMENT 'Servicios ofrecidos';

-- Tabla de cotizaciones
CREATE TABLE quotes (
    quote_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    quote_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Formato: COT-YYYYMMDD-XXXX',
    customer_id BIGINT NOT NULL,
    user_id BIGINT COMMENT 'Vendedor creador',
    quote_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    expiration_date DATE,
    status ENUM('draft', 'sent', 'accepted', 'rejected', 'expired') DEFAULT 'draft',
    subtotal DECIMAL(12,2) NOT NULL,
    tax_amount DECIMAL(12,2) DEFAULT 0.00,
    discount_amount DECIMAL(12,2) DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    currency CHAR(3) DEFAULT 'MXN',
    notes TEXT,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_quote_status (status),
    INDEX idx_quote_date (quote_date)
) COMMENT 'Cotizaciones a clientes';

-- Tabla de items en cotizaciones
CREATE TABLE quote_items (
    quote_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quote_id BIGINT NOT NULL,
    product_id BIGINT,
    service_id BIGINT,
    quantity DECIMAL(10,3) NOT NULL DEFAULT 1,
    unit_price DECIMAL(12,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    discount_percent DECIMAL(5,2) DEFAULT 0.00,
    notes TEXT,
    FOREIGN KEY (quote_id) REFERENCES quotes(quote_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE RESTRICT,
    CHECK (product_id IS NOT NULL OR service_id IS NOT NULL),
    INDEX idx_quoteitem_quote (quote_id)
) COMMENT 'Items en cotizaciones';

-- Tabla de órdenes
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    order_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Formato: ORD-YYYYMMDD-XXXX',
    customer_id BIGINT NOT NULL,
    user_id BIGINT COMMENT 'Vendedor creador',
    quote_id BIGINT COMMENT 'Cotización relacionada',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('draft', 'approved', 'processing', 'shipped', 'delivered', 'cancelled', 'returned') DEFAULT 'draft',
    subtotal DECIMAL(12,2) NOT NULL,
    tax_amount DECIMAL(12,2) DEFAULT 0.00,
    discount_amount DECIMAL(12,2) DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    currency CHAR(3) DEFAULT 'MXN',
    payment_method ENUM('cash', 'credit_card', 'bank_transfer', 'digital_wallet', 'other'),
    payment_status ENUM('pending', 'partial', 'paid', 'refunded') DEFAULT 'pending',
    shipping_address TEXT,
    shipping_method VARCHAR(50),
    shipping_cost DECIMAL(10,2) DEFAULT 0.00,
    expected_shipment_date DATE,
    tracking_number VARCHAR(100),
    notes TEXT,
    source ENUM('web', 'phone', 'in_person', 'crm') NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (quote_id) REFERENCES quotes(quote_id) ON DELETE SET NULL,
    INDEX idx_order_status (status),
    INDEX idx_order_date (order_date)
) COMMENT 'Órdenes de venta';

-- Tabla de transacciones de pago
CREATE TABLE payment_transactions (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    order_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_method ENUM('cash', 'credit_card', 'bank_transfer', 'digital_wallet', 'other') NOT NULL,
    transaction_code VARCHAR(50) UNIQUE,
    status ENUM('pending', 'completed', 'failed', 'refunded') NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE RESTRICT,
    INDEX idx_payment_order (order_id),
    INDEX idx_payment_status (status)
) COMMENT 'Transacciones de pago';

-- Tabla de items en órdenes
CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT,
    service_id BIGINT,
    quantity DECIMAL(10,3) NOT NULL DEFAULT 1,
    unit_price DECIMAL(12,2) NOT NULL COMMENT 'Precio al momento de la venta',
    unit_cost DECIMAL(12,2) COMMENT 'Costo al momento de la venta',
    tax_rate DECIMAL(5,2) DEFAULT 0.00,
    discount_percent DECIMAL(5,2) DEFAULT 0.00,
    serial_number VARCHAR(100),
    batch_number VARCHAR(100),
    status ENUM('pending', 'fulfilled', 'shipped', 'delivered', 'returned') DEFAULT 'pending',
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE RESTRICT,
    CHECK (product_id IS NOT NULL OR service_id IS NOT NULL),
    INDEX idx_orderitem_order (order_id)
) COMMENT 'Items en órdenes de venta';

-- Tabla de tickets de soporte
CREATE TABLE support_tickets (
    ticket_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    ticket_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Formato: TKT-YYYYMMDD-XXXX',
    customer_id BIGINT NOT NULL,
    user_id BIGINT COMMENT 'Agente asignado',
    order_id BIGINT COMMENT 'Orden relacionada',
    product_id BIGINT COMMENT 'Producto relacionado',
    subject VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category ENUM('technical', 'billing', 'shipping', 'returns', 'general') DEFAULT 'general',
    priority ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium',
    status ENUM('open', 'assigned', 'in_progress', 'waiting_customer', 'resolved', 'closed') DEFAULT 'open',
    sla_hours INT DEFAULT 48 COMMENT 'Horas para resolución',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at DATETIME,
    resolution TEXT,
    customer_rating TINYINT COMMENT '1-5 estrellas',
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL,
    INDEX idx_ticket_status (status),
    INDEX idx_ticket_priority (priority)
) COMMENT 'Tickets de soporte técnico';

-- Tabla de logs de actividad
CREATE TABLE activity_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    user_id BIGINT COMMENT 'Usuario que realizó la acción',
    customer_id BIGINT COMMENT 'Cliente relacionado',
    activity_type VARCHAR(50) NOT NULL COMMENT 'Ej: login, order_create, ticket_update',
    description TEXT,
    related_entity ENUM('customer', 'order', 'quote', 'product', 'ticket'),
    related_id BIGINT COMMENT 'ID del registro relacionado',
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    INDEX idx_activity_type (activity_type),
    INDEX idx_activity_date (created_at)
) COMMENT 'Registro de actividades del sistema';

-- Tabla de integraciones con APIs
CREATE TABLE api_integrations (
    integration_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT 'Referencia al tenant',
    name VARCHAR(50) NOT NULL,
    service_type ENUM('payment', 'shipping', 'erp', 'crm', 'other') NOT NULL,
    api_key VARCHAR(255),
    api_secret VARCHAR(255),
    base_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    settings JSON COMMENT 'Configuraciones adicionales',
    last_sync DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE,
    INDEX idx_integration_active (is_active)
) COMMENT 'Integraciones con APIs externas';

-- Tabla de control de migraciones
CREATE TABLE schema_migrations (
    migration_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT COMMENT 'NULL para migraciones globales',
    version VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant_clients(tenant_id) ON DELETE CASCADE
) COMMENT 'Control de migraciones de base de datos';



#### **1. Relaciones Clave**


|**Tabla Origen**|**Relación**|**Tabla Destino**|**Cardinalidad**|**ON DELETE**|**Descripción**|
|---|---|---|---|---|---|
|`tenant_clients`|→|`users`|1:N|CASCADE|Un tenant tiene múltiples usuarios|
|`tenant_clients`|→|`customers`|1:N|CASCADE|Un tenant tiene múltiples clientes|
|`customers`|→|`orders`|1:N|RESTRICT|Un cliente puede tener múltiples órdenes|
|`customers`|→|`quotes`|1:N|RESTRICT|Un cliente puede tener múltiples cotizaciones|
|`orders`|→|`order_items`|1:N|CASCADE|Una orden contiene múltiples items|
|`quotes`|→|`quote_items`|1:N|CASCADE|Una cotización contiene múltiples items|
|`products`|→|`order_items`|1:N|RESTRICT|Un producto puede estar en múltiples órdenes|
|`services`|→|`order_items`|1:N|RESTRICT|Un servicio puede estar en múltiples órdenes|
|`users`|→|`activity_logs`|1:N|SET NULL|Un usuario puede generar múltiples logs|
|`support_tickets`|←|`orders`|N:1|SET NULL|Un ticket puede estar asociado a una orden|

### **Tabla de Relaciones Completa**

|**Tabla Origen**|**Campo Origen**|**Tabla Destino**|**Campo Destino**|**Tipo Relación**|**ON DELETE**|**ON UPDATE**|
|---|---|---|---|---|---|---|
|`users`|`tenant_id`|`tenant_clients`|`tenant_id`|FOREIGN KEY|CASCADE|CASCADE|
|`customers`|`tenant_id`|`tenant_clients`|`tenant_id`|FOREIGN KEY|CASCADE|CASCADE|
|`products`|`tenant_id`|`tenant_clients`|`tenant_id`|FOREIGN KEY|CASCADE|CASCADE|
|`orders`|`customer_id`|`customers`|`customer_id`|FOREIGN KEY|RESTRICT|CASCADE|
|`quotes`|`customer_id`|`customers`|`customer_id`|FOREIGN KEY|RESTRICT|CASCADE|
|`order_items`|`order_id`|`orders`|`order_id`|FOREIGN KEY|CASCADE|CASCADE|
|`quote_items`|`quote_id`|`quotes`|`quote_id`|FOREIGN KEY|CASCADE|CASCADE|
|`payment_transactions`|`order_id`|`orders`|`order_id`|FOREIGN KEY|RESTRICT|CASCADE|
|`support_tickets`|`order_id`|`orders`|`order_id`|FOREIGN KEY|SET NULL|CASCADE|
|`product_categories`|`parent_id`|`product_categories`|`category_id`|SELF-REFERENCING|SET NULL|CASCADE|

### **Notas Importantes**

#### **1. Sobre Multi-Tenancy**

✅ **Ventajas**:

- Aislamiento total entre clientes (cada tenant tiene su esquema).
    
- Fácil backup/restore por cliente.
    

⚠️ **Consideraciones**:

- El rendimiento puede degradarse con muchos tenants.
    
- Migraciones deben sincronizarse en todos los esquemas.
    

#### **2. Sobre Borrado en Cascada**

- **`ON DELETE CASCADE`** se usa en relaciones fuertes (ejemplo: `tenant_clients → users`).
    
- **`ON DELETE RESTRICT`** evita borrados accidentales en datos críticos (ejemplo: `customers → orders`).
    

#### **3. Sobre Transacciones de Pago**

- La tabla `payment_transactions` permite múltiples pagos por orden (ejemplo: pagos parciales).
    
- Considerar integración con APIs como Stripe o PayPal.
    

#### **4. Sobre Auditoría**

- La tabla `activity_logs` registra acciones, pero no captura cambios detallados.
    
- Para rastreo completo, usar triggers + tablas de histórico.
    

---

### **Resumen Final**

✅ **Base de datos para un sistema multiempresa.  

📌 **Mejoras clave**:

- Índices optimizados.
    
- Auditoría detallada con triggers.
    
- Normalización de direcciones.
    
- Particionamiento para escalabilidad.


