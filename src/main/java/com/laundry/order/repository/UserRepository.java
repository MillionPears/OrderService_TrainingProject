package com.laundry.order.repository;

import com.laundry.order.entity.User;
import com.laundry.order.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

  boolean existsByPhoneNumber(String phoneNumber);

  @Query(value = """
    SELECT * FROM users u
    WHERE (:name IS NULL OR u.name = :name )
    AND (:gender IS NULL OR u.gender = :gender)
    """, nativeQuery = true)
  Page<User> filterByNameAndGenderWithIndex(@Param("gender") String gender,
                                            @Param("name") String name,
                                            Pageable pageable);
  @Transactional
  @Modifying
  @Query(value = " UPDATE users u SET u.point =:point WHERE u.id IN (:userIds)",nativeQuery = true)
  int updatePointByListId(@Param("point") Integer point,
                     @Param("userIds") List<UUID> userIds);
}
