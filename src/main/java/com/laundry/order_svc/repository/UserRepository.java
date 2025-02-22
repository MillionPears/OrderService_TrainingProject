package com.laundry.order_svc.repository;

import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

  boolean existsByPhoneNumber(String phoneNumber);

  @Query(value = "SELECT * FROM users u WHERE (u.name =:name OR u.name IS NULL  ) AND ( u.gender =:gender OR u.gender IS NULL )  ", nativeQuery = true)
  Page<User> filterByNameAndGenderWithIndex(@Param("gender") Gender gender,
                                            @Param("name") String name,
                                            Pageable pageable);
//  @Transactional
//  @Modifying
//  @Query(value = " UPDATE users u SET u.name =:name WHERE u.user_id IN LIST_ID=:list",nativeQuery = true)
//  Long updateByListId(@Param("list") List<UUID> list); // tra ve so luong row updated
}
