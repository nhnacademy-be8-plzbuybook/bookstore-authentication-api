package com.nhnacademy.shoppingmallservice.security.details;

import com.nhnacademy.shoppingmallservice.User.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = User.createUser("test", "테스트", "1234");
        return new PrincipalDetails(user);
    }
}
