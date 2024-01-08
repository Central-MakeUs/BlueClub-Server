package blueclub.server.auth.domain;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId(); // 소셜 식별 값
    public abstract String getEmail();
    public abstract String getNickname();
    public abstract String getImageUrl();
}
