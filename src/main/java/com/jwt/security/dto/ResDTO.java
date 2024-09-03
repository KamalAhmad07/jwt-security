package com.jwt.security.dto;

import com.jwt.security.entity.OurUser;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResDTO {
    private String accessToken;
    private String refreshToken;
    private  String newAccessToken;
}
