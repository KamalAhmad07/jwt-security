package com.jwt.security.redisConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${REDIS_HOST}")
    private String redisHost;

    @Value("${REDIS_PORT}")
    private int redisPort;

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    private static JedisPool pool;

//    @PostConstruct
//    public void init() {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        pool = new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
//    }


    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        System.err.println("host = "+redisHost + " password = "+redisPassword);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisHost);
        jedisConnectionFactory.setPort(redisPort);
        jedisConnectionFactory.setPassword(redisPassword);
        System.out.println("redis started successfully!");
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15)) // Set default TTL for cache entries
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

// custom key generator
//@Bean("customKeyGenerator")
//public KeyGenerator keyGenerator() {
//    return new KeyGenerator() {
//        @Override
//        public Object generate(Object target, Method method, Object... params) {
//            return method.getName() + "_" + Arrays.toString(params);
//        }
//    };
//}

// local config
//    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);

//    private static JedisPool pool;
//
//    // redis cloud
//    static {
//        String redisHost = "redis-15883.c305.ap-south-1-1.ec2.redns.redis-cloud.com"; // Redis Cloud hostname without port
//        int redisPort = 15883; // Redis Cloud port
//        String redisPassword = "V8Kq0t3ywWUFsY3IlJAPiLtiOs4OIDjk"; // Redis Cloud password
//
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        pool = new JedisPool(poolConfig, redisHost, redisPort,2000, redisPassword);
//    }
//
//    public static JedisPool getPool() {
//        return pool;
//    }

//    @Value("${redis.host}")
//    private String redisHost;
//
//    @Value("${redis.port}")
//    private int redisPort;
//
//    @Value("${redis.password}")
//    private String redisPassword;
//
//    private static JedisPool pool;
//
//    @PostConstruct
//    public void init() {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        pool = new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
//    }
//
//    public static JedisPool getPool() {
//        return pool;
//    }


}
