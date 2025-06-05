package com.bowerzlabs.events;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Objects;

@Component
public class UIEventListener implements ApplicationListener<UIEvent> {
    private static final Logger log = LoggerFactory.getLogger(UIEventListener.class);
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public UIEventListener(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void onApplicationEvent(UIEvent event) {
        log.info("Toast message: {} | Type: {}", event.getMessage(), event.getStatus());

         // Use FlashMap to store attributes for the next request
        FlashMap flashMap = new FlashMap();
        flashMap.put("toastMessage", event.getMessage());
        flashMap.put("toastType", event.getStatus());

        // Save the FlashMap for the next request
        Objects.requireNonNull(RequestContextUtils.getFlashMapManager(request)).saveOutputFlashMap(flashMap, request, response);
    }
}
