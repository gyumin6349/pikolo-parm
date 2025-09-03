package pikolo.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import pikolo.user.CustomUserDetails;
import pikolo.user.UserDTO;
import pikolo.user.UserService;

@Controller
@RequiredArgsConstructor
public class AccountController {

	private final UserService us;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@PostMapping("/account/confirm-password")
	public String checkPassword(@AuthenticationPrincipal CustomUserDetails userInfo,
			@RequestParam String password) {
		if(userInfo != null) {
			UserDTO userDTO = us.searchInfoByUserId(userInfo.getUsername());
			
			if(passwordEncoder.matches(password, userDTO.getPasswordHash())) {
				
				return "redirect:/user/profile";
			}
		
		}
		
		return "user/confirm_pass";
	}
	
	@GetMapping("/user/profile")
	public String userProfile(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		if(userInfo != null) {
			UserDTO userDTO = us.searchInfoByUserId(userInfo.getUsername());
			model.addAttribute("user", userDTO);
		}
		
		return "user/modfiy_userInfo";
	}
	
	@PostMapping("/account/profile")
	public String modifyProfile(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		if(userInfo != null) {
			UserDTO userDTO = us.searchInfoByUserId(userInfo.getUsername());
			model.addAttribute("user", userDTO);
		}
		
		return "user/modify_userInfo";
	}
	
}
