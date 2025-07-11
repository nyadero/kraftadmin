
### Spring Boot Admin Library

A modular and customizable admin dashboard library for Spring Boot applications. It provides dynamic filtering, sorting, pagination, file preview, and global search across fields — all with minimal configuration.

Ideal for building internal tools, CMS interfaces, data dashboards, or managing entities in CRUD-heavy applications.

Coming soon as a Maven/Gradle dependency. For now, clone and build locally.

#### Features

- Dynamic filtering and sorting on any field

- File preview support in listings

- Pagination with URL state preservation

- Global search across all fields

- Easily customizable for different data models

- Works out-of-the-box with Spring Data JPA

- Enum-based filters for structured querying

### Installation (Current Method)

Until it's available via Maven Central or GitHub Packages, you can use it by cloning and building the project locally:

```bash
git clone https://github.com/nyadero/kraftadmin.git
cd kraftadmin
mvn clean install
```

#### Add to Your Spring Boot Project
Add the Maven Dependency

````
<dependency>
  <groupId>io.github.bowerzlabs</groupId>
  <artifactId>kraft-admin</artifactId>
  <version>0.1.1</version>
</dependency>
````

### Configuration
application.properties
````
# Enable the admin library
kraft.kraft-admin.enabled=true

# Set the admin panel title
kraft.title=Hacksurge Dashboard

````

OR application.yml

````
kraft:
  kraft-admin:
    enabled: true
  title: Kraft Sandbox11
````

### Entity & Repository Setup

Update your main application class to scan KraftAdmin's models and repositories:

````
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
  "your.package",
  "com.bowerzlabs.repository.kraftrepos"
})
@EntityScan(basePackages = {
  "your.package",
  "com.bowerzlabs.models.kraftmodels"
})
public class Application {
  public static void main(String[] args) {
  SpringApplication.run(Application.class, args);
  }
}

````

###  Modules Overview

## Modules Overview

| Module          | Description                                   |
|------------------|-----------------------------------------------|
| `kraft-ui`       | UI components for the admin dashboard         |
| `kraft-security` | Auth & permission middleware                  |
| `kraft-admin`    | Core admin logic and utilities                |
| `kraft-admin`  | Combines and exposes all modules for plug-and-play |

### Coming Soon

- GitHub Packages + Maven Central

- Example project

- Admin UI customization docs

- Starter guide with multi-tenant support
- Monitoring and logging
- Analytics 

Form Field Factory

The FormFieldFactory is a dynamic form generator for Java entity classes. It automatically generates form fields (e.g., TextField, DateField, SearchableSelectField, etc.) based on entity attributes and annotations.

### Core Functionality

- Field Type Inference: Determines the appropriate form field component by inspecting:

  - Java type (e.g., String, LocalDate, boolean)

  - JPA annotations (e.g., @Column, @Lob, @ManyToOne, @Enumerated)

  - Custom annotations like @FormInputType and @DisplayField

- Value Extraction:

    Pulls values from the actual entity object (if present) or fallback map.

- Validation Mapping:

        Applies validation rules and error messages from DbObjectSchema.

- Relationship Handling:

        Supports @ManyToOne and @OneToOne by fetching related entities using a shared CrudService.

        Builds option maps using @DisplayField for display labels.

- Support for Enums & Embedded Objects:

        Automatically generates select options for enum types.

        Begins groundwork for handling embedded/complex objects (incomplete but structured for expansion).

Annotate Your Entities

    @Entity
    public class Product {
    @Id
    private UUID id;

    @FormInputType(FormInputType.Type.TEXT)
    private String name;

    @FormInputType(FormInputType.Type.NUMBER)
    private Double price;

    @FormInputType(FormInputType.Type.IMAGE)
    private String thumbnail;

    @FormInputType(FormInputType.Type.WYSIWYG)
    private String description;

    // getters & setters...
    }

Custom Field Display with @FormInputType

The annotation @FormInputType allows you to define how a field should be rendered on the admin form:

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FormInputType {
    Type value();

    enum Type {
        TEXT, NUMBER, COLOR, CHECKBOX, IMAGE, DATE, EMAIL, PASSWORD, FILE,
            TEXTAREA, WYSIWYG, DATETIME, TIME, RANGE, TEL, URL, RADIO
        }
    }

Why it matters

This means you can declaratively control whether a field appears as a textbox, number input, image uploader, WYSIWYG editor, etc., just by annotating the field.

KraftAdmin will automatically reflect this in the UI form without extra frontend code.

### Screenshots

<img src="/images/kraft-admin.png" alt="kraftadmin list page">

