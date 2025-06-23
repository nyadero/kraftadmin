package com.bowerzlabs.utils;

import com.bowerzlabs.constants.FieldType;
import com.bowerzlabs.dtos.FieldValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Slf4j
public class DisplayUtils {

//    private final Tika tika = new Tika();
    private static FieldType getDataType(Object value) {

        if (value == null) return FieldType.TEXT;

        String val = value.toString().trim().toLowerCase();

        if (val.endsWith(".jpg") || val.endsWith(".jpeg") || val.endsWith(".png") || val.endsWith(".gif") || val.endsWith(".webp")) {
            return FieldType.IMAGE;
        } else if (val.endsWith(".pdf") || val.endsWith(".doc") || val.endsWith(".docx")) {
            return FieldType.DOCUMENT;
        } else if (val.startsWith("http")) {
            return FieldType.LINK;
        } else if (isColorValue(val)) {
            return FieldType.COLOR;
        }if (value instanceof byte[]) {
            return FieldType.BINARY;
        } else {
            return FieldType.TEXT;
        }
    }
    
    public static FieldValue resolveFieldValue(Object value) {
        Tika tika = new Tika();

        log.info("tika value {}", tika.detect(String.valueOf(value)));
        FieldValue fieldValue = new FieldValue();

        if (value == null) {
            fieldValue.setFieldType(FieldType.TEXT);
            fieldValue.setValue("");
            return fieldValue;
        }

        // Step 1: Handle raw byte[]
        if (value instanceof byte[] bytes) {
            String base64 = Base64.getEncoder().encodeToString(bytes);

            if (isImage(bytes)) {
                fieldValue.setFieldType(FieldType.IMAGE);
                fieldValue.setValue("data:image/png;base64," + base64);
            } else if (isPdf(bytes)) {
                fieldValue.setFieldType(FieldType.DOCUMENT);
                fieldValue.setValue("data:application/pdf;base64," + base64);
            } else if (isAudio(bytes)) {
                fieldValue.setFieldType(FieldType.AUDIO);
                fieldValue.setValue("data:audio/mpeg;base64," + base64);
            } else if (isVideo(bytes)) {
                fieldValue.setFieldType(FieldType.VIDEO);
                fieldValue.setValue("data:video/mp4;base64," + base64);
            } else {
                fieldValue.setFieldType(FieldType.BINARY);
                fieldValue.setValue("data:application/octet-stream;base64," + base64);
            }

            return fieldValue;
        }

        // Step 2: Handle String values
        String val = value.toString().trim().toLowerCase();

        if (val.startsWith("data:image/")) {
            fieldValue.setFieldType(FieldType.IMAGE);
        } else if (val.startsWith("data:application/pdf")) {
            fieldValue.setFieldType(FieldType.DOCUMENT);
        } else if (val.startsWith("data:audio/")) {
            fieldValue.setFieldType(FieldType.AUDIO);
        } else if (val.startsWith("data:video/")) {
            fieldValue.setFieldType(FieldType.VIDEO);
        } else if (isLikelyBase64(val)) {
            fieldValue.setFieldType(FieldType.BINARY);
            val = "data:application/octet-stream;base64," + val;
        } else if (val.endsWith(".jpg") || val.endsWith(".jpeg") || val.endsWith(".png") || val.endsWith(".gif") || val.endsWith(".webp")) {
            fieldValue.setFieldType(FieldType.IMAGE);
        } else if (val.endsWith(".pdf") || val.endsWith(".doc") || val.endsWith(".docx")) {
            fieldValue.setFieldType(FieldType.DOCUMENT);
        } else if (val.endsWith(".mp3") || val.endsWith(".wav") || val.endsWith(".aac")) {
            fieldValue.setFieldType(FieldType.AUDIO);
        } else if (val.endsWith(".mp4") || val.endsWith(".mov") || val.endsWith(".avi")) {
            fieldValue.setFieldType(FieldType.VIDEO);
        } else if (val.startsWith("http")) {
            fieldValue.setFieldType(FieldType.LINK);
        } else if (isColorValue(val)) {
            fieldValue.setFieldType(FieldType.COLOR);
        } else {
            fieldValue.setFieldType(FieldType.TEXT);
        }

        fieldValue.setValue(value);
        return fieldValue;
    }

