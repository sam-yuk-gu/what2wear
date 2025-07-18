package com.samyukgu.di;

import java.util.concurrent.ConcurrentHashMap;

public class DIContainer {
    // 컨테이너 객체 반환
    private static volatile DIContainer instance;
    // 싱글톤으로 관리되는 인스턴스 관리
    private final ConcurrentHashMap<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    // DIContainer 싱글톤 구조
    private DIContainer(){}

    public static DIContainer getInstance() {
        if (instance == null) {
            synchronized (DIContainer.class) {
                if (instance == null) {
                    instance = new DIContainer();
                }
            }
        }
        return instance;
    }

    // 매개변수로 받은 인스턴스 컨테이너에 저장
    public <T> void registerSingleton(Class<T> type, T instance) {
        singletons.put(type, instance);
    }

    // 싱글톤 객체 리턴
    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        if (singletons.containsKey(type)) {
            return (T) singletons.get(type);
        }

        throw new RuntimeException("No registration found for type: " + type.getName());
    }


    public void clear() {
        singletons.clear();
    }
}
