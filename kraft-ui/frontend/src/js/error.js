// Universal error handling and loader management
(function() {
    'use strict';

    // Function to hide loader
    function hideLoader() {
        const loader = document.getElementById('loader');
        if (loader) {
            loader.style.display = 'none';
        }
    }

    // Function to show error page
    function showErrorPage(errorMessage, errorType = 'Application Error') {
        // Hide loader first
        hideLoader();

        // Create error page HTML
        const errorPageHTML = `
            <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
                <div class="max-w-md w-full space-y-8">
                    <div class="text-center">
                        <div class="mx-auto h-12 w-12 text-red-500">
                            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                            </svg>
                        </div>
                        <h2 class="mt-6 text-3xl font-extrabold text-gray-900">
                            ${errorType}
                        </h2>
                        <div class="mt-4 bg-red-50 border border-red-200 rounded-md p-4">
                            <div class="flex">
                                <div class="flex-shrink-0">
                                    <svg class="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
                                    </svg>
                                </div>
                                <div class="ml-3">
                                    <p class="text-sm text-red-800">
                                        ${errorMessage}
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="mt-6 flex space-x-4 justify-center">
                            <button onclick="window.location.reload()" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                                Reload Page
                            </button>
                            <button onclick="window.history.back()" class="bg-gray-600 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">
                                Go Back
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Replace page content with error page
        document.body.innerHTML = errorPageHTML;
    }

    // Handle page load errors
    window.addEventListener('load', function() {
        hideLoader();
    });

    // Handle DOMContentLoaded for faster hiding
    document.addEventListener('DOMContentLoaded', function() {
        hideLoader();
    });

    // Handle JavaScript errors
//    window.addEventListener('error', function(event) {
//        console.error('JavaScript Error:', event.error);
//        showErrorPage(
//            `JavaScript Error: ${event.error.message || 'An unexpected error occurred'}`,
//            'JavaScript Error'
//        );
//    });

    // Handle unhandled promise rejections
    window.addEventListener('unhandledrejection', function(event) {
        console.error('Unhandled Promise Rejection:', event.reason);
        showErrorPage(
            `Promise Error: ${event.reason.message || event.reason || 'An unexpected error occurred'}`,
            'Promise Rejection'
        );
    });

    // Check for Thymeleaf errors on page load
    document.addEventListener('DOMContentLoaded', function() {
        // Check for Thymeleaf error indicators
        const thymeleafErrors = [
            // Common Thymeleaf error patterns
            document.querySelector('[th\\:text*="Error"]'),
            document.querySelector('[th\\:text*="Exception"]'),
            document.querySelector('.error-page'),
            document.querySelector('#error-message'),
            // Check for error text in body
            document.body.textContent.includes('TemplateInputException'),
            document.body.textContent.includes('TemplateProcessingException'),
            document.body.textContent.includes('SpelEvaluationException'),
            document.body.textContent.includes('Cannot execute'),
            document.body.textContent.includes('target is null'),
            document.body.textContent.includes('Exception evaluating SpringEL expression')
        ];

        // Check if any Thymeleaf error indicators are present
        const hasThymeleafError = thymeleafErrors.some(error => error);

        if (hasThymeleafError) {
            // Extract error message from page content
            let errorMessage = 'A template processing error occurred.';

            // Try to extract more specific error message
            const bodyText = document.body.textContent;
            const errorPatterns = [
                /Cannot execute[^:]*: ([^\\n]+)/,
                /Exception evaluating SpringEL expression[^:]*: ([^\\n]+)/,
                /TemplateInputException[^:]*: ([^\\n]+)/,
                /TemplateProcessingException[^:]*: ([^\\n]+)/
            ];

            for (const pattern of errorPatterns) {
                const match = bodyText.match(pattern);
                if (match) {
                    errorMessage = match[1] || match[0];
                    break;
                }
            }

            showErrorPage(errorMessage, 'Template Error');
        }
    });

    // Handle AJAX errors (if you're using fetch or XMLHttpRequest)
    const originalFetch = window.fetch;
    window.fetch = function(...args) {
        return originalFetch.apply(this, args)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                return response;
            })
            .catch(error => {
                console.error('Fetch Error:', error);
                showErrorPage(
                    `Network Error: ${error.message}`,
                    'Network Error'
                );
                throw error;
            });
    };

    // Fallback: Hide loader after a maximum timeout
    setTimeout(function() {
        hideLoader();
    }, 10000); // 10 seconds maximum

    // Make functions available globally for manual calls
    window.hideLoader = hideLoader;
    window.showErrorPage = showErrorPage;
})();