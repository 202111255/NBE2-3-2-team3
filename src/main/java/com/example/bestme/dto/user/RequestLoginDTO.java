package com.example.bestme.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestLoginDTO {
    private String email;
    private String password;

}
