(function(){function n(){const e=document.getElementById("loader");e&&(e.style.display="none")}function r(e,t="Application Error"){n();const o=`
            <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
                <div class="max-w-md w-full space-y-8">
                    <div class="text-center">
                        <div class="mx-auto h-12 w-12 text-red-500">
                            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                            </svg>
                        </div>
                        <h2 class="mt-6 text-3xl font-extrabold text-gray-900">
                            ${t}
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
                                        ${e}
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
        `;document.body.innerHTML=o}window.addEventListener("load",function(){n()}),document.addEventListener("DOMContentLoaded",function(){n()}),window.addEventListener("unhandledrejection",function(e){console.error("Unhandled Promise Rejection:",e.reason),r(`Promise Error: ${e.reason.message||e.reason||"An unexpected error occurred"}`,"Promise Rejection")}),document.addEventListener("DOMContentLoaded",function(){if([document.querySelector('[th\\:text*="Error"]'),document.querySelector('[th\\:text*="Exception"]'),document.querySelector(".error-page"),document.querySelector("#error-message"),document.body.textContent.includes("TemplateInputException"),document.body.textContent.includes("TemplateProcessingException"),document.body.textContent.includes("SpelEvaluationException"),document.body.textContent.includes("Cannot execute"),document.body.textContent.includes("target is null"),document.body.textContent.includes("Exception evaluating SpringEL expression")].some(o=>o)){let o="A template processing error occurred.";const i=document.body.textContent,s=[/Cannot execute[^:]*: ([^\\n]+)/,/Exception evaluating SpringEL expression[^:]*: ([^\\n]+)/,/TemplateInputException[^:]*: ([^\\n]+)/,/TemplateProcessingException[^:]*: ([^\\n]+)/];for(const a of s){const d=i.match(a);if(d){o=d[1]||d[0];break}}r(o,"Template Error")}});const c=window.fetch;window.fetch=function(...e){return c.apply(this,e).then(t=>{if(!t.ok)throw new Error(`HTTP ${t.status}: ${t.statusText}`);return t}).catch(t=>{throw console.error("Fetch Error:",t),r(`Network Error: ${t.message}`,"Network Error"),t})},setTimeout(function(){n()},1e4),window.hideLoader=n,window.showErrorPage=r})();
