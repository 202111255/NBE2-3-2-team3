package com.example.bestme.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class RequestSignUpDTO {
    private String email;
    private String password;
    private String nickname;
}
