package com.laundry.order_svc.repository.specification;

import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.util.IllegalFormatWidthException;

public class UserSpecification {
    public static Specification<User> hasName(String name) {
        return(root, query, criteriaBuilder) -> {
            if(name ==null ||name.isEmpty()){
                throw new IllegalArgumentException("name must not be null or empty");
            }
            //System.out.println("haha "+ criteriaBuilder.like(root.get("name"), "%"+name+"%"));
            return criteriaBuilder.like(root.get("name"), "%"+name+"%");
        };
    }

    public static Specification<User> hasPhoneNumber(String phoneNumber) {
        return (root, query, criteriaBuilder) -> {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber);
        };
    }

    public static Specification<User> hasGender(Gender gender){
        return ((root, query, criteriaBuilder) -> {
            if(gender==null) throw new IllegalArgumentException("gender must be not null");
           return criteriaBuilder.equal(root.get("gender"), gender);
        });

    }

}