    public static FieldValue resolveFieldValue2(Object value) {
        FieldValue fieldValue = new FieldValue();
        Tika tika = new Tika();

        if (value == null) {
            fieldValue.setFieldType(FieldType.TEXT);
            fieldValue.setValue("");
            return fieldValue;
        }

        try {
            // Handle byte[] input
            if (value instanceof byte[] bytes) {
                String mime = tika.detect(bytes);
                String base64 = Base64.getEncoder().encodeToString(bytes);
                log.info("Detected MIME from bytes: {}", mime);

                if (mime.startsWith("image/")) {
                    fieldValue.setFieldType(FieldType.IMAGE);
                    fieldValue.setValue("data:" + mime + ";base64," + base64);
                } else if (mime.equals("application/pdf") || mime.contains("word") || mime.contains("document")) {
                    fieldValue.setFieldType(FieldType.DOCUMENT);
                    fieldValue.setValue("data:" + mime + ";base64," + base64);
                } else if (mime.startsWith("audio/")) {
                    fieldValue.setFieldType(FieldType.AUDIO);
                    fieldValue.setValue("data:" + mime + ";base64," + base64);
                } else if (mime.startsWith("video/")) {
                    fieldValue.setFieldType(FieldType.VIDEO);
                    fieldValue.setValue("data:" + mime + ";base64," + base64);
                } else {
                    fieldValue.setFieldType(FieldType.BINARY);
                    fieldValue.setValue("data:" + mime + ";base64," + base64);
                }

                return fieldValue;
            }

            // Handle string inputs: can be data URLs, base64, file paths, or links
            String val = value.toString().trim().toLowerCase();

            if (val.startsWith("data:")) {
                String mime = val.substring(5, val.indexOf(';'));
                fieldValue.setFieldType(resolveFieldTypeFromMime(mime));
                fieldValue.setValue(value);
                return fieldValue;
            }

            if (isLikelyBase64(val)) {
                String mime = tika.detect(Base64.getDecoder().decode(val));
                fieldValue.setFieldType(resolveFieldTypeFromMime(mime));
                fieldValue.setValue("data:" + mime + ";base64," + val);
                return fieldValue;
            }

            if (val.startsWith("http")) {
                fieldValue.setFieldType(FieldType.LINK);
                fieldValue.setValue(val);
                return fieldValue;
            }

            if (isColorValue(val)) {
                fieldValue.setFieldType(FieldType.COLOR);
                fieldValue.setValue(val);
                return fieldValue;
            }

            // Last resort: try to detect using tika based on content
            String mime = tika.detect(val);
            log.info("Tika-detected MIME from String: {}", mime);
            fieldValue.setFieldType(resolveFieldTypeFromMime(mime));
            fieldValue.setValue(val);
            return fieldValue;

        } catch (Exception e) {
            log.warn("Tika MIME detection failed: {}", e.getMessage());
            fieldValue.setFieldType(FieldType.TEXT);
            fieldValue.setValue(value);
            return fieldValue;
        }
    }


    private static boolean isAudio(byte[] data) {
        // Basic magic number check for MP3 (ID3 header) or WAV
        return data.length > 3 && (data[0] == 'I' && data[1] == 'D' && data[2] == '3') || // MP3
                (data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F');    // WAV
    }

    private static boolean isVideo(byte[] data) {
        // Basic MP4 header check (ftyp in first few bytes)
        return data.length > 12 && new String(data, 4, 4).equals("ftyp");
    }


    private static boolean isLikelyBase64(String val) {
        return val.matches("^[A-Za-z0-9+/=\\s]+$") && val.length() > 100;
    }

    private static boolean isImage(byte[] bytes) {
        return bytes.length >= 4 &&
                (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) ||      // JPEG
                (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50);        // PNG
    }

    private static boolean isPdf(byte[] bytes) {
        return bytes.length >= 4 &&
                bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
    }


    private static boolean isColorValue(String val) {
        return isHexColor(val) || isRgbOrHsl(val) || isNamedColor(val);
    }

//    private boolean isHexColor(String val) {
//        return val.matches("^#(?:[0-9a-f]{3}|[0-9a-f]{4}|[0-9a-f]{6}|[0-9a-f]{8})$");
//    }

    private static boolean isHexColor(String val) {
        return val.matches("(?i)^#(?:[0-9a-f]{3}|[0-9a-f]{4}|[0-9a-f]{6}|[0-9a-f]{8})$");
    }


    private static boolean isRgbOrHsl(String val) {
        return val.matches("^(rgb|rgba|hsl|hsla)\\s*\\(.*\\)$");
    }

    private static boolean isNamedColor(String val) {
        // Simple check for common CSS color names
        Set<String> cssColors = Set.of(
                "black", "white", "red", "green", "blue", "yellow", "pink", "cyan", "magenta",
                "orange", "purple", "gray", "brown", "lime", "navy", "teal", "maroon", "olive",
                "silver", "gold", "beige", "coral", "crimson", "indigo", "ivory", "khaki", "lavender",
                "plum", "salmon", "tan", "turquoise", "violet", "wheat"
        );
        return cssColors.contains(val);
    }


    /**
     *     format field value for display
     */
    public static String formatForDisplay(Class<?> fieldType, Object value) {
        if (value == null) return "";

        if (value instanceof byte[] bytes) {
            // Try to detect and encode the content
            String base64 = Base64.getEncoder().encodeToString(bytes);
            if (isImage(bytes)) {
                return "data:image/png;base64," + base64;
            } else if (isPdf(bytes)) {
                return "data:application/pdf;base64," + base64;
            } else {
                return "data:application/octet-stream;base64," + base64;
            }
        }

        if (fieldType == LocalDateTime.class) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"));
        } else if (fieldType == LocalTime.class) {
            return ((LocalTime) value).format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else if (fieldType == LocalDate.class) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (fieldType == ZonedDateTime.class) {
            ZonedDateTime zdt = (ZonedDateTime) value;
            return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z"));
        } else if (fieldType == OffsetDateTime.class) {
            OffsetDateTime odt = (OffsetDateTime) value;
            return odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a O"));
        } else if (fieldType == Date.class) {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format((Date) value);
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            return String.join(", ", ((Collection<?>) value).stream().map(Object::toString).toList());
        } else if (fieldType.isEnum()) {
            return value.toString();
        }

        return value.toString();
    }

    private static FieldType resolveFieldTypeFromMime(String mime) {
        if (mime == null) return FieldType.TEXT;
        if (mime.startsWith("image/")) return FieldType.IMAGE;
        if (mime.startsWith("audio/")) return FieldType.AUDIO;
        if (mime.startsWith("video/")) return FieldType.VIDEO;
        if (mime.equals("application/pdf") || mime.contains("word") || mime.contains("document")) return FieldType.DOCUMENT;
        if (mime.contains("octet-stream")) return FieldType.BINARY;
        return FieldType.TEXT;
    }


}
