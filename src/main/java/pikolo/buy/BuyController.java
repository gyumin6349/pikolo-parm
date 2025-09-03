package pikolo.buy;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pikolo.user.CustomUserDetails;

@Controller
public class BuyController {

	@GetMapping("/buy/list")
	public String buyProductPage(@AuthenticationPrincipal CustomUserDetails userDetail, Model model) {
		if(userDetail!=null) {
			model.addAttribute("userId", userDetail.getUsername());
		}
		
		return "buy/buy_page";
	}
}
