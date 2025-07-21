package com.samyukgu.what2wear.common.model;

import com.samyukgu.what2wear.codi.model.Clothing;
import com.samyukgu.what2wear.codi.model.Codi;
import lombok.Getter;

import java.util.List;

@Getter
public class SelectionResult {
    private final List<Clothing> outfits;
    private final Codi codi;

    public SelectionResult(List<Clothing> outfits, Codi codi) {
        this.outfits = outfits;
        this.codi = codi;
    }
}