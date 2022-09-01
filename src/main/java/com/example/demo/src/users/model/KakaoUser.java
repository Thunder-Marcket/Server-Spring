package com.example.demo.src.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUser {
    private Long kakaoId;
    private String profileImgUrl;
    private String userName;
}
