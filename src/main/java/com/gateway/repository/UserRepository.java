package com.gateway.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.gateway.bean.User;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    @Query(value="{'email' : ?0}")
    User findByEmail(String email);
}
