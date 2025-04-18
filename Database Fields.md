### 1. Tabla de Clientes (Customers)

| Campo               | Tipo de Dato         | Descripción                                      |
| ------------------- | -------------------- | ------------------------------------------------ |
| customer_id         | INT (Auto-increment) | Identificador único del cliente                  |
| first_name          | VARCHAR(50)          | Nombre(s) del cliente                            |
| last_name           | VARCHAR(50)          | Apellido(s) del cliente                          |
| company_name        | VARCHAR(100)         | Nombre de la empresa (si es cliente corporativo) |
| tax_id              | VARCHAR(20)          | RFC, CURP, NIT o equivalente para facturación    |
| email               | VARCHAR(100)         | Correo electrónico principal (único por cliente) |
| phone               | VARCHAR(20)          | Teléfono fijo de contacto                        |
| mobile              | VARCHAR(20)          | Teléfono móvil de contacto                       |
| address_street      | VARCHAR(100)         | Calle y número de dirección                      |
| address_city        | VARCHAR(50)          | Ciudad/Municipio                                 |
| address_state       | VARCHAR(50)          | Estado/Provincia                                 |
| address_postal_code | VARCHAR(20)          | Código postal                                    |
| address_country     | VARCHAR(50)          | País (por defecto 'México')                      |
| customer_type       | ENUM                 | Tipo: 'individual' o 'business'                  |
| status              | ENUM                 | Estado: 'active', 'inactive' o 'lead'            |
| created_at          | TIMESTAMP            | Fecha de creación del registro                   |
| updated_at          | TIMESTAMP            | Fecha de última actualización                    |
| notes               | TEXT                 | Notas adicionales sobre el cliente               |

### 2. Tabla de Usuarios (Users)

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|user_id|INT (Auto-increment)|ID único del usuario del sistema|
|username|VARCHAR(50)|Nombre de usuario para login (único)|
|email|VARCHAR(100)|Correo electrónico (único)|
|password_hash|VARCHAR(255)|Contraseña encriptada|
|first_name|VARCHAR(50)|Nombre real del usuario|
|last_name|VARCHAR(50)|Apellido real del usuario|
|role|ENUM|Rol: 'admin', 'sales', 'support', 'manager', 'warehouse'|
|department|VARCHAR(50)|Departamento al que pertenece|
|phone|VARCHAR(20)|Teléfono de contacto|
|is_active|BOOLEAN|Si el usuario está activo (true/false)|
|last_login|DATETIME|Fecha del último inicio de sesión|
|created_at|TIMESTAMP|Fecha de creación|
|updated_at|TIMESTAMP|Fecha de última actualización|

### 3. Tabla de Productos (Products)

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|product_id|INT (Auto-increment)|ID único del producto|
|sku|VARCHAR(50)|Código único de identificación (Stock Keeping Unit)|
|name|VARCHAR(100)|Nombre del producto|
|description|TEXT|Descripción detallada|
|category_id|INT|ID de la categoría (relación)|
|brand_id|INT|ID de la marca (relación)|
|price|DECIMAL(10,2)|Precio de venta al público|
|cost|DECIMAL(10,2)|Costo para la empresa|
|tax_rate|DECIMAL(5,2)|Porcentaje de impuesto aplicable|
|is_service|BOOLEAN|Si es un servicio (no producto físico)|
|is_digital|BOOLEAN|Si es producto digital|
|stock_quantity|INT|Cantidad disponible en inventario|
|low_stock_threshold|INT|Nivel mínimo antes de alertar|
|weight|DECIMAL(10,2)|Peso en gramos|
|dimensions|VARCHAR(50)|Medidas (Largo x Ancho x Alto en cm)|
|is_active|BOOLEAN|Si está disponible para venta|
|created_at|TIMESTAMP|Fecha de creación|
|updated_at|TIMESTAMP|Fecha de última actualización|

### 4. Tabla de Categorías de Productos

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|category_id|INT (Auto-increment)|ID único de categoría|
|name|VARCHAR(50)|Nombre de la categoría|
|description|TEXT|Descripción de la categoría|
|parent_id|INT|ID de categoría padre (para subcategorías)|
|is_active|BOOLEAN|Si la categoría está activa|

### 5. Tabla de Marcas

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|brand_id|INT (Auto-increment)|ID único de marca|
|name|VARCHAR(50)|Nombre de la marca|
|description|TEXT|Descripción/información de la marca|
|website|VARCHAR(100)|URL del sitio web de la marca|
|is_active|BOOLEAN|Si la marca está activa en el sistema|

