package com.greenhouse.auth.security;

import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.greenhouse.auth.client.feign.UserClient;
import com.greenhouse.auth.dto.UserBaseDTO;
import com.greenhouse.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author keets
 * @date 2017/8/5
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password;
        Map data = (Map) authentication.getDetails();
        String clientId = (String) data.get("client");
        Assert.hasText(clientId, "clientId must have value");
        String type = (String) data.get("type");


        password = (String) authentication.getCredentials();
        //如果你是调用user服务，这边不用注掉
        //map = userClient.checkUsernameAndPassword(getUserServicePostObject(username, password, type));
//        map = checkUsernameAndPassword(getUserServicePostObject(username, password, type));
        UserBaseDTO userInfo = userService.getUserInfoByNameAndPassword(username, password);

//        String userId = (String) map.get("userId");
//        if (StringUtils.isBlank(userId)) {
//            String errorCode = (String) map.get("code");
//            throw new BadCredentialsException(errorCode);
//        }
        CustomUserDetails customUserDetails = buildCustomUserDetails( userInfo, clientId);
        return new CustomAuthenticationToken(customUserDetails);
    }

    private CustomUserDetails buildCustomUserDetails(UserBaseDTO userInfo, String clientId) {
        CustomUserDetails customUserDetails = new CustomUserDetails.CustomUserDetailsBuilder()
                .withUserId(userInfo.getUserId())
                .withPassword(userInfo.getPassword())
                .withUsername(userInfo.getUsername())
                .withClientId(clientId)
                .withRoles(userInfo.getRoles())
                .withAuthorities(userInfo.getAuthorities())
                .build();
        return customUserDetails;
    }

    private Map<String, String> getUserServicePostObject(String username, String password, String type) {
        Map<String, String> requestParam = new HashMap<String, String>();
        requestParam.put("userName", username);
        requestParam.put("password", password);
        if (type != null && StringUtils.isNotBlank(type)) {
            requestParam.put("type", type);
        }
        return requestParam;
    }

    //模拟调用user服务的方法
    private Map checkUsernameAndPassword(Map map) {

        //checkUsernameAndPassword
        Map ret = new HashMap();
        ret.put("userId", UUID.randomUUID().toString());

        return ret;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}