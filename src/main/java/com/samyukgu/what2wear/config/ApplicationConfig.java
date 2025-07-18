package com.samyukgu.what2wear.config;

import com.samyukgu.what2wear.di.DIContainer;

public class ApplicationConfig {
    public static void configure() {
        DIContainer container = DIContainer.getInstance();
        /*
           사용 예제
           DAO 컨테이너 주입
           container.registerSingleton(DomainDAO.class, new DomainDAOImpl());

           Service 컨테이너 주입
           container.registerSingleton(DomainService.class, new UserService(container.resolve(DomainDAO.class)));
        */

        // DAO

        // Service
    }
}
