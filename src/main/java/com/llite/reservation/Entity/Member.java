package com.llite.reservation.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = false)
    private String password;

    @Column(unique = false)
    private String role;

    private String nickname;
    private String provider;
    private String providerId;

    @Builder
    public Member(String username, String password, String nickname, String role, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
    }

    public Object update(String name) {
        if(this.getNickname().equals(name)) {
            return this.getNickname();
        }
        return null;
    }
}
