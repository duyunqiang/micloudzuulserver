package com.mindsignited;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
//import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;

/**
 *
 */
@SpringBootApplication
@EnableZuulProxy
//@EnableOAuth2Sso
/** to make an api gateway we use the below instead of @EnableOauth2Sso **/
//@EnableOAuth2Resource
public class ZuulServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
    }

}
