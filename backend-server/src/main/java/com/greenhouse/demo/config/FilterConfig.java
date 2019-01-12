package com.greenhouse.demo.config;

import com.greenhouse.demo.filter.AuthorizationFilter;
import com.sun.deploy.config.SecuritySettings;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Created by keets on 2017/12/6.
 */
@Configuration
@EnableAutoConfiguration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(authorizationFilter());
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter();
    }

    @EnableWebSecurity
    @EnableConfigurationProperties(SecurityProperties.class)
    public static class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
        // Customized SecuritySettings
        private final SecuritySettings securitySettings;

        // Customized AuthenticationManager
        private final AuthenticationManager authenticationManager;

        // Customized Filter 1
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        // Customized Filter 2
        private final BusinessFilterSecurityInterceptor businessFilterSecurityInterceptor;

        @Autowired
        public WebSecurityConfigurer(SecuritySettings securitySettings, WebUriRoleService uriRoleService,
                                     AuthenticationManager authenticationManager, RoleHierarchy roleHierarchy, CacheManager redis, UserDataCollector udc) {
            super(true);
            this.securitySettings = securitySettings;
            this.authenticationManager = authenticationManager;
            this.jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, securitySettings, redis, udc);
            this.businessFilterSecurityInterceptor = new BusinessFilterSecurityInterceptor(uriRoleService, roleHierarchy);
        }

        @Override
        public AuthenticationManager authenticationManager() throws Exception {
            return authenticationManager;
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            if (ArrayUtils.isNotEmpty(securitySettings.getAllowedOrigins())) {
                http.cors().configurationSource(getCorsSource()); // Cross-Origin Resource Sharing
            }
            http.exceptionHandling().disable(); // Let customized (GlobalExceptionController.java + GlobalExceptionHandler.java) handle it.
            http.anonymous().disable();
            http.rememberMe().disable();
            http.sessionManagement().disable();
            http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class); // Custom JWT based security filter
            //http.authorizeRequests().anyRequest().authenticated(); // default FilterSecurityInterceptor
            http.addFilterAfter(businessFilterSecurityInterceptor, JwtAuthenticationFilter.class); // Custom security interceptor after default
            http.csrf().disable(); // disable csrf
            http.headers().cacheControl(); // enable cache control
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(securitySettings.getIgnorings());
        }

        private CorsConfigurationSource getCorsSource() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setMaxAge(securitySettings.getCorsMaxAgeInSeconds());
            config.setAllowCredentials(securitySettings.getCorsAllowCredentials()); // you USUALLY want this
            config.setAllowedOrigins(Arrays.asList(securitySettings.getAllowedOrigins()));
            config.setAllowedHeaders(Arrays.asList("*"));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
            source.registerCorsConfiguration("/**", config);
            return source;
        }
    }
}
