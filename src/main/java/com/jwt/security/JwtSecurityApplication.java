package com.jwt.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
//import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableCaching
public class JwtSecurityApplication {

	public static void main(String[] args) {

////		// Create a Jedis connection pool
//		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
//
//		// Get the pool and use the database
//		try (Jedis jedis = jedisPool.getResource()) {
//
//			jedis.set("mykey", "Hello from Jedis");
//
//			String value = jedis.get("mykey");
//			System.err.println( "value = "+value );
//		}
//
//		// close the connection pool
//		jedisPool.close();

//		Dotenv dotenv = Dotenv.load();
//		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		SpringApplication.run(JwtSecurityApplication.class, args);
		System.err.println("JWT Application Started!!");
	}

}
