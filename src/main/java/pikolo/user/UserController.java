package pikolo.user;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
	
	@Autowired
	UserService us;
	
	@GetMapping("/user/signup")
	public String goToSignUp() {
		
		return "user/signup";
	}
	
	@GetMapping("/user/mypage")
	public String userMypage(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		if(userInfo != null) {
			model.addAttribute("userId", userInfo.getUsername());
		}
		
		
		return "user/mypage";
	}
	
	@GetMapping("/user/confirm_pass")
	public String confrimPassword(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		if(userInfo != null) {
			model.addAttribute("userId", userInfo.getUsername());
		}
		
		return "user/confirm_pass";
	}
	
	
	@PostMapping("/user/userSignUp")
	public String userSignUp(@RequestParam String userId,
							 @RequestParam String password,
							 @RequestParam String userPhone,
							 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
							 @RequestParam(required=false) String userEmail,
							 @RequestParam String userName,
							 @RequestParam String userAddress,
							 @RequestParam String detailAddress,
							 @RequestParam String userBank,
							 @RequestParam String userBankAccount,
							 RedirectAttributes ra) {
		
		
		
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(userId);
		userDTO.setPasswordHash(password);
		userDTO.setUserPhone(userPhone);
		userDTO.setBirthDate(birthDate);
		userDTO.setUserEmail(userEmail);
		userDTO.setUserName(userName);
		userDTO.setUserAddress(userAddress+" "+detailAddress);
		userDTO.setUserBankAccount(userBankAccount);
		userDTO.setBankName(userBank);
		
		
		System.out.println(userDTO.getPasswordHash());
		
		us.registerUser(userDTO);
		ra.addFlashAttribute("msg", "회원가입 성공!!");
		 return "login/login_frm";
	}
}
