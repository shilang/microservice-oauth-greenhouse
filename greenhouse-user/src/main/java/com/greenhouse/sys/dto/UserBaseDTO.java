package com.greenhouse.sys.dto;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class UserBaseDTO {

    private String username;

    private String password;

    private boolean enabled = true;

    private String userId;

    private String clientId;

    private Collection<? extends GrantedAuthority> authorities;

    private Collection roles;
}
