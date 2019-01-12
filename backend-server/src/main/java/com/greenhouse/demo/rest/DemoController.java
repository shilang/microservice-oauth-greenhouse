package com.greenhouse.demo.rest;

import com.greenhouse.demo.annotation.PreAuth;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by keets on 2017/12/6.
 */
@RestController
public class DemoController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
//    @PreAuthorize(value = "hasAuthority('CREATE_COMPANY')") hasRole('Admin')
    @PreAuth("hasAuthority('cargo')")
    public String test() {
        return "ok";
    }

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @PreAuthorize(value = "hasAuthority('SHOW_INFO')")
    public String getUserInfo() {
        return "user-info";
    }

}
