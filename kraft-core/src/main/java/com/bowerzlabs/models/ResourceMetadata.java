package com.bowerzlabs.models;

/**
 * This class will store metadata for each resource field.
 */
public class ResourceMetadata {
    private ResourceName name;
    private String group;
    private String icon;
    private boolean editable;

    public ResourceMetadata(ResourceName name, String group, String icon, boolean editable) {
        this.name = name;
        this.group = group;
        this.icon = icon;
        this.editable = editable;
    }

    public ResourceMetadata(ResourceName name) {
        this.name = name;
    }

    // Getters and setters...
    public ResourceName getName() {
        return name;
    }

    public void setName(ResourceName name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString() {
        return "ResourceMetadata{" +
                "name=" + name +
                ", group='" + group + '\'' +
                ", icon='" + icon + '\'' +
                ", editable=" + editable +
                '}';
    }
}
