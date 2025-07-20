package com.samyukgu.what2wear.member.common;

public class MemberConstants {
    public static final int ID_MIN_LENGTH = 5;
    public static final int ID_MAX_LENGTH = 20;

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 16;

    public static final String ID_PATTERN = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,20}$";;
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$";

    public static final String ID_CONSTANT = "아이디는 영문자를 포함하여 " + MemberConstants.ID_MIN_LENGTH + "자 이상 " + MemberConstants.ID_MAX_LENGTH + "자 이하입니다.";
    public static final String PW_CONSTANT = "비밀번호는 영문자와 숫자를 포함하고 길이가 " + PASSWORD_MIN_LENGTH + "~" + PASSWORD_MAX_LENGTH + "사이여야 합니다.";
}
