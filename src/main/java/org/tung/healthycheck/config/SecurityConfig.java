package org.tung.healthycheck.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    //Khi bạn thêm dependency spring-boot-starter-security thì
    //Tự động kích hoạt bảo mật mặc định
    //Tạo một tài khoản mặc định: Username: user, Password: là chuỗi ngẫu nhiên như 01149ffb-5763-48e8-870f-3353d415bf09
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Spring Security sẽ duyệt tuần tự từng rule trong .authorizeHttpRequests()
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF cho REST API
                .authorizeHttpRequests(auth -> auth
                        // Cho phép /auth/** truy cập tự do (đăng ký, đăng nhập)
                        .requestMatchers("/auth/**").permitAll()

                        // Yêu cầu đăng nhập cho tất cả endpoint /products/**
                        .requestMatchers("/products/**", "/accounts/**","/family-members/**").authenticated()

                        //Các request khác (không phải /auth/**, cũng không phải /products/**)
                        // thì cho phép tự do
                        .anyRequest().permitAll()
                )
                // Xác thực bằng JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
