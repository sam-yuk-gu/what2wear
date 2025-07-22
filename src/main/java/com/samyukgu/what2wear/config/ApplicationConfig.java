package com.samyukgu.what2wear.config;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.mail.service.AuthService;
import com.samyukgu.what2wear.mail.service.MailService;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.dao.MemberDAO;
import com.samyukgu.what2wear.member.dao.MemberOracleDAO;
import com.samyukgu.what2wear.member.service.MemberService;

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

        //UserSession
        container.registerSingleton(MemberSession.class, new MemberSession());
        // DAO
        container.registerSingleton(MemberDAO.class, new MemberOracleDAO());
        // Service
        container.registerSingleton(MemberService.class, new MemberService(container.resolve(MemberDAO.class)));
        container.registerSingleton(MailService.class, new MailService());
        container.registerSingleton(AuthService.class, new AuthService(container.resolve(MailService.class)));
    }
}
