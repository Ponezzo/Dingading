package com.mickey.dinggading.domain.oauth.provider;

import java.util.Map;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode()
public class GoogleOAuth2UserInfo {

    public Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String code) {
        return (String) attributes.get(code);
    }

    public String getName() {
        return getAttribute("name");
    }

    public String getEmail() {
        return getAttribute("email");
    }

    public String getImageUrl() {
        return getAttribute("picture");
    }
}
