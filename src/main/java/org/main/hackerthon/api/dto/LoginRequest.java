package org.main.hackerthon.api.dto;

import lombok.Builder;
import lombok.Getter;
import org.main.hackerthon.api.domain.User;

@Getter
public class LoginRequest {

    private String uniqueId;
    private String name;
    private String role;

    public LoginRequest(User user){
        this.uniqueId = user.getUniqueId();
        this.name = user.getName();
        this.role = user.getRoleKey();
    }

    @Builder
    public LoginRequest(String uniqueId, String role){
        this.uniqueId = uniqueId;
        this.role = role;
    }
}
