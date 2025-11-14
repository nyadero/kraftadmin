package com.kraftadmin.kraft_jpa_entities;

import com.kraftadmin.enums.JobMatch;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class JobMatchResults {
    @Setter
    private JobMatch jobMatch;
    private final Map<String, List<String>> matchedAttributes = new HashMap<>();
    private final Map<String, List<String>> notMetAttributes = new HashMap<>();
    private final Map<String, List<String>> requiredAttributes = new HashMap<>();



    //    public double getTotalScore() {
//        return totalScore;
//    }

    public void addRequiredAttribute(String key, String attribute) {
        requiredAttributes.computeIfAbsent(key, k -> new ArrayList<>()).add(attribute);
    }

    public void addMatchedAttribute(String key, List<String> attributes) {
        matchedAttributes.computeIfAbsent(key, k -> new ArrayList<>()).addAll(attributes);
    }

    public void addNotMetAttribute(String key, List<String> attributes) {
        notMetAttributes.computeIfAbsent(key, k -> new ArrayList<>()).addAll(attributes);
    }


    public double calculateTotalScorePercentage() {
        double totalAttributes = matchedAttributes.size() + notMetAttributes.size();
        double percentage = (totalAttributes > 0) ? (double) matchedAttributes.size() / requiredAttributes.size() * 100.0 : 0.0;

        // Ensure percentage is within the valid range [0, 100]
        double formattedPercentage = Math.min(Math.max(percentage, 0.0), 100.0);

        // Format the percentage to two decimal points
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(formattedPercentage));
    }

    public double getTotalScore() {
        return calculateTotalScorePercentage();
    }


    public void setTotalPossibleScore(double totalPossibleScore) {
    }
}
