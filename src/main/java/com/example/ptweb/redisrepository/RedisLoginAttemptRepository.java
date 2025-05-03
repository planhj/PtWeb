package com.example.ptweb.redisrepository;

import com.example.ptweb.other.RedisLoginAttempt;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisLoginAttemptRepository extends CrudRepository<RedisLoginAttempt, String> {
    Optional<RedisLoginAttempt> findByIp(@NotNull String ip);
    void deleteByIp(@NotNull String ip);
}
