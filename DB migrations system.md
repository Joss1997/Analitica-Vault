src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── migrationservice/
│   │               ├── config/
│   │               │   ├── FlywayConfig.java
│   │               │   └── SecurityConfig.java
│   │               ├── controller/
│   │               │   ├── MigrationController.java
│   │               │   └── TenantController.java
│   │               ├── dto/
│   │               │   ├── MigrationRequest.java
│   │               │   └── TenantRequest.java
│   │               ├── exception/
│   │               │   ├── GlobalExceptionHandler.java
│   │               │   └── TenantNotFoundException.java
│   │               ├── model/
│   │               │   └── Tenant.java
│   │               ├── repository/
│   │               │   └── TenantRepository.java
│   │               ├── service/
│   │               │   ├── MigrationService.java
│   │               │   └── TenantService.java
│   │               ├── util/
│   │               │   └── DatabaseUtils.java
│   │               └── MigrationServiceApplication.java
│   └── resources/
│       ├── application.yml
│       └── db/
│           └── migration/
│               ├── free/
│               │   ├── V1__Initial_free_schema.sql
│               │   └── V2__Add_free_features.sql
│               └── premium/
│                   ├── V1__Initial_premium_schema.sql
│                   └── V2__Add_premium_features.sql