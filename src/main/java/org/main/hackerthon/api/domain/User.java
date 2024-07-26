package org.main.hackerthon.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id")
    private String uniqueId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String unqueId, String name, String email, Role role) {
        this.uniqueId = unqueId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public User update(String email, String name){
        this.email = email;
        this.name = name;
        return this;
    }

    public String getRoleKey(){
        return this.role.getName();
    }


}
