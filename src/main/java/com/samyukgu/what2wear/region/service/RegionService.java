package com.samyukgu.what2wear.region.service;

import com.samyukgu.what2wear.common.util.RegionLoader;
import com.samyukgu.what2wear.region.dao.RegionDAO;
import com.samyukgu.what2wear.region.model.Region;

import java.util.List;

public class RegionService {
    private final RegionDAO dao;

    public RegionService(RegionDAO dao) {
        this.dao = dao;
    }

    public void importRegionDataFromCsv() {
//        dao.saveAll(regions);
    }
}