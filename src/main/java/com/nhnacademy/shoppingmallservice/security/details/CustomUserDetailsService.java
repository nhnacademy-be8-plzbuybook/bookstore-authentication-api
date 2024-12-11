package com.nhnacademy.shoppingmallservice.security.details;

import com.nhnacademy.shoppingmallservice.entity.Member;
import com.nhnacademy.shoppingmallservice.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member userData = memberRepository.findByUsername(username);

        if (userData.getPassword() == null) {
            throw new IllegalArgumentException("비밀번호 null : " + username);
        }

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}
