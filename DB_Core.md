-- Tabla maestra de clientes/empresas (tanto free tier como premium)
CREATE TABLE tenants (
    tenant_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'Código único (ej: CLI-001)',
    name VARCHAR(100) NOT NULL COMMENT 'Nombre de la empresa o usuario free',
    tax_id VARCHAR(30) COMMENT 'RFC/CIF/NIT según país',
    email VARCHAR(100) UNIQUE NOT NULL,
    plan_type ENUM('free', 'premium', 'enterprise') DEFAULT 'free',
    db_name VARCHAR(50) UNIQUE COMMENT 'Nombre de la DB dedicada (si es premium)',
    container_id VARCHAR(100) COMMENT 'ID del contenedor asignado',
    status ENUM('pending', 'active', 'suspended', 'deleted') DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_plan (plan_type),
    INDEX idx_tenant_status (status)
) COMMENT 'Registro central de todos los clientes';

-- Tabla de configuración global del sistema
CREATE TABLE global_config (
    config_id VARCHAR(50) PRIMARY KEY COMMENT 'Ej: max_free_users, default_currency',
    config_value JSON NOT NULL COMMENT 'Valor en formato JSON',
    description TEXT,
    updated_by BIGINT COMMENT 'Usuario que modificó la configuración',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT 'Configuración del sistema';

-- Tabla de usuarios administradores del sistema core
CREATE TABLE admin_users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('superadmin', 'tech', 'support') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_admin_active (is_active)
) COMMENT 'Usuarios con acceso al sistema core';

-- Tabla de API keys para integraciones
CREATE TABLE api_keys (
    key_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT COMMENT 'NULL si es clave global',
    key_name VARCHAR(50) NOT NULL,
    api_key VARCHAR(64) UNIQUE NOT NULL COMMENT 'Clave generada',
    scopes JSON NOT NULL COMMENT 'Permisos en formato JSON',
    expires_at DATETIME COMMENT 'NULL para claves permanentes',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    INDEX idx_api_key (api_key)
) COMMENT 'API keys para autenticación';

-- Tabla de contenedores y recursos asignados
CREATE TABLE containers (
    container_id VARCHAR(100) PRIMARY KEY COMMENT 'ID del contenedor Docker/K8s',
    tenant_id BIGINT COMMENT 'NULL si está disponible',
    status ENUM('running', 'stopped', 'error', 'maintenance') NOT NULL,
    assigned_at DATETIME,
    released_at DATETIME,
    db_connection JSON COMMENT '{host: "", port: 3306, user: "", db_name: ""}',
    resources JSON COMMENT '{cpu: "2", memory: "4GB", storage: "50GB"}',
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE SET NULL,
    INDEX idx_container_status (status)
) COMMENT 'Registro de contenedores asignados';

-- Tabla de migraciones aplicadas
CREATE TABLE schema_migrations (
    migration_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    script_name VARCHAR(100) NOT NULL,
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    applied_by BIGINT COMMENT 'admin_users.user_id',
    FOREIGN KEY (applied_by) REFERENCES admin_users(user_id) ON DELETE SET NULL
) COMMENT 'Control de migraciones de esquema';

-- Tabla de logs de actividad del core
CREATE TABLE core_activity_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT 'admin_users.user_id',
    activity_type VARCHAR(50) NOT NULL COMMENT 'Ej: tenant_create, container_assign',
    description TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES admin_users(user_id) ON DELETE SET NULL,
    INDEX idx_core_activity_type (activity_type),
    INDEX idx_core_activity_date (created_at)
) COMMENT 'Logs de actividades administrativas';

## FREE TIER

-- Tabla maestra de tenants (coordinación con core_system)
CREATE TABLE tenants (
    tenant_id BIGINT PRIMARY KEY,
    tenant_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'CLI-001',
    name VARCHAR(100) NOT NULL,
    plan_type ENUM('free', 'trial') DEFAULT 'free',
    status ENUM('active', 'suspended', 'deleted') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_status (status)
) COMMENT 'Reflejo simplificado de tenants desde core_system';

-- Tabla de usuarios (por tenant)
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    user_code VARCHAR(20) NOT NULL COMMENT 'USR-001',
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('admin', 'user', 'support') DEFAULT 'user',
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_tenant_email (tenant_id, email),
    INDEX idx_user_tenant (tenant_id)
) COMMENT 'Usuarios por organización';

-- Tabla de clientes
CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    customer_code VARCHAR(20) NOT NULL COMMENT 'CUST-001',
    name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(30),
    email VARCHAR(100),
    status ENUM('lead', 'active', 'inactive') DEFAULT 'lead',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    INDEX idx_customer_tenant (tenant_id)
) COMMENT 'Clientes de cada tenant';

-- Tabla de productos
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    sku VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    UNIQUE KEY uk_product_tenant_sku (tenant_id, sku),
    INDEX idx_product_tenant (tenant_id)
) COMMENT 'Productos por tenant';

-- Tabla de órdenes
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    order_code VARCHAR(20) NOT NULL COMMENT 'ORD-YYYYMMDD-001',
    customer_id BIGINT,
    total_amount DECIMAL(12,2) NOT NULL,
    status ENUM('draft', 'completed', 'cancelled') DEFAULT 'draft',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    INDEX idx_order_tenant (tenant_id),
    INDEX idx_order_date (created_at)
) COMMENT 'Órdenes por tenant';

