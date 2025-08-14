package arile.toy.test_data.dto.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record GithubUser(
        String id,
        String name,
        String email
) implements OAuth2User {

    public static GithubUser from(Map<String, Object> attributes) {
        return new GithubUser(
                String.valueOf(attributes.get("login")),
                String.valueOf(attributes.get("name")), // nullable
                String.valueOf(attributes.get("email")) // nullable
        );
    }

    // 뽑을 정보는 위에 있으니, 따로 구현 x
    @Override public Map<String, Object> getAttributes() {return Map.of();}
    // 딱히 필요 x (권한 : admin, user, ...)
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {return List.of();}

    @Override
    public String getName() {
        return name.equals("null") ? id : name; // name이 없다면 id, 있으면 name을 그대로 반환
    }
}
