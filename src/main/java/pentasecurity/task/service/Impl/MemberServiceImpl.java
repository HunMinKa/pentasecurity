package pentasecurity.task.service.Impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pentasecurity.task.dto.RegistrationDto;
import pentasecurity.task.entity.Member;
import pentasecurity.task.exception.MemberAlreadyExistException;
import pentasecurity.task.repository.MemberRepository;
import pentasecurity.task.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createMember(@Valid RegistrationDto memberDto) {
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new MemberAlreadyExistException("There is an account with that email address: " + memberDto.getEmail());
        }
        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build();

        memberRepository.save(member);
    }
}
