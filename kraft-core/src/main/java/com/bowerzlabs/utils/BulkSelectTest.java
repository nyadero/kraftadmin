package com.bowerzlabs.utils;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public class BulkSelectTest {
    @PostMapping("/{entityName}/bulk-action")
    public String handleBulkAction(
            @PathVariable String entityName,
            @RequestParam("selectedIds") List<String> selectedIds,
            @RequestParam("bulkAction") String action,
            RedirectAttributes redirectAttributes
    ) {
        switch (action) {
            case "delete":
//                crudService.deleteMultiple(entityName, selectedIds);
                redirectAttributes.addFlashAttribute("success", "Deleted selected records.");
                break;
            case "export":
                // implement your own export logic
                redirectAttributes.addFlashAttribute("success", "Exported selected records.");
                break;
            default:
                redirectAttributes.addFlashAttribute("error", "Unknown bulk action.");
        }
        return "redirect:/admin/" + entityName;
    }

}

//<tr th:each="displayItem : ${displayItems}">
//    <td th:each="field : ${fieldNames}">
//        <div th:with="displayField=${displayItem[field]}">
//
//            <!-- Image -->
//            <img th:if="${displayField.type.name() == 'IMAGE'}"
//th:src="${displayField.value}" width="100" alt="Image"/>
//
//            <!-- Document or Link -->
//            <a th:if="${displayField.type.name() == 'DOCUMENT' or displayField.type.name() == 'LINK'}"
//th:href="${displayField.value}" target="_blank">📄 View</a>
//
//            <!-- Text -->
//            <span th:if="${displayField.type.name() == 'TEXT'}"
//th:text="${displayField.value}"></span>
//
//            <!-- Markdown (as HTML) -->
//            <div th:if="${displayField.type.name() == 'MARKDOWN'}"
//th:utext="${displayField.value}"></div>
//
//            <!-- Video -->
//            <video th:if="${displayField.type.name() == 'VIDEO'}"
//th:src="${displayField.value}" width="250" controls></video>
//
//            <!-- Audio -->
//            <audio th:if="${displayField.type.name() == 'AUDIO'}"
//th:src="${displayField.value}" controls></audio>
//
//            <!-- QR Code -->
//            <img th:if="${displayField.type.name() == 'QR'}"
//th:src="'data:image/png;base64,' + ${displayField.value}" width="100" alt="QR Code"/>
//
//            <!-- HTML (as trusted content) -->
//            <div th:if="${displayField.type.name() == 'HTML'}"
//th:utext="${displayField.value}"></div>
//
//            <!-- JSON (formatted) -->
//            <pre th:if="${displayField.type.name() == 'JSON'}"
//th:text="${displayField.value}"></pre>
//
//            <!-- Badge (e.g., status) -->
//            <span th:if="${displayField.type.name() == 'BADGE'}"
//th:text="${displayField.value}"
//th:class="'badge ' + ${displayField.value.toLowerCase()}"></span>
//
//            <!-- Map (lat,lng links) -->
//            <a th:if="${displayField.type.name() == 'MAP'}"
//th:href="'https://www.google.com/maps?q=' + ${displayField.value}"
//target="_blank">🗺️ View Map</a>
//
//        </div>
//    </td>
//</tr>
