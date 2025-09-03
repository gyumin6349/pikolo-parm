package pikolo.payment;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pikolo.user.CustomUserDetails;

@Controller
public class PaymentController {

	@GetMapping("/user/payment")
	public String userPayment(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		if(userInfo != null) {
			model.addAttribute("userId", userInfo.getUsername());
		}
		
		
		return "payment/payment";
	}
	
}
