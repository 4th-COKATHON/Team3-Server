package org.main.hackerthon.api.oauth2;

import lombok.RequiredArgsConstructor;
import org.main.hackerthon.api.dto.LoginRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final LoginRequest loginRequest;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return loginRequest.getRole();
            }
        });
        return collection;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    public String getUniqueId(){
        return loginRequest.getUniqueId();
    }

    @Override
    public String getName() {
        return loginRequest.getName();
    }
}
