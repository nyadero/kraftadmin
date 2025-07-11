package com.bowerzlabs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DynamicQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(DynamicQueryBuilder.class);

    public static CriteriaQuery<Object> buildQuery(
            CriteriaBuilder cb,
            Class<?> clazz,
            Map<String, String> filters,
            String searchKeyword
    ) {
        CriteriaQuery<Object> query = cb.createQuery(Object.class);
        Root<?> root = query.from(clazz);

        Predicate[] predicates = buildPredicates(cb, root, clazz, filters, searchKeyword);
        List<jakarta.persistence.criteria.Order> orderList = buildSortOrders(cb, root, filters);

        query.select(root).where(predicates);
        if (!orderList.isEmpty()) {
            query.orderBy(orderList);
        }

        return query;
    }


    public static Predicate[] buildPredicates(
            CriteriaBuilder cb,
            Root<?> root,
            Class<?> clazz,
            Map<String, String> filters,
            String searchKeyword
    ) {
        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase("search") || key.equalsIgnoreCase("sortBy") || key.equalsIgnoreCase("sortOrder"))
                continue;

            try {
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);
                Object val = EntityUtil.convertValue(field.getType(), entry.getValue());
                predicates.add(cb.equal(root.get(key), val));
            } catch (Exception e) {
                // skip invalid fields
            }
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == String.class) {
                    predicates.add(cb.like(cb.lower(root.get(field.getName())), "%" + searchKeyword.toLowerCase() + "%"));
                }
            }
        }

        return predicates.toArray(new Predicate[0]);
    }

    public static List<jakarta.persistence.criteria.Order> buildSortOrders(
            CriteriaBuilder cb,
            Root<?> root,
            Map<String, String> filters
    ) {
        List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();

        String sortBy = filters.get("sortBy");
        String sortOrder = filters.get("sortOrder");

        if (sortBy != null && !sortBy.isBlank()) {
            sortBy = unformatLabel(sortBy);
            boolean isAscending = sortOrder == null || sortOrder.equalsIgnoreCase("asc");
            orders.add(isAscending ? cb.asc(root.get(sortBy)) : cb.desc(root.get(sortBy)));
        }else{
            orders.add(cb.desc(root.get("id")));
        }

        return orders;
    }

    public static String unformatLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return "";
        }

        // Lowercase the label and split by space
        String[] parts = label.trim().split("\\s+");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());

        // Reconstruct camelCase
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase());
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

    public static long getTotal(CriteriaBuilder cb1, Class<?> clazz, EntityManager entityManager, CriteriaQuery<Object> query) {
        log.info("predicates {}", query.getRestriction());
        CriteriaQuery<Long> countQuery = cb1.createQuery(Long.class);
        Root<?> countRoot = countQuery.from(clazz);
        countQuery.select(cb1.count(countRoot)).where();
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}

