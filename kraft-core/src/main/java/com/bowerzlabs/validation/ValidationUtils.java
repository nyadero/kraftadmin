package com.bowerzlabs.validation;

import com.bowerzlabs.annotations.FormInputType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ValidationUtils {
    public static String extractValidationRules(Field field) {
        StringBuilder rules = new StringBuilder();

        // Required validation rule
        if (isFieldRequired(field)) {
            rules.append("required|");
        }

        // Size (for strings, collections)
        if (field.isAnnotationPresent(Size.class)) {
            Size size = field.getAnnotation(Size.class);
            rules.append("size(min:").append(size.min()).append(",max:").append(size.max()).append(")|");
        }

        // Min / Max (integer types)
        if (field.isAnnotationPresent(Min.class)) {
            Min min = field.getAnnotation(Min.class);
            rules.append("min:").append(min.value()).append("|");
        }
        if (field.isAnnotationPresent(Max.class)) {
            Max max = field.getAnnotation(Max.class);
            rules.append("max:").append(max.value()).append("|");
        }

        // Decimal Min / Max
        if (field.isAnnotationPresent(DecimalMin.class)) {
            DecimalMin min = field.getAnnotation(DecimalMin.class);
            rules.append("decimalMin:").append(min.value()).append("|");
        }

        if (field.isAnnotationPresent(DecimalMax.class)) {
            DecimalMax max = field.getAnnotation(DecimalMax.class);
            rules.append("decimalMax:").append(max.value()).append("|");
        }

        // Positive / Negative
        if (field.isAnnotationPresent(Positive.class)) rules.append("positive|");
        if (field.isAnnotationPresent(PositiveOrZero.class)) rules.append("positiveOrZero|");
        if (field.isAnnotationPresent(Negative.class)) rules.append("negative|");
        if (field.isAnnotationPresent(NegativeOrZero.class)) rules.append("negativeOrZero|");

        // Email
        if (field.isAnnotationPresent(Email.class)) {
            rules.append("email|");
        }

        // Digits
        if (field.isAnnotationPresent(Digits.class)) {
            Digits digits = field.getAnnotation(Digits.class);
            rules.append("digits(integer:").append(digits.integer())
                    .append(",fraction:").append(digits.fraction()).append(")|");
        }

        // AssertTrue / AssertFalse
        if (field.isAnnotationPresent(AssertTrue.class)) rules.append("mustBeTrue|");
        if (field.isAnnotationPresent(AssertFalse.class)) rules.append("mustBeFalse|");

        // Past / Future
        if (field.isAnnotationPresent(Past.class)) rules.append("past|");
        if (field.isAnnotationPresent(Future.class)) rules.append("future|");

        // Regex
        if (field.isAnnotationPresent(Pattern.class)) {
            Pattern pattern = field.getAnnotation(Pattern.class);
            rules.append("regex:").append(pattern.regexp()).append("|");
        }

        // Column nullable
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if (!column.nullable()) rules.append("required|");
        }

        // Form input type-based validation
        if (field.isAnnotationPresent(FormInputType.class)) {
            FormInputType formInputType = field.getAnnotation(FormInputType.class);
            switch (formInputType.value()) {
                case TEXT, TEXTAREA, WYSIWYG -> rules.append("string|");
                case NUMBER, RANGE -> rules.append("numeric|");
                case COLOR -> rules.append("hexColor|");
                case CHECKBOX, RADIO -> rules.append("boolean|");
                case EMAIL -> rules.append("email|");
                case PASSWORD -> rules.append("minLength:8|mustContainUppercase|mustContainSpecialChar|");
                case FILE, IMAGE -> rules.append("file|");
                case DATE, DATETIME, TIME -> rules.append("date|");
                case TEL -> rules.append("tel|");
                case URL -> rules.append("url|");
            }
        }
//        log.info("Validation rules {}", rules);
        return rules.toString().replaceAll("\\|$", ""); // Remove trailing "|"
    }

    private static boolean isFieldRequired(Field field) {
        if (field.isAnnotationPresent(NotNull.class) ||
                field.isAnnotationPresent(NotBlank.class) ||
                field.isAnnotationPresent(NotEmpty.class)) {
            return true;
        }

        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        if (manyToOne != null && !manyToOne.optional()) return true;

        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        return oneToOne != null && !oneToOne.optional();
    }

    public static Map<String, String> validateValues(Map<String, String> validationRules, Map<String, String> formValues, Map<String, String> fieldLabels) {
        Map<String, String> validationErrors = new HashMap<>();
        for (Map.Entry<String, String> entry : validationRules.entrySet()) {
            String fieldName = entry.getKey();
            String rules = entry.getValue();
            String fieldValue = formValues.getOrDefault(fieldName, "").trim();

            // Required validation
            if (rules.contains("required") && fieldValue.isEmpty()) {
                validationErrors.put(fieldName, fieldLabels.get(fieldName) + " is required.");
            }

            // Size validation
            if (rules.contains("size")) {
                int min = extractSizeValue(rules, "min");
                int max = extractSizeValue(rules, "max");
//                log.info("Extracted size for {}: min={}, max={}", fieldName, min, max);
                if (fieldValue.length() < min || fieldValue.length() > max) {
                    validationErrors.put(fieldName, fieldLabels.get(fieldName) + " must be between " + min + " and " + max + " characters.");
                }
            }

            // Regex validation
            if (rules.contains("regex")) {
                String regex = extractRegex(rules);
                log.info("Extracted regex for {}: {}", fieldName, regex);
                if (!fieldValue.matches(regex)) {
                    validationErrors.put(fieldName, fieldLabels.get(fieldName) + " format is invalid.");
                }
            }
        }
        return validationErrors;
    }

    // Helper methods to extract size constraints
    private static int extractSizeValue(String rules, String type) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("size\\([^)]*" + type + ":(\\d+)[^)]*\\)");
        java.util.regex.Matcher matcher = pattern.matcher(rules);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return (type.equals("min")) ? 5 : Integer.MAX_VALUE; // Defaults
    }

    private static String extractRegex(String rules) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("regex: ([^|]+)");
        java.util.regex.Matcher matcher = pattern.matcher(rules);

        return matcher.find() ? matcher.group(1).trim() : "";
    }
}
