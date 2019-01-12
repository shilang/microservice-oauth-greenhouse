package com.greenhouse.demo.filter;

import com.greenhouse.demo.client.FeignAuthClient;
import com.greenhouse.demo.entity.Permission;
import com.greenhouse.demo.security.CustomAuthentication;
import com.greenhouse.demo.security.SimpleGrantedAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author keets
 */
public class AuthorizationFilter implements Filter {

    @Autowired
    private FeignAuthClient feignAuthClient;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("初始化过滤器。");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("过滤器正在执行...");
        // pass the request along the filter chain
//        String userId = ((HttpServletRequest) servletRequest).getHeader(SecurityConstants.USER_ID_IN_HEADER);

//        if (StringUtils.isNotEmpty(userId)) {
//            UserContext userContext = new UserContext(UUID.fromString(userId));
//            userContext.setAccessType(AccessType.ACCESS_TYPE_NORMAL);
//
//            List<Permission> permissionList = feignAuthClient.getUserPermissions(userId);
//            List<SimpleGrantedAuthority> authorityList = new ArrayList();
//            for (Permission permission : permissionList) {
//                SimpleGrantedAuthority authority = new SimpleGrantedAuthority();
//                authority.setAuthority(permission.getPermission());
//                authorityList.add(authority);
//            }
//
//            CustomAuthentication userAuth  = new CustomAuthentication();
//            userAuth.setAuthorities(authorityList);
//            userContext.setAuthorities(authorityList);
//            userContext.setAuthentication(userAuth);
//            SecurityContextHolder.setContext(userContext);
//        }
        String authorization = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if (StringUtils.isNotEmpty(authorization) && isJwtBearerToken(authorization)) {
            //decode jwt authorization
//            DecodedJWT
//
            String token = authorization.substring(7);
            String sercet = java.util.Base64.getEncoder().encodeToString("secret".getBytes("UTF-8"));
            Claims claims = Jwts.parser().setSigningKey(sercet).parseClaimsJws(token).getBody();
            System.out.println(claims);
            UserContext userContext = new UserContext(UUID.randomUUID());
            List permissionList = (List<Map>)claims.get("authorities");

            List<SimpleGrantedAuthority> authorityList = new ArrayList();

            for (Object permission : permissionList) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority();
                authority.setAuthority(((Map)permission).get("authority").toString());
                authorityList.add(authority);
            }
            CustomAuthentication userAuth  = new CustomAuthentication();
            userAuth.setAuthorities(authorityList);
            userContext.setAuthorities(authorityList);
            userContext.setAuthentication(userAuth);
            userContext.setUserId(claims.get("userId").toString());
            SecurityContextHolder.setContext(userContext);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private boolean isJwtBearerToken(String token) {
        return StringUtils.countMatches(token, ".") == 2 && (token.startsWith("Bearer") || token.startsWith("bearer"));
    }
}
