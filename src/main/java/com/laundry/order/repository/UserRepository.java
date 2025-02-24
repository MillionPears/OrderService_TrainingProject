package com.laundry.order.repository;

import com.laundry.order.entity.User;
import com.laundry.order.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
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
