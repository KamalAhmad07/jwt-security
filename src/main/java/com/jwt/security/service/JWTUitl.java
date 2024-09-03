package com.jwt.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.logging.Logger;


@Component
public class JWTUitl {

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration; // In seconds

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration; // In seconds

    @Value("${JWT_SECRET}")
    private String secretKey;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private CacheManager cacheManager;

    @Value("${jwt.access.token.expiration}")
    private int expiryTime; // In seconds -- 900 sec 15 min



    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Map<String, Object> getHeader(){
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        return header;
    }


    public String generateAccessToken(String username){
        return Jwts.builder()
                .setHeader(getHeader())  // header
                .subject(username)  // payload
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration*1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateRefreshToken(String username){
        return Jwts.builder()
                .setHeader(getHeader()) // header
                .subject(username) // payload
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration*1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String refreshAccessToken(String refreshToken) throws Exception {

        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        if(claims.getExpiration().before(new Date()))
            throw  new Exception("Refresh token has been Expired");

        String username = claims.getSubject();
        return generateAccessToken(username);
    }


    public boolean validateToken(String token, UserDetails userDetails){
          Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

          if(!claims.getSubject().equals(userDetails.getUsername())) return false;

          if(claims.getExpiration().before(new Date()))
             return false;
        return true;
    }

    public String extractUserName(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return  claims.getSubject();
    }


    // adding and checking the data in cache.

    @Cacheable(value = "true" ,key = "#token" ,condition = "#token != null", unless = "#result == false")
    public boolean blacklistToken(String token) {
        return  true;
        // The token will be automatically evicted from the cache after the TTL
    }

    public boolean isTokenBlacklisted(String token) {
        System.out.println("token for blacklisting kamal  = "+token);
        Boolean isBlacklisted = getFromCache("true", token);
        if (isBlacklisted != null) {
            System.err.println("Token found in cache!");
            return isBlacklisted;
        } else {
            System.err.println("Token not found in cache!");
            return false;
        }
    }

    private Boolean getFromCache(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                return (Boolean) valueWrapper.get();
            }
        }
        return null;
    }
}
