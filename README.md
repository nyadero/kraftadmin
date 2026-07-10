# KraftAdmin

**Build modern admin panels for Spring Boot.**

KraftAdmin is an annotation-driven admin framework that automatically generates CRUD interfaces, forms, tables, filters, actions, dashboards, and file management from your existing Spring Boot entities.

> **This repository contains the original implementation of KraftAdmin.**
>
> 🚀 Active development has moved to the **official KraftAdmin repository**:
>
> **https://github.com/Kraft-Admin/kraftadmin**
>
> The new version features a cleaner architecture, improved modularity, richer extension APIs, and many new capabilities. New features, documentation, and future releases will be published there.

---

## Features

- Automatic CRUD generation
- Dynamic forms
- Search, filtering & sorting
- Pagination
- File uploads & previews
- Entity relationships
- Custom actions
- Lifecycle events
- Entity listeners
- Import & Export
- Dashboard navigation
- Extensible architecture

---

## Example

```java
@Entity
@KraftAdminResource(label = "Products", group = "Catalog")
public class Product {

    @KraftAdminField(label = "Name", searchable = true)
    private String name;

    @KraftAdminField(label = "Price")
    private BigDecimal price;
}
```

That's all it takes to expose an entity in the admin dashboard.

---

## What's Next

The next generation of KraftAdmin includes:

- Cleaner modular architecture
- Rich extension APIs
- Dashboard widgets
- Audit logging
- Analytics
- Multi-module support
- Official demo application
- Improved documentation

---

## Contributing

Issues and pull requests are welcome. For new features and ongoing development, please use the new repository.

---

## License

Released under the MIT License.
