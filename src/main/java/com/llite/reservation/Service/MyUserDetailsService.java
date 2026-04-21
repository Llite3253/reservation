package com.llite.reservation.Service;

import com.llite.reservation.Entity.Member;
import com.llite.reservation.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 username으로 회원 조회
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("없는 회원입니다."));

        // 2. 시큐리티가 이해할 수 있는 UserDetails 객체로 변환해서 반환
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRole())
                .build();
    }
}
