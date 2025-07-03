package com.bowerzlabs.service;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.analytics.AnalyticsComponent;
import com.bowerzlabs.analytics.BarchartComponent;
import com.bowerzlabs.constants.TimePeriod;
import com.bowerzlabs.dtos.AnalyticsData;
import com.bowerzlabs.dtos.PeriodFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyzeData {
    private static final Logger log = LoggerFactory.getLogger(AnalyzeData.class);

    private final CrudService crudService;
    private final EntitiesScanner entitiesScanner;
    private static List<AnalyticsComponent> analyticsComponents = new ArrayList<>();
    private List<PeriodFilter> periodFilters = new ArrayList<>();
    Map<String, Object> analyticFilters = new HashMap<>();
    private final SimpMessagingTemplate simpMessagingTemplate;
    List<String> chartTypes = List.of("bar", "line");
    Random rand = new Random();


    public AnalyzeData(CrudService crudService, EntitiesScanner entitiesScanner, SimpMessagingTemplate simpMessagingTemplate) {
        this.crudService = crudService;
        this.entitiesScanner = entitiesScanner;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // every 60 seconds
//    @Scheduled(fixedDelay = 60000)
//    @Async
    public void analyze() {
        analyticsComponents = new ArrayList<>();
        periodFilters = new ArrayList<>();
        for(TimePeriod timePeriod: TimePeriod.values()){
            PeriodFilter periodFilter = new PeriodFilter(timePeriod);
            periodFilters.add(periodFilter);
            analyticFilters.put(periodFilter.getPeriod().name(), periodFilter.getTime());
        }
        try {
            log.info("Running background analytics scan...");
            assert entitiesScanner != null;
            List<EntityMetaModel> entityMetaModelList = entitiesScanner.getAllEntityClasses().stream().limit(6).toList();
            AnalyticsComponent analyticsComponent = null;
            PeriodFilter periodFilter = new PeriodFilter(TimePeriod.YEAR);
            String randomType = chartTypes.get(rand.nextInt(chartTypes.size()));
            for (EntityMetaModel entityMetaModel: entityMetaModelList){
                analyticsComponent = new AnalyticsComponent(entityMetaModel.getEntityClass().getName(), "Analytics for " + entityMetaModel.getEntityClass().getName());
                analyticsComponent.setType(randomType);
                new BarchartComponent(analyticsComponent);
                AnalyticsData data = crudService.loadAnalyticsData(entityMetaModel, periodFilter);
                analyticsComponent.setData(data);
                analyticsComponents.add(analyticsComponent);
            }
            simpMessagingTemplate.convertAndSend("/topic/analytics/", new Random().nextDouble());
            log.info("random {}", new Random().nextDouble());
        } catch (Exception e) {
            log.error("Error during analysis", e);
        }
    }

    public static List<AnalyticsComponent> getAnalyticsComponents() {
        return analyticsComponents;
    }

}
