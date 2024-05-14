package pentasecurity.task.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pentasecurity.task.dto.RegistrationDto;
import pentasecurity.task.exception.MemberAlreadyExistException;
import pentasecurity.task.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;;


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("member", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("member") @Valid RegistrationDto registrationDto, BindingResult result, RedirectAttributes attributes) {
         log.info(registrationDto.getEmail());
        if (result.hasErrors()) {
            return "register";
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.confirmError", "비밀번호가 일치하지 않습니다");
            return "register";
        }
        try {
            memberService.createMember(registrationDto);
        } catch (MemberAlreadyExistException e) {
            result.rejectValue("email", "registrationDto.email", "아이디가 중복되었습니다.");
            return "register";
        }
        attributes.addFlashAttribute("message", "가입되셨습니다");
        return "redirect:/register";
    }
}

