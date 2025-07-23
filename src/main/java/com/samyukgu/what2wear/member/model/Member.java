package com.samyukgu.what2wear.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    Long id;
    String account_id;
    String email;
    String name;
    String nickname;
    String password;
    byte[] profile_img;
    String deleted;
    Long count;
}
