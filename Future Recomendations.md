## ⚠️ Recomendaciones y Mejoras

### 🔒 1. Seguridad en Contraseñas

- Campo `password_hash` debe ser **encriptado con algoritmo seguro (e.g. bcrypt o Argon2)** en el backend.
    
- También podrías añadir un campo `password_last_changed` para política de rotación.
    

### 📐 2. Restricciones y validaciones adicionales

- **`CHECK (quantity > 0)`** en `order_items` y `quote_items`.
    
- **`CHECK (tax_rate BETWEEN 0 AND 100)`**, lo mismo para `discount_percent`.
    

### ⚠️ 3. Ambigüedad en `product_id` y `service_id` nulos

Aunque usas `CHECK (product_id IS NOT NULL OR service_id IS NOT NULL)`:

- Se recomienda validar también que **solo uno de ellos esté lleno**:
- 
CHECK (
    (product_id IS NOT NULL AND service_id IS NULL)
 OR (product_id IS NULL AND service_id IS NOT NULL)
)

### 📚 4. Integridad de `quote_id` y `order_id` (1:1)

Si realmente es 1:1 entre `quotes` y `orders`:

- Asegúrate que `quote_id` tenga una **constraint UNIQUE** en `orders`.
    

### 🧩 5. Indexación sobre `created_at`

Agrega índice en:

INDEX idx_created_at (created_at)

En tablas como `orders`, `support_tickets`, `activity_logs` para mejorar filtrado cronológico.

### 🧪 6. Control de Versiones para Productos y Servicios

- Si los precios o especificaciones cambian seguido, podrías implementar:
    
    - Tabla `product_versions`
        
    - Campos tipo `version`, `valid_from`, `valid_to`
        

### 📦 7. Tabla de `inventory_movements`

Aunque tengas `stock_quantity` en `products`, una tabla como esta es ideal para trazabilidad:

CREATE TABLE inventory_movements (
    movement_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    order_item_id BIGINT,
    type ENUM('inbound', 'outbound', 'adjustment') NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    reason VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (order_item_id) REFERENCES order_items(order_item_id)
);
## 🧠 Ideas Adicionales

| Idea                   | Justificación                                           |
| ---------------------- | ------------------------------------------------------- |
| Tabla `documents`      | Para PDFs (cotizaciones, facturas, manuales, etc.)      |
| Tabla `notes` genérica | Notas por entidad (`customer`, `order`, `ticket`, etc.) |
| Multidivisa avanzada   | Agregar campo `exchange_rate` en cotizaciones/órdenes   |
| Tabla `notifications`  | Para enviar alertas internas a usuarios                 |