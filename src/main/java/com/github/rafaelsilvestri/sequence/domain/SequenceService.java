package com.github.rafaelsilvestri.sequence.domain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@AllArgsConstructor
@Service
public class SequenceService {
    private static final String DEFAULT_ERROR_MESSAGE = "Duplicate entry {} for type {}";

    final SequenceRepository repository;
    final StringRedisTemplate redisTemplate;
    final MemcachedClient memcachedClient;

    @Transactional
    public void createWithDb(String type) {
        var code = repository.findNextCode(type);
        var sequence = Sequence.builder().code(code)
                .type(type)
                .build();
        try {
            repository.save(sequence);
        } catch (DbActionExecutionException ex) {
            if (ex.getCause() instanceof DuplicateKeyException) {
                log.error(DEFAULT_ERROR_MESSAGE, code, type);
            }
            throw ex;
        }
    }

    @Transactional
    public void createWithRedis(String type) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(type))) {
            var strCode = String.valueOf(repository.findNextCode(type));
            redisTemplate.opsForValue().set(type, strCode, Duration.ofMinutes(5));
        }

        var code = redisTemplate.opsForValue().increment(type);
        var sequence = Sequence.builder()
                .code(code)
                .type(type)
                .build();
        try {
            repository.save(sequence);
        } catch (DbActionExecutionException ex) {
            if (ex.getCause() instanceof DuplicateKeyException) {
                log.error(DEFAULT_ERROR_MESSAGE, code, type);
                // if conflicts, delete the key to refresh
                redisTemplate.delete(type);
            }
            throw ex;
        }
    }

    @Transactional
    public void createWithMemcached(String type) {
        if (memcachedClient.get(type) == null) {
            // incr only works if the  value to increment is stored as string
            var strCode = String.valueOf(repository.findNextCode(type));
            memcachedClient.add(type, 60 * 5, strCode);
        }

        var code = memcachedClient.incr(type, 1);
        var sequence = Sequence.builder()
                .code(code)
                .type(type)
                .build();
        try {
            repository.save(sequence);
        } catch (DbActionExecutionException ex) {
            if (ex.getCause() instanceof DuplicateKeyException) {
                log.error(DEFAULT_ERROR_MESSAGE, code, type);
                // if conflicts, delete the key to refresh
                memcachedClient.delete(type);
            }
            throw ex;
        }
    }

}
