package com.kraftadmin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;

@Controller
public class KraftErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(KraftErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String errorMessage = "An unexpected error occurred";
        String errorType = "Application Error";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            switch (statusCode) {
                case 404:
                    errorMessage = "Page not found";
                    errorType = "404 Error";
                    break;
                case 500:
                    errorMessage = "Internal server error";
                    errorType = "500 Error";
                    break;
                case 403:
                    errorMessage = "Access forbidden";
                    errorType = "403 Error";
                    break;
                default:
                    errorMessage = "HTTP " + statusCode + " Error";
                    errorType = "HTTP Error";
            }
        }

        if (exception != null) {
            Throwable throwable = (Throwable) exception;
            log.error("Error occurred", throwable);

            // Handle specific exception types
            if (throwable instanceof TemplateInputException ||
                    throwable instanceof TemplateProcessingException) {
                errorType = "Template Error";
                errorMessage = extractThymeleafErrorMessage(throwable);
            } else if (throwable.getCause() != null &&
                    throwable.getCause().getMessage() != null) {
                errorMessage = throwable.getCause().getMessage();
            } else if (throwable.getMessage() != null) {
                errorMessage = throwable.getMessage();
            }
        }

        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorType", errorType);
        model.addAttribute("showBackButton", true);

        return "error";
    }

    private String extractThymeleafErrorMessage(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null) return "Template processing error occurred";

        // Extract meaningful part of Thymeleaf error messages
        if (message.contains("Cannot execute")) {
            int start = message.indexOf("Cannot execute");
            int end = message.indexOf(" (", start);
            if (end > start) {
                return message.substring(start, end);
            }
        }

        if (message.contains("Exception evaluating SpringEL expression")) {
            int start = message.indexOf("Exception evaluating SpringEL expression");
            int end = message.indexOf(" (", start);
            if (end > start) {
                return message.substring(start, end);
            }
        }

        return message.length() > 200 ? message.substring(0, 200) + "..." : message;
    }
}
