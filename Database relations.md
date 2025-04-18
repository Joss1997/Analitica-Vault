1. **Clientes → Órdenes** (1 a muchos)
    
    - `customers.customer_id` → `orders.customer_id`
        
    - Un cliente puede tener muchas órdenes
        
2. **Clientes → Cotizaciones** (1 a muchos)
    
    - `customers.customer_id` → `quotes.customer_id`
        
    - Un cliente puede tener muchas cotizaciones
        
3. **Clientes → Tickets** (1 a muchos)
    
    - `customers.customer_id` → `support_tickets.customer_id`
        
    - Un cliente puede tener múltiples tickets de soporte
        
4. **Usuarios → Órdenes** (1 a muchos)
    
    - `users.user_id` → `orders.user_id`
        
    - Un usuario puede gestionar múltiples órdenes
        
5. **Usuarios → Cotizaciones** (1 a muchos)
    
    - `users.user_id` → `quotes.user_id`
        
    - Un usuario puede crear múltiples cotizaciones
        
6. **Usuarios → Tickets** (1 a muchos)
    
    - `users.user_id` → `support_tickets.user_id`
        
    - Un usuario puede estar asignado a múltiples tickets
        
7. **Usuarios → Logs** (1 a muchos)
    
    - `users.user_id` → `activity_logs.user_id`
        
    - Un usuario genera múltiples registros de actividad
        
8. **Categorías → Productos** (1 a muchos)
    
    - `product_categories.category_id` → `products.category_id`
        
    - Una categoría contiene muchos productos
        
9. **Marcas → Productos** (1 a muchos)
    
    - `product_brands.brand_id` → `products.brand_id`
        
    - Una marca tiene muchos productos
        
10. **Categorías → Servicios** (1 a muchos)
    
    - `service_categories.category_id` → `services.category_id`
        
    - Una categoría de servicios contiene muchos servicios
        
11. **Órdenes → Items de Orden** (1 a muchos)
    
    - `orders.order_id` → `order_items.order_id`
        
    - Una orden contiene múltiples items (productos/servicios)
        
12. **Productos → Items de Orden** (1 a muchos)
    
    - `products.product_id` → `order_items.product_id`
        
    - Un producto puede aparecer en múltiples órdenes
        
13. **Servicios → Items de Orden** (1 a muchos)
    
    - `services.service_id` → `order_items.service_id`
        
    - Un servicio puede aparecer en múltiples órdenes
        
14. **Cotizaciones → Items de Cotización** (1 a muchos)
    
    - `quotes.quote_id` → `quote_items.quote_id`
        
    - Una cotización contiene múltiples items
        
15. **Productos → Items de Cotización** (1 a muchos)
    
    - `products.product_id` → `quote_items.product_id`
        
    - Un producto puede aparecer en múltiples cotizaciones
        
16. **Servicios → Items de Cotización** (1 a muchos)
    
    - `services.service_id` → `quote_items.service_id`
        
    - Un servicio puede aparecer en múltiples cotizaciones
        
17. **Categorías → Subcategorías** (Auto-relación 1 a muchos)
    
    - `product_categories.category_id` → `product_categories.parent_id`
        
    - Una categoría puede tener muchas subcategorías
        
18. **Categorías de Servicios → Subcategorías** (Auto-relación 1 a muchos)
    
    - `service_categories.category_id` → `service_categories.parent_id`
        
    - Una categoría de servicios puede tener subcategorías
        

### Relaciones Especiales:

1. **Relación Exclusiva en Items**:
    
    - En `order_items` y `quote_items` hay una relación exclusiva:
        
        - Cada item debe referenciar O un producto O un servicio (no ambos)
            
        - Implementado con CHECK constraint
            
2. **Relaciones Temporales**:
    
    - Todas las tablas principales tienen `created_at` y `updated_at` para tracking temporal
        
3. **Relaciones de Estado**:
    
    - Múltiples tablas tienen campos `status` que determinan su estado actual