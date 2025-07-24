package com.samyukgu.what2wear.region.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    Long id;
    String regionParent;
    String regionChild;
    Long nx;
    Long ny;

    @Override
    public String toString() {
        // 콤보박스에 표시될 텍스트 형태
        return regionChild == null || regionChild.isBlank() ? regionParent : regionChild;
    }
}
