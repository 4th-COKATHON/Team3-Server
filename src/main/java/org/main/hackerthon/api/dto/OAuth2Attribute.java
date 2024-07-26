package org.main.hackerthon.api.dto;

import lombok.Builder;
import lombok.Getter;
import org.main.hackerthon.api.domain.Role;
import org.main.hackerthon.api.domain.User;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attribute {

    private Map<String, Object> attributes;
    private String name;
    private String email;
    private String providerId;


    public static OAuth2Attribute of(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equals("google")){
            return ofGoogle(attributes);
        }
        return null;
    }

    private static OAuth2Attribute ofGoogle(Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .name(attributes.get("name").toString())
                .email(attributes.get("email").toString())
                .providerId(attributes.get("sub").toString())
                .attributes(attributes)
                .build();
    }

    public User toEntity(String uniqueId){
        return User.builder()
                .name(name)
                .email(email)
                .unqueId(uniqueId)
                .role(Role.USER)
                .build();
    }

}
