package arile.toy.test_data.config;

import arile.toy.test_data.dto.security.GithubUser;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers((PathRequest.toStaticResources().atCommonLocations())).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/",
                                "table-schema",
                                "table-schema/export"
                        ).permitAll() // 해당 정보는 허용
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                .oauth2Login(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/")) // 로그아웃 할 때는 root로 돌아가라
                .build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> GithubUser.from(delegate.loadUser(userRequest).getAttributes());
        }
    }

