package com.llite.reservation.Controller;

import com.llite.reservation.Dto.Reqeust.JoinRequest;
import com.llite.reservation.Dto.Reqeust.LoginRequest;
import com.llite.reservation.Jwt.JwtToken;
import com.llite.reservation.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody LoginRequest request) {
        JwtToken jwtToken = memberService.login(request);
        return ResponseEntity.ok().body(jwtToken);
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody JoinRequest request) {
        memberService.join(request);
        return ResponseEntity.ok().build();
    }
}
