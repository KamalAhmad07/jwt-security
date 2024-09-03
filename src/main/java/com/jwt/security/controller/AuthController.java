package com.jwt.security.controller;

import com.jwt.security.dto.ReqDTO;
import com.jwt.security.dto.ResDTO;
import com.jwt.security.service.AuthService;
import com.jwt.security.service.JWTUitl;
import com.jwt.security.service.OurUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class AuthController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OurUserDetailsService userDetailsService;

    @Autowired
    private JWTUitl jwtUtil;

    @Autowired
    private AuthService userService;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody ReqDTO signUpRequest) {
        String res =  userService.saveUser(signUpRequest);

          return ResponseEntity.ok(res);

    }

    @PostMapping("/signIn")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody ReqDTO authRequest) throws Exception {

         // 1. authenticate
         // 2. loadUser
         // 3. generate AccessToken
         // 4. generate refreshToken

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        ResDTO res = new ResDTO();
        res.setAccessToken(accessToken);
        res.setRefreshToken(refreshToken);
        res.setNewAccessToken(null);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) throws Exception {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);

        ResDTO res = new ResDTO();
        res.setAccessToken(null);
        res.setRefreshToken(refreshToken);
        res.setNewAccessToken(newAccessToken);

        return ResponseEntity.ok(res);
    }


    @PostMapping("/signOut")
    public ResponseEntity<?> signOut(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            System.out.println("tooken for blackList = "+token);
            boolean blackListToken =  jwtUtil.blacklistToken(token);
            System.err.println("isTokenBlackList  = " +blackListToken);
            return ResponseEntity.status(200).body("user logged out successfully");

        } else {
            return ResponseEntity.status(400).body("No token provided");
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public String demo() {
        return "Data access successfully!";
    }

}



