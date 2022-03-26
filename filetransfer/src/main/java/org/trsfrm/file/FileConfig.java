package org.trsfrm.file;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
public class FileConfig {
	
	protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .httpBasic().disable()
            .csrf().disable()
            
                .authorizeRequests()
            
                .antMatchers(HttpMethod.GET, "/ap1/v1/**").hasRole("ADMIN")//permitAll()
                .antMatchers(HttpMethod.DELETE, "/vehicles/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/v1/vehicles/**").permitAll();
                //.anyRequest().authenticated();
        //@formatter:on
    }

}
