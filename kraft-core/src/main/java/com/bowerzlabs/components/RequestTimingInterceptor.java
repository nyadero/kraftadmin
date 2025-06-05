package com.bowerzlabs.components;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RequestTimingInterceptor implements HandlerInterceptor {
    private static final String START_TIME_ATTRIBUTE = "startTime";

    /**
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }


//    /**
//     * @param request
//     * @param response
//     * @param handler
//     * @param modelAndView
//     * @throws Exception
//     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null && modelAndView != null) {
            long duration = System.currentTimeMillis() - startTime;
            modelAndView.addObject("responseTime", duration + "ms");
        }
    }


    /**
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
//        if (startTime != null) {
//            long duration = System.currentTimeMillis() - startTime;
//            response.setHeader("X-Response-Time", duration + "ms");
//            System.out.println("Request to " + request.getRequestURI() + " took " + duration + "ms");
//        }
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            // Don't modify redirect URLs!
            if (!response.isCommitted() && !isRedirect(response)) {
                response.addHeader("X-Response-Time", duration + "ms");
            }

            // You can log it instead:
            System.out.println("[Request Timing] " + request.getRequestURI() + " took " + duration + "ms");
        }
    }

    private boolean isRedirect(HttpServletResponse response) {
        int status = response.getStatus();
        return status >= 300 && status < 400;
    }
}
