package com.kraftadmin;

import java.util.ArrayList;
import java.util.List;

/**
 * Meta model for MongoDB documents (replaces EntityMetaModel)
 */
public class DocumentMetaModel {
    private final Class<?> documentClass;
    private final List<Class<?>> subTypes;

    public DocumentMetaModel(Class<?> documentClass, List<Class<?>> subTypes) {
        this.documentClass = documentClass;
        this.subTypes = subTypes != null ? subTypes : new ArrayList<>();
    }

    public Class<?> getDocumentClass() {
        return documentClass;
    }

    public List<Class<?>> getSubTypes() {
        return subTypes;
    }

    public String getCollectionName() {
        return MongoDocumentScanner.resolveDocumentNameManual(documentClass);
    }

    public String getSimpleName() {
        return documentClass.getSimpleName();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     * @apiNote In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * The string output is not necessarily stable over time or across
     * JVM invocations.
     * @implSpec The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     */
    @Override
    public String toString() {
        return super.toString();
    }
}