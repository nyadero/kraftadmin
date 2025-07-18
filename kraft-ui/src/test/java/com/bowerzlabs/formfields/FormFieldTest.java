package com.bowerzlabs.formfields;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FormFieldTest {

    private FormField field;

    @BeforeEach
    void setUp() {
        field = new DummyField();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getLabel() {
        assertEquals("Test Label", field.getLabel());
    }

    @Test
    void getType() {
    }

    @Test
    void getName() {
    }

    @Test
    void getPlaceholder() {
    }

    @Test
    void getRequired() {
    }

    @Test
    void getValue() {
    }

    @Test
    void getValidationErrors() {
    }

    @Test
    void testGetLabel() {
    }

    @Test
    void testGetType() {
    }

    @Test
    void testGetName() {
    }

    @Test
    void testGetPlaceholder() {
    }

    @Test
    void testGetRequired() {
    }

    @Test
    void testGetValue() {
    }

    @Test
    void testGetValidationErrors() {
    }

    @Test
    void getValidationRules() {
    }

    @Test
    void isRelationship() {
    }

    @Test
    void getModelData() {
    }

    @Test
    void formatLabel() {
    }

    @Test
    void getSearchOperations() {
    }

    @Test
    void setSearchOperations() {
    }

    @Test
    void setName() {
    }

    @Test
    void setLabel() {
    }

    @Test
    void getWrapperClass() {
    }

    @Test
    void setWrapperClass() {
    }

    @Test
    void setValue() {
    }

    @Test
    void testFormatLabel() {
        assertEquals("First Name", FormField.formatLabel("firstName"));
        assertEquals("Created at", FormField.formatLabel("created_at"));
        assertEquals("User address city", FormField.formatLabel("user.address.city"));
        assertEquals("", FormField.formatLabel(null));
    }

    @Test
    void testSearchOperationsSetterGetter() {
        List<String> ops = List.of("like", "equals");
        field.setSearchOperations(ops);
        assertEquals(ops, field.getSearchOperations());
    }

    @Test
    void testWrapperClassSetterGetter() {
        assertEquals("wrapper", field.getWrapperClass());
        field.setWrapperClass("custom-wrapper");
        assertEquals("custom-wrapper", field.getWrapperClass());
    }

    @Test
    void testSetName() {
        field.setName("customField");
        assertEquals("dummyField", field.getName());
    }

    @Test
    void testSetLabel() {
        field.setLabel("Custom Label");
        assertEquals("Test Label", field.getLabel());
    }

    @Test
    void testSetValue() {
        field.setValue("Something");
        assertEquals("Dummy Value", field.getValue());
    }

    @Test
    void testIsRelationshipDefaultFalse() {
        assertFalse(field.isRelationship());
    }

    static class DummyField extends FormField {
        @Override
        public String getLabel() {
            return "Test Label";
        }

        @Override
        public String getType() {
            return "dummy";
        }

        @Override
        public String getName() {
            return "dummyField";
        }

        @Override
        public String getPlaceholder() {
            return "Enter something";
        }

        @Override
        public boolean getRequired() {
            return true;
        }

        @Override
        public Object getValue() {
            return "Dummy Value";
        }

        @Override
        public Map<String, String> getValidationErrors() {
            return Map.of("required", "This field is required");
        }

        @Override
        public Map<String, String> getValidationRules() {
            return Map.of("required", "true");
        }

        @Override
        public Map<String, Object> getModelData() {
            return Map.of("label", "Test Label");
        }

//        @Override public String getLabel() { return super.label; }
//        @Override public String getType() { return "text"; }
//        @Override public String getName() { return super.name; }
//        @Override public String getPlaceholder() { return "Test"; }
//        @Override public boolean getRequired() { return true; }
//        @Override public Object getValue() { return super.value; }
//        @Override public Map<String, String> getValidationErrors() { return Map.of(); }
//        @Override public Map<String, String> getValidationRules() { return Map.of(); }
//        @Override public Map<String, Object> getModelData() { return Map.of(); }
    }
}