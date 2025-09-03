package pikolo.main;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pikolo.user.CustomUserDetails;

@Controller
public class MainController {
	
	@RequestMapping(value="/", method = {RequestMethod.GET, RequestMethod.POST})
	public String main(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		if(userInfo != null) {
			model.addAttribute("userId", userInfo.getUsername());
		}
		
		return "main/main";
	}
}
