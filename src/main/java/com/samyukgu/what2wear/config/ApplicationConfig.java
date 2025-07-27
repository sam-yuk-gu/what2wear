package com.samyukgu.what2wear.config;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.friend.dao.FriendDAO;
import com.samyukgu.what2wear.friend.dao.FriendOracleDAO;
import com.samyukgu.what2wear.friend.service.FriendService;
import com.samyukgu.what2wear.likePost.dao.LikePostDAO;
import com.samyukgu.what2wear.likePost.dao.LikePostOracleDAO;
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
import com.samyukgu.what2wear.region.service.RegionService;
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
import com.samyukgu.what2wear.region.Session.RegionWeatherSession;
import com.samyukgu.what2wear.region.dao.RegionDAO;
import com.samyukgu.what2wear.region.dao.RegionOracleDAO;
import com.samyukgu.what2wear.weather.service.WeatherService;

// 공동 작성자: 백승준
public class ApplicationConfig {
    public static void configure() {
        DIContainer container = DIContainer.getInstance();

        // Session
        container.registerSingleton(MemberSession.class, new MemberSession());
        container.registerSingleton(RegionWeatherSession.class, new RegionWeatherSession());

        // DAO
        container.registerSingleton(MemberDAO.class, new MemberOracleDAO());
        container.registerSingleton(FriendDAO.class, new FriendOracleDAO());
        container.registerSingleton(com.samyukgu.what2wear.codi.dao.CodiDAO.class,
                new com.samyukgu.what2wear.codi.dao.CodiOracleDAO());
        container.registerSingleton(PostDAO.class, new PostOracleDAO());
        container.registerSingleton(LikePostDAO.class, new LikePostOracleDAO());
        container.registerSingleton(PostCommentDAO.class, new PostCommentOracleDAO());
        container.registerSingleton(NotificationDAO.class, new NotificationOracleDAO());
        container.registerSingleton(RegionDAO.class, new RegionOracleDAO());
        container.registerSingleton(WardrobeDAO.class, new WardrobeOracleDAO());
        container.registerSingleton(CategoryDAO.class, new CategoryOracleDAO());
        container.registerSingleton(com.samyukgu.what2wear.myCodi.dao.CodiDAO.class,
                new com.samyukgu.what2wear.myCodi.dao.CodiOracleDAO());
        container.registerSingleton(CodiDetailDAO.class, new CodiDetailOracleDAO());

        // Service
        container.registerSingleton(MemberService.class,
                new MemberService(container.resolve(MemberDAO.class)));
        container.registerSingleton(PostService.class,
                new PostService(
                        container.resolve(PostDAO.class),
                        container.resolve(LikePostDAO.class)
                ));
        container.registerSingleton(com.samyukgu.what2wear.codi.service.CodiService.class,
                new com.samyukgu.what2wear.codi.service.CodiService(
                        container.resolve(com.samyukgu.what2wear.codi.dao.CodiDAO.class)
                ));
        container.registerSingleton(NotificationService.class,
                new NotificationService(
                        container.resolve(FriendDAO.class),
                        container.resolve(NotificationDAO.class),
                        container.resolve(MemberDAO.class)
                ));
        container.registerSingleton(WeatherService.class, new WeatherService());
        container.registerSingleton(RegionService.class, new RegionService(container.resolve(RegionDAO.class)));
        container.registerSingleton(WardrobeService.class, new WardrobeService(container.resolve(WardrobeDAO.class)));
        container.registerSingleton(CategoryService.class, new CategoryService(container.resolve(CategoryDAO.class)));
        container.registerSingleton(com.samyukgu.what2wear.myCodi.service.CodiService.class,
                new com.samyukgu.what2wear.myCodi.service.CodiService(
                        container.resolve(com.samyukgu.what2wear.myCodi.dao.CodiDAO.class),
                        container.resolve(CodiDetailDAO.class)
                ));
        container.registerSingleton(MailService.class, new MailService());
        container.registerSingleton(AuthService.class, new AuthService(container.resolve(MailService.class)));
        container.registerSingleton(FriendService.class, new FriendService(
                container.resolve(MemberDAO.class),
                container.resolve(FriendDAO.class),
                container.resolve(NotificationDAO.class)
        ));
    }
}