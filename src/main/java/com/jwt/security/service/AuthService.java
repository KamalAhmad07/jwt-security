package com.jwt.security.service;

import com.jwt.security.dto.ReqDTO;
import com.jwt.security.dto.ResDTO;
import com.jwt.security.entity.OurUser;
import com.jwt.security.redisConfig.RedisConfig;
import com.jwt.security.repositroy.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class AuthService {

    @Autowired
    private JWTUitl jwtUitl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private PasswordEncoder passwordEncoder;



    public String saveUser(ReqDTO reqDTO){
        try {
            OurUser user = new OurUser();
            user.setEmail(reqDTO.getEmail());
            user.setRole(reqDTO.getRole());
            user.setPassword(passwordEncoder.encode(reqDTO.getPassword()));

            user = userRepository.save(user);
            if (user.getId() != null && user.getId() != 0) {
                return "User saved SuccessFully!";
            }
        }catch (Exception e){
            System.err.println("error : " +e.getMessage());
            return e.getMessage();
        }
       return null;
    }




}
