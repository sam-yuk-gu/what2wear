package com.samyukgu.what2wear.myCodi.service;

import com.samyukgu.what2wear.myCodi.dao.MyCodiDAO;
import com.samyukgu.what2wear.myCodi.model.MyCodi;

import java.util.List;

public class MyCodiService {
    private final MyCodiDAO myCodiDao;

    public MyCodiService(MyCodiDAO myCodiDao) {
        this.myCodiDao = myCodiDao;
    }

    // 사용자의 코디 하나 조회
    public MyCodi getCodiById(Long id, Long memberId) {
        return myCodiDao.findById(id, memberId);
    }

    // 사용자의 모든 코디 조회
    public List<MyCodi> getAllCodi(Long memberId) {
        return myCodiDao.findAll(memberId);
    }

    // 코디 저장
    public void saveCodi(MyCodi myCodi) {
        myCodiDao.save(myCodi);
    }

    // 코디 삭제
    public void deleteCodi(Long id, Long memberId) {
        myCodiDao.delete(id, memberId);
    }

}
