package com.jwt.security.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReqDTO {

    private String email;
    private String password;
    private String role;

}
