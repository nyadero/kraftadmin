package com.kraftadmin.components;

import com.kraftadmin.service.AnalyzeData;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class AnalyzeTrigger {

    private final AnalyzeData analyzeData;

    public AnalyzeTrigger(AnalyzeData analyzeData) {
        this.analyzeData = analyzeData;
    }

    // single startup run
    @EventListener(ApplicationReadyEvent.class)
    public void onAppReady() {
        analyzeData.analyze();
    }

}