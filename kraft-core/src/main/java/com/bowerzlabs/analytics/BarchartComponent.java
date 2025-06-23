package com.bowerzlabs.analytics;

import com.bowerzlabs.dtos.AnalyticsData;

public class BarchartComponent implements AnalyticsObserver{
    private AnalyticsComponent analyticsComponent;

    public BarchartComponent(AnalyticsComponent analyticsComponent) {
        this.analyticsComponent = analyticsComponent;
        analyticsComponent.addSubscriber(this);
        analyticsComponent.setTemplatePath("fragments/analytics/bar-chart");
    }

    @Override
    public void update(AnalyticsData data) {
//        assert simpMessagingTemplate != null;
//        simpMessagingTemplate.convertAndSend(data);
        System.out.println(analyticsComponent.getTitle() + " " + analyticsComponent.getDescription() + " " + analyticsComponent.getData());
    }

}

//<!--<div th:fragment="chart(title, labels, values)" xmlns:th="http://www.w3.org/1999/xhtml"-->
//<!--     xmlns:th="http://www.w3.org/1999/xhtml" xmlns:th="http://www.w3.org/1999/xhtml">-->
//<!--    <h2 th:text="${title}"></h2>-->
//<!--    <canvas th:attr="id=${title.replaceAll(' ', '')}"></canvas>-->
//<!--    <script th:inline="javascript">-->
//<!--        /*<![CDATA[*/-->
//<!--        const ctx = document.getElementById([[${title.replaceAll(' ', '')}]]).getContext('2d');-->
//<!--        new Chart(ctx, {-->
//        <!--            type: 'bar',-->
//        <!--            data: {-->
//        <!--                labels: [[${labels}]],-->
//        <!--                datasets: [{-->
//        <!--                    label: '[[${title}]]',-->
//        <!--                    data: [[${values}]],-->
//        <!--                    backgroundColor: 'rgba(75, 192, 192, 0.6)'-->
//        <!--                }]-->
//        <!--            }-->
//        <!--        });-->
//<!--        /*]]>*/-->
//<!--    </script>-->
//<!--</div>-->

