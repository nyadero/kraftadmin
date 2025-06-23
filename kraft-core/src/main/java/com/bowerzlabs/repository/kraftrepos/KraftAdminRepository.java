package com.bowerzlabs.repository.kraftrepos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Map;

public abstract class KraftAdminRepository implements PagingAndSortingRepository<Class<?>, Object> {
    public abstract Object save(Class<?> clazz, Map<String, String> fieldValues, Object current);

    abstract Object findById(Class<?> clazz, Object id);

    abstract Page<?> findAll(Class<?> clazz, Pageable pageable);

    public abstract Iterable<?> findAll(Class<?> clazz, Sort sort);
}
