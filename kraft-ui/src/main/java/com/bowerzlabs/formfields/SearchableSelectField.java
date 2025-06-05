package com.bowerzlabs.formfields;

import java.util.Map;

public class SearchableSelectField extends FormField {
    private String label;
    private String name;
    private final String placeholder;
    private final Map<Object, Object> options;
    private final boolean required;
    private final Object value;
    private final Map<String, String> validationErrors;
    private final Map<String, String> validationRules;

    public SearchableSelectField(
            String label,
            String name,
            String placeholder,
            Map<Object, Object> options,
            Object value,
            boolean required,
            Map<String, String> validationErrors,
            Map<String, String> validationRules
    ) {
        super();
        this.label = formatLabel(label);
        this.name = name;
        this.placeholder = placeholder;
        this.options = options;
        this.required = required;
        this.value = value;
        this.validationErrors = validationErrors;
        this.validationRules = validationRules;
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    @Override
    public String getType() {
        return "searchable-select";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public boolean getRequired() {
        return required;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public Map<Object, Object> getOptions() {
        return options;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    @Override
    public Map<String, String> getValidationRules() {
        return Map.of(); // Optional: you can inject rules too
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public void setLabel(String s) {
        this.label = s;
    }

//    @Override
//    public String toHtml(String label,
//                         String type,
//                         String placeholder,
//                         String name,
//                         Object value,
//                         boolean required,
//                         Map<String, String> validationErrors) {
//
//        StringBuilder html = new StringBuilder();
//
//        // Start form group and label
//        html.append("<div class='form-group'>")
//                .append("<label for='").append(name).append("'>").append(label).append(required ? " *" : "").append("</label>");
//
//        // Create the search input field
//        html.append("<input type='text' id='").append(name).append("' name='").append(name).append("' ")
//                .append("class='form-control' placeholder='").append(placeholder).append("' autocomplete='off' onkeyup='searchItems(this.value)' />");
//
//        // Add the results container (hidden by default)
//        html.append("<div id='searchResults' class='results-container' style='display:none;'>")
//                .append("<!-- The search results will be dynamically inserted here -->")
//                .append("</div>");
//
//        // Close form group
//        html.append("</div>");
//
//        // Add validation error message if it exists
//        if (validationErrors != null && !validationErrors.isEmpty() && validationErrors.containsKey(name)) {
//            html.append("<span style='color:red;display:none;' id='error-message'>")
//                    .append(validationErrors.get(name))
//                    .append("</span><br/>");
//        }
//
//        // Add JavaScript to handle search dynamically
//        html.append("<script>")
//                .append("function searchItems(query) {")
//                .append("if (query.length < 3) {")
//                .append("document.getElementById('").append(name).append("-results').style.display = 'none';")
//                .append("return;")
//                .append("} else {")
//                .append("var xhr = new XMLHttpRequest();")
//                .append("xhr.open('GET', '").append(endpoint).append("/search?searchField=").append(name).append("&query=' + encodeURIComponent(query), true);")
//                .append("xhr.onreadystatechange = function() {")
//                .append("if (xhr.readyState == 4 && xhr.status == 200) {")
//                .append("var results = JSON.parse(xhr.responseText);")
//                .append("var resultsContainer = document.getElementById('").append(name).append("-results');")
//                .append("resultsContainer.innerHTML = '';")
//                .append("results.forEach(function(result) {")
//                .append("var div = document.createElement('div');")
//                .append("div.classList.add('result-item');")
//                .append("div.innerHTML = result['").append(displayFieldName).append("'];")
//                .append("div.onclick = function() {")
//                .append("document.getElementById('").append(name).append("').value = result['").append(displayFieldName).append("'];")
//                .append("resultsContainer.style.display = 'none';")
//                .append("};")
//                .append("resultsContainer.appendChild(div);")
//                .append("});")
//                .append("resultsContainer.style.display = 'block';")
//                .append("} else {")
//                .append("document.getElementById('").append(name).append("-results').style.display = 'none';")
//                .append("}")
//                .append("};")
//                .append("xhr.send();")
//                .append("}")
//                .append("}")
//                .append("</script>");
//
//
//        // Return the generated HTML
//        html1 = html.toString();
//        return html.toString();
//    }

    /**
     * builds the input model data
     */
    @Override
    public Map<String, Object> getModelData() {
        return Map.of(
                "label", label,
                "name", name,
                "placeholder", placeholder,
                "options", options,
                "required", required,
                "validationErrors", validationErrors,
                "validationRules", validationRules
        );
    }

    @Override
    public String toString() {
        return "SearchableSelectField{" +
                "label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", options=" + options +
                ", required=" + required +
                ", value=" + value +
                ", validationErrors=" + validationErrors +
                ", validationRules=" + validationRules +
                '}';
    }
}
