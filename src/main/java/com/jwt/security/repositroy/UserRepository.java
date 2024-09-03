package com.jwt.security.repositroy;

import com.jwt.security.entity.OurUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
//@EnableRedisRepositories
public interface UserRepository extends JpaRepository<OurUser,Integer> {

    Optional<OurUser> findByEmail(String email);

}
