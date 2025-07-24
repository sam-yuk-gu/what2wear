package com.samyukgu.what2wear.config;

import com.samyukgu.what2wear.codi.dao.CodiDAO;
import com.samyukgu.what2wear.codi.dao.CodiOracleDAO;
import com.samyukgu.what2wear.codi.service.CodiService;
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
import com.samyukgu.what2wear.myCodi.dao.CodiDetailDAO;
import com.samyukgu.what2wear.myCodi.dao.CodiDetailOracleDAO;
import com.samyukgu.what2wear.notification.dao.NotificationDAO;
import com.samyukgu.what2wear.notification.dao.NotificationOracleDAO;
import com.samyukgu.what2wear.notification.service.NotificationService;
import com.samyukgu.what2wear.wardrobe.dao.CategoryDAO;
import com.samyukgu.what2wear.wardrobe.dao.CategoryOracleDAO;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeDAO;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeOracleDAO;
import com.samyukgu.what2wear.wardrobe.service.CategoryService;
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
        container.registerSingleton(CodiDAO.class, new CodiOracleDAO());
        container.registerSingleton(PostDAO.class, new PostOracleDAO());
        container.registerSingleton(PostCommentDAO.class, new PostCommentOracleDAO());
        container.registerSingleton(NotificationDAO.class, new NotificationOracleDAO());

        // Service
        container.registerSingleton(MemberService.class, new MemberService(container.resolve(MemberDAO.class)));
        container.registerSingleton(PostService.class, new PostService(container.resolve(PostDAO.class)));
        container.registerSingleton(CodiService.class, new CodiService(container.resolve(CodiDAO.class)));
        container.registerSingleton(NotificationService.class,
                new NotificationService(
                        container.resolve(FriendDAO.class),
                        container.resolve(NotificationDAO.class),
                        container.resolve(MemberDAO.class))
        );

        // wardrobe
        container.registerSingleton(WardrobeDAO.class, new WardrobeOracleDAO());
        container.registerSingleton(WardrobeService.class, new WardrobeService(container.resolve(WardrobeDAO.class)));

        // Codi
        container.registerSingleton(CodiDAO.class, new CodiOracleDAO());
        container.registerSingleton(CodiService.class, new CodiService(container.resolve(CodiDAO.class)));

        // mycodi
        container.registerSingleton(com.samyukgu.what2wear.myCodi.dao.CodiDAO.class, new com.samyukgu.what2wear.myCodi.dao.CodiOracleDAO());
        container.registerSingleton(CodiDetailDAO.class, new CodiDetailOracleDAO());
        container.registerSingleton(com.samyukgu.what2wear.myCodi.service.CodiService.class, new com.samyukgu.what2wear.myCodi.service.CodiService(container.resolve(com.samyukgu.what2wear.myCodi.dao.CodiDAO.class), container.resolve(CodiDetailDAO.class)));

        // Category
        container.registerSingleton(CategoryDAO.class, new CategoryOracleDAO());
        container.registerSingleton(CategoryService.class, new CategoryService(container.resolve(CategoryDAO.class)));

        container.registerSingleton(MailService.class, new MailService());
        container.registerSingleton(AuthService.class, new AuthService(container.resolve(MailService.class)));
        container.registerSingleton(FriendService.class, new FriendService(
                container.resolve(MemberDAO.class),
                container.resolve(FriendDAO.class),
                container.resolve(NotificationDAO.class)
        ));
    }
}