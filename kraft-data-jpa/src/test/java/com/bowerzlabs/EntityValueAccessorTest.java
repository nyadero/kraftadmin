package com.bowerzlabs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.metamodel.EntityType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityValueAccessorTest {

    EntityManager entityManager;
    EntityType<DummyEntity> entityType;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setFieldValue() {
    }

    @BeforeEach
    void setup() {
        entityManager = mock(EntityManager.class);
        entityType = mock(EntityType.class);
        when(entityType.getJavaType()).thenReturn(DummyEntity.class);
    }

    @Test
    void testSetSimpleFieldValue() throws Exception {
        DummyEntity entity = new DummyEntity();
        EntityValueAccessor.setFieldValue(entityType, entity, "name", "Brian", entityManager);
        assertEquals("Brian", entity.getName());
    }

    @Test
    void testSetNestedFieldValue() throws Exception {
        DummyEntity entity = new DummyEntity();
        EntityValueAccessor.setFieldValue(entityType, entity, "nested.city", "Nairobi", entityManager);
        assertNotNull(entity.getNested());
        assertEquals("Nairobi", entity.getNested().getCity());
    }

    @Test
    void testSetIntegerFieldValue() throws Exception {
        DummyEntity entity = new DummyEntity();
        EntityValueAccessor.setFieldValue(entityType, entity, "age", "25", entityManager);
        assertEquals(25, entity.getAge());
    }

    @Test
    void testPasswordEncoding() throws Exception {
        DummyEntity entity = new DummyEntity();
        EntityValueAccessor.setFieldValue(entityType, entity, "password", "secret123", entityManager);
        assertNotEquals("secret123", entity.getPassword());
        assertTrue(entity.getPassword().startsWith("$2a$")); // bcrypt prefix
    }

    @Test
    void testDoubleNestedEntity() throws Exception {
        DummyEntity entity = new DummyEntity();
        EntityValueAccessor.setFieldValue(entityType, entity, "nested.nested.name", "Double Nest", entityManager);
        assertNotNull(entity.getNested());
        assertNotEquals("Double Nest", entity.getNested().getCity());
    }

    static class DummyEntity {
        @Id
        private Long id;
        private String name;
        private int age;
        private NestedEntity nested;
        private String password;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public NestedEntity getNested() {
            return nested;
        }

        public void setNested(NestedEntity nested) {
            this.nested = nested;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class NestedEntity {
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    static class DoubleNestedEntity {
        private NestedEntity nestedEntity;
        private String name;

        public NestedEntity getNestedEntity() {
            return nestedEntity;
        }

        public void setNestedEntity(NestedEntity nestedEntity) {
            this.nestedEntity = nestedEntity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}