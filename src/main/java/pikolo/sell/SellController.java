package pikolo.sell;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import pikolo.user.CustomUserDetails;

@Controller
public class SellController {
	
	@GetMapping("/sell/register")
	public String registerProduct(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		if(userInfo != null) {
			model.addAttribute("userId", userInfo.getUsername());
		}
		
		return "sell/register";
	}
	
	@PostMapping("/upload/image")
	public String registerImage(List<MultipartFile> mfList, Model model) {
		
		List<String> uploadFiles = new ArrayList<>();
		
		
		return "";
	}
	
}
