package com.llite.reservation;

import com.llite.reservation.Entity.Member;
import com.llite.reservation.Repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@SpringBootApplication
public class ReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner initData(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            if(memberRepository.findByUsername("admin").isEmpty()) {
//                memberRepository.save(Member.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("1234"))
//                        .role("USER")
//                        .build());
//            }
//        };
//    }
}
