package com.example.bestme.service.user;

import com.example.bestme.domain.user.Role;
import com.example.bestme.domain.user.User;
import com.example.bestme.dto.user.RequestLoginDTO;
import com.example.bestme.dto.user.RequestSignUpDTO;
import com.example.bestme.exception.ApiResponse;
import com.example.bestme.repository.user.UserRepository;
import com.example.bestme.util.jwt.JwtTokenDTO;
import com.example.bestme.util.jwt.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse<Void>> join(RequestSignUpDTO to) {

        // 중복 아이디 체크
        if (userRepository.findByEmail(to.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT,"중복된 아이디입니다."));
        }

        // 중복 닉네임 체크
        if (userRepository.findByNickname(to.getNickname()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT,"중복된 닉네임입니다."));
        }

        // 체크 끝난 후
        // to -> entity 변경
        ModelMapper modelMapper = new ModelMapper();

        User user = modelMapper.map(to, User.class);

        // 빈 데이터들 입력 및 DB에 저장
        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(to.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setDeletedAt(null);
        user.setDeletedFlag(false);
        user.setKakaoFlag(false);
        user.setRole(Role.USER);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "회원가입에 성공하였습니다.", null));
    }


    @Override
    public ResponseEntity<ApiResponse<JwtTokenDTO>> login(RequestLoginDTO to) {

        // 입력한 이메일을 바탕으로 DB에 존재하는 User 검색
        User user = userRepository.findByEmail(to.getEmail());

        // 입력한 이메일이 존재하지 않으면 error 발생
        // 비밀번호 비교 - 동일하지 않을 시 error 발생
        // (순서 지키기) - (입력한 비밀번호, DB에 존재하는 User 의 암호화된 비밀번호)
        if (user == null || !passwordEncoder.matches(to.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "잘못된 이메일 또는 비밀번호로 인해서 로그인에 실패하였습니다.", null));
        }

        // email + password 기반 Authentication 객체 생성
        // authentication 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(to.getEmail(), to.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtTokenDTO jwtTokenDTO = jwtTokenProvider.generateToken(authentication);

        // ApiResponse 에 토큰 반환
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공하였습니다.", jwtTokenDTO));
    }
}