-- Tabla de items de órdenes
CREATE TABLE order_items (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    INDEX idx_orderitem_tenant (tenant_id)
) COMMENT 'Items de órdenes';

-- Tabla de configuración por tenant
CREATE TABLE tenant_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    config_key VARCHAR(50) NOT NULL,
    config_value JSON NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    UNIQUE KEY uk_tenant_config (tenant_id, config_key)
) COMMENT 'Configuración específica por tenant';

-- Tabla de actividad por tenant
CREATE TABLE tenant_activity_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    activity_type VARCHAR(50) NOT NULL,
    description TEXT,
    ip_address VARCHAR(45),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_tenant_activity (tenant_id, created_at)
) COMMENT 'Logs de actividad por tenant';

## **Relaciones Clave**

|Tabla Origen|Relación|Tabla Destino|Cardinalidad|ON DELETE|
|---|---|---|---|---|
|users|→|tenants|N:1|CASCADE|
|customers|→|tenants|N:1|CASCADE|
|products|→|tenants|N:1|CASCADE|
|orders|→|tenants|N:1|CASCADE|
|orders|→|customers|N:1|SET NULL|
|order_items|→|orders|N:1|CASCADE|
|order_items|→|products|N:1|RESTRICT|

## **Índices Clave**

1. **Índices compuestos tenant + campo único**:
    
    - `uk_product_tenant_sku`: Evita SKUs duplicados por tenant
        
    - `uk_user_tenant_email`: Email único por tenant
        
2. **Índices de rendimiento**:
    
    - `idx_order_tenant`: Búsqueda rápida de órdenes por tenant
        
    - `idx_tenant_activity`: Filtrado de logs por tenant y fecha

## PREMIUM TIER

-- Tabla de usuarios (sin tenant_id)
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_code VARCHAR(20) NOT NULL COMMENT 'USR-001',
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('owner', 'admin', 'manager', 'staff') DEFAULT 'staff',
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_role (role)
) COMMENT 'Usuarios internos del cliente premium';

-- Tabla de clientes (CRM del cliente premium)
CREATE TABLE customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) NOT NULL COMMENT 'CUST-001',
    name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(30),
    email VARCHAR(100),
    phone VARCHAR(20),
    status ENUM('lead', 'active', 'vip', 'inactive') DEFAULT 'lead',
    credit_limit DECIMAL(12,2) DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_status (status)
) COMMENT 'Clientes del negocio premium';

-- Tabla de productos/servicios
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(12,2) NOT NULL,
    cost DECIMAL(10,2),
    stock INT DEFAULT 0,
    is_service BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_sku (sku),
    INDEX idx_product_active (is_active)
) COMMENT 'Catálogo del cliente premium';

-- Tabla de órdenes (ventas/pedidos)
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(20) UNIQUE NOT NULL COMMENT 'ORD-YYYYMMDD-001',
    customer_id BIGINT,
    status ENUM('draft', 'paid', 'shipped', 'delivered', 'cancelled') DEFAULT 'draft',
    subtotal DECIMAL(12,2) NOT NULL,
    tax DECIMAL(12,2) DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL,
    payment_method ENUM('cash', 'card', 'transfer') DEFAULT 'cash',
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    INDEX idx_order_status (status),
    INDEX idx_order_date (created_at)
) COMMENT 'Órdenes de venta';

-- Tabla de items de órdenes
CREATE TABLE order_items (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0.00,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    INDEX idx_orderitem_order (order_id)
) COMMENT 'Detalle de órdenes';

-- Tabla de integraciones personalizadas (API keys, webhooks)
CREATE TABLE integrations (
    integration_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT 'Ej: Shopify, Stripe',
    api_key VARCHAR(255),
    config JSON COMMENT 'Configuración personalizada',
    is_active BOOLEAN DEFAULT TRUE,
    last_sync DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_integration_active (is_active)
) COMMENT 'Integraciones externas del cliente';

-- Tabla de configuración personalizada
CREATE TABLE client_config (
    config_id VARCHAR(50) PRIMARY KEY COMMENT 'Ej: currency, timezone',
    config_value JSON NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT 'Configuración específica del cliente';

-- Tabla de logs de actividad (para auditoría)
CREATE TABLE activity_logs (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL COMMENT 'Ej: login, order_create',
    details TEXT,
    ip_address VARCHAR(45),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_activity_date (created_at)
) COMMENT 'Registro de actividades';

## **Diferencias Clave vs Free Tier**

| **Característica**  | **Free Tier**             | **Premium**                        |
| ------------------- | ------------------------- | ---------------------------------- |
| **Esquema**         | Multi-tenancy (tenant_id) | Dedicado (sin tenant_id)           |
| **Aislamiento**     | Datos compartidos         | DB 100% independiente              |
| **Personalización** | Configuración limitada    | Total libertad (API keys, configs) |
| **Rendimiento**     | Recursos compartidos      | Recursos dedicados                 |
| **Escalabilidad**   | Límites por tenant        | Sin límites artificiales           |


