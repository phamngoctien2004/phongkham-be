package com.dcm.demo.helpers;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper {
    public static <T> Specification<T> contain(String keyword, List<String> fields){
        return (root, query, cb) -> {
            if(keyword == null || keyword.isEmpty()) {
                return cb.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            for(String field : fields){
                predicates.add(
                        cb.like(root.get(field),"%"+ keyword.trim() + "%")
                );
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
