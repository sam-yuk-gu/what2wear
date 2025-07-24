package com.samyukgu.what2wear.region.dao;

import com.samyukgu.what2wear.region.model.Region;

import java.util.List;

public interface RegionDAO {
    void saveAll(List<Region> regions);
}
