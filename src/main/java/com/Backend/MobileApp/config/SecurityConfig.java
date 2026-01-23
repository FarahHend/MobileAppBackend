package com.Backend.MobileApp.config;

import com.Backend.MobileApp.security.JwtAuthenticationFilter;
import com.Backend.MobileApp.security.JwtUtil;
import com.Backend.MobileApp.service.CustomOAuth2UserService;
import com.Backend.MobileApp.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          JwtUtil jwtUtil,
                          CustomUserDetailsService customUserDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ✅ Public endpoints
                        .requestMatchers("/auth/**", "/oauth2/**").permitAll()

                        // ✅ EMPLOYEE attendance APIs
                        .requestMatchers(
                                "/api/attendance/check-in",
                                "/api/attendance/check-out",
                                "/api/attendance/today",
                                "/api/attendance/me",
                                "/api/help//employee",
                                "/api/help/employee/**"
                        ).hasRole("EMPLOYEE")

                        // ✅ ADMIN attendance APIs
                        .requestMatchers(
                                "/api/attendance/user/**",
                                "/api/attendance/date/**",
                                "/api/help/admin",
                                "/api/help/admin/**"
                        ).hasRole("ADMIN")

                        // ✅ Other EMPLOYEE APIs
                        .requestMatchers("/api/user/**").hasRole("EMPLOYEE")

                        // ❌ Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // OAuth2 Login
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                // JWT Stateless session
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}
