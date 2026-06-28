//package com.salonbooking.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class JacksonConfig {
//
//    @Bean
//    public Hibernate6Module hibernate6Module() {
//        Hibernate6Module module = new Hibernate6Module();
//        // Ensure Jackson doesn't trigger lazy-loading of proxy relations during serialization,
//        // preventing LazyInitializationException or unnecessary extra database queries.
//        module.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
//        return module;
//    }
//}