### 6. Tabla de Servicios (Services)

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|service_id|INT (Auto-increment)|ID único del servicio|
|code|VARCHAR(50)|Código identificador único|
|name|VARCHAR(100)|Nombre del servicio|
|description|TEXT|Descripción detallada|
|category_id|INT|ID de categoría de servicio|
|price|DECIMAL(10,2)|Precio del servicio|
|cost|DECIMAL(10,2)|Costo para la empresa|
|tax_rate|DECIMAL(5,2)|Impuesto aplicable|
|duration_minutes|INT|Duración estimada en minutos|
|is_preventive|BOOLEAN|Si es servicio preventivo|
|is_corrective|BOOLEAN|Si es servicio correctivo|
|is_active|BOOLEAN|Si el servicio está disponible|
|created_at|TIMESTAMP|Fecha de creación|
|updated_at|TIMESTAMP|Fecha de actualización|

### 7. Tabla de Órdenes (Orders)

| Campo            | Tipo de Dato         | Descripción                                                                     |
| ---------------- | -------------------- | ------------------------------------------------------------------------------- |
| order_id         | INT (Auto-increment) | ID único de la orden                                                            |
| customer_id      | INT                  | ID del cliente asociado                                                         |
| user_id          | INT                  | ID del usuario que gestiona la orden                                            |
| order_date       | TIMESTAMP            | Fecha de creación de la orden                                                   |
| status           | ENUM                 | Estado: 'pending', 'processing', 'completed', 'cancelled', 'refunded'           |
| total_amount     | DECIMAL(10,2)        | Total de la orden con impuestos                                                 |
| tax_amount       | DECIMAL(10,2)        | Monto total de impuestos                                                        |
| discount_amount  | DECIMAL(10,2)        | Monto total de descuentos                                                       |
| payment_method   | ENUM                 | Método: 'cash', 'credit_card', 'debit_card', 'bank_transfer', 'paypal', 'other' |
| payment_status   | ENUM                 | Estado: 'pending', 'paid', 'partially_paid', 'refunded'                         |
| shipping_address | TEXT                 | Dirección de envío completa                                                     |
| notes            | TEXT                 | Notas adicionales                                                               |
| source           | ENUM                 | Origen: 'web', 'phone', 'in_person', 'crm'                                      |

### 8. Tabla de Detalles de Orden (Order Items)

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|order_item_id|INT (Auto-increment)|ID único del item|
|order_id|INT|ID de la orden relacionada|
|product_id|INT|ID del producto (NULL si es servicio)|
|service_id|INT|ID del servicio (NULL si es producto)|
|quantity|INT|Cantidad comprada|
|unit_price|DECIMAL(10,2)|Precio unitario en el momento de la compra|
|tax_rate|DECIMAL(5,2)|Impuesto aplicado|
|discount|DECIMAL(5,2)|Descuento aplicado (%)|
|notes|TEXT|Notas específicas del item|

### 9. Tabla de Tickets de Soporte

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|ticket_id|INT (Auto-increment)|ID único del ticket|
|customer_id|INT|ID del cliente que reporta|
|user_id|INT|ID del usuario asignado|
|subject|VARCHAR(100)|Asunto del ticket|
|description|TEXT|Descripción detallada del problema|
|status|ENUM|Estado: 'open', 'in_progress', 'on_hold', 'resolved', 'closed'|
|priority|ENUM|Prioridad: 'low', 'medium', 'high', 'critical'|
|created_at|TIMESTAMP|Fecha de creación|
|updated_at|TIMESTAMP|Fecha de última actualización|
|resolved_at|DATETIME|Fecha de resolución|
|resolution|TEXT|Explicación de la solución|

### 10. Tabla de Integración con API

|Campo|Tipo de Dato|Descripción|
|---|---|---|
|integration_id|INT (Auto-increment)|ID único de integración|
|name|VARCHAR(50)|Nombre descriptivo (ej. "DeepSeek Productos")|
|api_key|VARCHAR(255)|Clave API para autenticación|
|api_secret|VARCHAR(255)|Secreto API (encriptado)|
|base_url|VARCHAR(255)|URL base del API|
|is_active|BOOLEAN|Si la integración está activa|
|settings|JSON|Configuraciones adicionales en formato JSON|
|last_sync|DATETIME|Fecha de última sincronización|
|created_at|TIMESTAMP|Fecha de creación|
|updated_at|TIMESTAMP|Fecha de actualización|