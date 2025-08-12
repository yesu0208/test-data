package arile.toy.test_data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 인증하지 않는다(permitAll)
                .logout(logout -> logout.logoutSuccessUrl("/")) // 로그아웃 할 때는 root로 돌아가라
                .build();
    }
}
