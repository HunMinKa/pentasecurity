package pentasecurity.task.service;

import jakarta.validation.Valid;
import pentasecurity.task.dto.RegistrationDto;

public interface MemberService {
    void createMember(@Valid RegistrationDto member);

}
