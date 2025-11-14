package com.kraftadmin.service;

import com.kraftadmin.EntitiesScanner;
import com.kraftadmin.EntityMetaModel;
import com.kraftadmin.analytics.AnalyticsComponent;
import com.kraftadmin.annotations.InternalAdminResource;
import com.kraftadmin.constants.TimePeriod;
import com.kraftadmin.dtos.AnalyticsData;
import com.kraftadmin.dtos.PeriodFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyzeData {
    private static final Logger log = LoggerFactory.getLogger(AnalyzeData.class);
    private static Set<AnalyticsComponent> analyticsComponents = new HashSet<>();
    private final CrudService crudService;
    private final EntitiesScanner entitiesScanner;
    private final SimpMessagingTemplate simpMessagingTemplate;
    Map<String, Object> analyticFilters = new HashMap<>();
    List<String> chartTypes = List.of("bar", "line");
    Random rand = new Random();
    private List<PeriodFilter> periodFilters = new ArrayList<>();

    public AnalyzeData(CrudService crudService, EntitiesScanner entitiesScanner, SimpMessagingTemplate simpMessagingTemplate) {
        this.crudService = crudService;
        this.entitiesScanner = entitiesScanner;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public static Set<AnalyticsComponent> getAnalyticsComponents() {
        return analyticsComponents;
    }

    // every hour
    @Scheduled(fixedDelay = (60000 * 60))
    @Async
    public void analyze() {
        analyticsComponents = new HashSet<>();
        periodFilters = new ArrayList<>();
        for (TimePeriod timePeriod : TimePeriod.values()) {
            PeriodFilter periodFilter = new PeriodFilter(timePeriod);
            periodFilters.add(periodFilter);
            analyticFilters.put(periodFilter.getPeriod().name(), periodFilter.getTime());
        }
        try {
            log.info("Running background analytics scan...");
            assert entitiesScanner != null;
            List<EntityMetaModel> entityMetaModelList = entitiesScanner.getAllEntityClasses().stream()
                    .filter(entityMetaModel -> !entityMetaModel.getEntityClass().getJavaType().isAnnotationPresent(InternalAdminResource.class))
                    .limit(4).toList();
            log.info("entityMetamodel {}", entityMetaModelList.size());
//            AnalyticsComponent analyticsComponent = null;
            PeriodFilter periodFilter = new PeriodFilter(TimePeriod.DAY);
            String randomType = chartTypes.get(rand.nextInt(chartTypes.size()));
            for (EntityMetaModel entityMetaModel : entityMetaModelList) {
                AnalyticsComponent analyticsComponent = new AnalyticsComponent(entityMetaModel.getEntityClass().getName(), "Analytics for " + entityMetaModel.getEntityClass().getName());
                analyticsComponent.setType(randomType);
//                new BarchartComponent(analyticsComponent);
                AnalyticsData data = crudService.loadAnalyticsData(entityMetaModel, periodFilter);
                analyticsComponent.setData(data);
                analyticsComponents.add(analyticsComponent);
            }
            simpMessagingTemplate.convertAndSend("/topic/analytics/", new Random().nextDouble());
            log.info("random {}, analyticsComponents = {}", new Random().nextDouble(), analyticsComponents.size());
        } catch (Exception e) {
            log.error("Error during analysis", e);
        }
    }

}
