package com.wuchaooooo.service.zjutsds.interceptor;

import com.wuchaooooo.service.zjutsds.pojo.po.PUser;
import com.wuchaooooo.service.zjutsds.pojo.vo.VUser;
import com.wuchaooooo.service.zjutsds.pojo.vo.VUserInfo;
import com.wuchaooooo.service.zjutsds.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by wuchaooooo on 07/04/2017.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        UserDetails user = customUserDetailsService.loadUserByUsername(username);
        LOGGER.info("password={}, needPassword={}", password, user.getPassword());
        String s = passwordEncoder().encode("666666");
        VUser vUser = userService.getUser(username, user.getPassword());
        VUserInfo vUserInfo = userService.getUserInfo(username);
        if (vUserInfo != null) {
            vUser.setName(vUserInfo.getName());
        }
        //密码匹配验证
        if (passwordEncoder().matches(password, user.getPassword())) {
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            return new UsernamePasswordAuthenticationToken(vUser, password, authorities);
        }
        throw new BadCredentialsException("Wrong password.");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
