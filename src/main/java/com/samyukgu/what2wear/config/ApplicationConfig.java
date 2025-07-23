package com.samyukgu.what2wear.config;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.friend.dao.FriendDAO;
import com.samyukgu.what2wear.friend.dao.FriendOracleDAO;
import com.samyukgu.what2wear.friend.service.FriendService;
import com.samyukgu.what2wear.mail.service.AuthService;
import com.samyukgu.what2wear.mail.service.MailService;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.dao.MemberDAO;
import com.samyukgu.what2wear.member.dao.MemberOracleDAO;
import com.samyukgu.what2wear.member.service.MemberService;
import com.samyukgu.what2wear.myCodi.dao.MyCodiDAO;
import com.samyukgu.what2wear.myCodi.dao.MyCodiOracleDAO;
import com.samyukgu.what2wear.myCodi.service.MyCodiService;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeDAO;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeOracleDAO;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import com.samyukgu.what2wear.post.dao.PostDAO;
import com.samyukgu.what2wear.post.dao.PostOracleDAO;
import com.samyukgu.what2wear.post.service.PostService;
import com.samyukgu.what2wear.postcomment.dao.PostCommentDAO;
import com.samyukgu.what2wear.postcomment.dao.PostCommentOracleDAO;
import com.samyukgu.what2wear.postcomment.model.PostComment;
import com.samyukgu.what2wear.postcomment.service.PostCommentService;

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

        // MemberSession
        container.registerSingleton(MemberSession.class, new MemberSession());

        // DAO
        container.registerSingleton(MemberDAO.class, new MemberOracleDAO());
        container.registerSingleton(FriendDAO.class, new FriendOracleDAO());
        container.registerSingleton(PostDAO.class, new PostOracleDAO());
        container.registerSingleton(PostCommentDAO.class, new PostCommentOracleDAO());

        // Service
        container.registerSingleton(MemberService.class, new MemberService(container.resolve(MemberDAO.class)));
        container.registerSingleton(PostService.class, new PostService(container.resolve(PostDAO.class)));

        // wardrobe
        container.registerSingleton(WardrobeDAO.class, new WardrobeOracleDAO());
        container.registerSingleton(WardrobeService.class, new WardrobeService(container.resolve(WardrobeDAO.class)));

        // myCodi
        container.registerSingleton(MyCodiDAO.class, new MyCodiOracleDAO());
        container.registerSingleton(MyCodiService.class, new MyCodiService(container.resolve(MyCodiDAO.class)));

        container.registerSingleton(MailService.class, new MailService());
        container.registerSingleton(AuthService.class, new AuthService(container.resolve(MailService.class)));
        container.registerSingleton(FriendService.class, new FriendService(container.resolve(MemberDAO.class), container.resolve(FriendDAO.class)));
    }
}
