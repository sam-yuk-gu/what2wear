package com.samyukgu.what2wear.notification.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
// 작성자 : 백승준
public class Notification {
    Long id;
    Long receiverId;
    Long senderId;
}
