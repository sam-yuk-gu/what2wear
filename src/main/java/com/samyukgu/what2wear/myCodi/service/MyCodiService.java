package com.samyukgu.what2wear.myCodi.service;


import com.samyukgu.what2wear.myCodi.dao.CodiDAO;
import com.samyukgu.what2wear.myCodi.model.Codi;

import java.util.List;

public class MyCodiService {
    private final CodiDAO myCodiDao;

    public MyCodiService(CodiDAO myCodiDao) {
        this.myCodiDao = myCodiDao;
    }

    // 사용자의 코디 하나 조회
    public Codi getCodiById(Long id, Long memberId) {
        return myCodiDao.findById(id, memberId);
    }

    // 사용자의 모든 코디 조회
    public List<Codi> getAllCodi(Long memberId) {
        return myCodiDao.findAllByMemberId(memberId);
    }

    // 코디 저장
    public void saveCodi(Codi myCodi) {
        myCodiDao.save(myCodi);
    }

    // 코디 삭제
    public void deleteCodi(Long id, Long memberId) {
        myCodiDao.delete(id, memberId);
    }

}
