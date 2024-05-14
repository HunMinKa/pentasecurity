package pentasecurity.task.service.Impl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.BDDMockito.*;
import pentasecurity.task.dto.RegistrationDto;
import pentasecurity.task.exception.MemberAlreadyExistException;
import pentasecurity.task.repository.MemberRepository;

import java.util.Set;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {

    private Validator validator;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    private RegistrationDto validRegistrationDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validRegistrationDto = new RegistrationDto();
        validRegistrationDto.setEmail("test@example.com");
        validRegistrationDto.setPassword("Password123!");
        validRegistrationDto.setConfirmPassword("Password123!");
    }

    @Test
    void email_Format_Error_Test() {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setEmail("invalidemail"); // 잘못된 이메일 형식
        registrationDto.setPassword("Password1!");
        registrationDto.setConfirmPassword("Password1!");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(registrationDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("유효한 이메일 주소를 입력해주세요"));
    }

    @Test
    void password_Complexity_Error_Test() {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("user@example.com");
        dto.setPassword("pass"); // 복잡성 요구사항을 충족하지 않음
        dto.setConfirmPassword("pass");

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("비밀번호는 최소 4자 이상이며, 대문자, 소문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다."));
    }

    @Test
    void createMember_WhenUserDoesNotExist_ShouldCreateUser() {
        given(memberRepository.existsByEmail(anyString())).willReturn(false);

        memberService.createMember(validRegistrationDto);

        then(memberRepository).should(times(1)).save(any());
    }

    @Test
    void createMember_WhenUserAlreadyExists_ShouldThrowException() {
        given(memberRepository.existsByEmail(anyString())).willReturn(true);

        assertThatThrownBy(() -> memberService.createMember(validRegistrationDto))
                .isInstanceOf(MemberAlreadyExistException.class);
    }

    @Test
    void createMember_ShouldEncodePassword() {
        given(memberRepository.existsByEmail(anyString())).willReturn(false);

        memberService.createMember(validRegistrationDto);

        then(passwordEncoder).should(times(1)).encode("Password123!");
    }
}
