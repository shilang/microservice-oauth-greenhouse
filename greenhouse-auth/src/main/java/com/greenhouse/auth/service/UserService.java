package com.greenhouse.auth.service;

import com.google.common.collect.Lists;
import com.greenhouse.auth.dto.UserBaseDTO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    public UserBaseDTO getUserInfoByNameAndPassword(String username, String password) {
        //todo ,get information from db
        UserBaseDTO userBaseDTO = new UserBaseDTO();
        userBaseDTO.setUsername("liushilang");
        userBaseDTO.setUserId("1000");
        userBaseDTO.setClientId("frontend");
        userBaseDTO.setEnabled(true);
        userBaseDTO.setPassword("123456");
        SimpleGrantedAuthority admin = new SimpleGrantedAuthority("admin");
        SimpleGrantedAuthority cargo = new SimpleGrantedAuthority("cargo");
        SimpleGrantedAuthority driver = new SimpleGrantedAuthority("dirver");
        userBaseDTO.setAuthorities(Lists.newArrayList(admin, cargo, driver));
        userBaseDTO.setRoles(Lists.newArrayList("manager", "car-manager", "oper"));
        return userBaseDTO;
    }
}
