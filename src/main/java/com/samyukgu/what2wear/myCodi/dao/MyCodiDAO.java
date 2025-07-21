package com.samyukgu.what2wear.myCodi.dao;

import com.samyukgu.what2wear.myCodi.model.MyCodi;

import java.util.List;

public interface MyCodiDAO {

    MyCodi findById(Long id, Long memberId);
    List<MyCodi> findAll(Long memberId);
    void save(MyCodi myCodi);
    void delete(Long id, Long memberId);
}
