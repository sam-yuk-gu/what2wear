package com.samyukgu.what2wear.common.model;

import com.samyukgu.what2wear.codi.model.Clothing;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import lombok.Getter;

import java.util.List;

@Getter
public class SelectionResult {
    private final List<Wardrobe> outfits;
    private final Codi codi;

    public SelectionResult(List<Wardrobe> outfits, Codi codi) {
        this.outfits = outfits;
        this.codi = codi;
    }
}