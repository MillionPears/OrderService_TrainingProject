package com.laundry.order_svc.repository;

import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    boolean existsByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT * FROM users u WHERE (: gender IS NULL OR u.gender =:gender) AND (u.name =:name) ",nativeQuery = true)
    Page<User> filterByNameAndGenderWithIndex(@Param("name") String name,
                                                      @Param("gender") Gender gender,
                                                      Pageable pageable);
}
