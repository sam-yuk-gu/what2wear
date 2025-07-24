package com.samyukgu.what2wear.common.service;

import com.samyukgu.what2wear.wardrobe.dao.WardrobeDAO;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;

import java.util.logging.Logger;

public class SelectOutfitModalService {
    private static final Logger logger = Logger.getLogger(SelectOutfitModalService.class.getName());
    private final WardrobeDAO wardrobeDAO;

    public SelectOutfitModalService(WardrobeDAO wardrobeDAO) {
        this.wardrobeDAO = wardrobeDAO;
    }

}
