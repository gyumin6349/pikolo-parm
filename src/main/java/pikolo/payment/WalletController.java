package pikolo.payment;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import pikolo.user.CustomUserDetails;
import pikolo.user.UserDTO;
import pikolo.user.UserService;

@Controller
@RequiredArgsConstructor

public class WalletController {
	private final UserService userService;
	private final KakaoPayService kakao; // 잔액 조회용
	private final WalletService walletService;

	@GetMapping("/wallet")
	public String wallet(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		long userSeq = 0L;
		if (userInfo != null) {
			var u = userService.searchInfoByUserId(userInfo.getUsername());
			if (u != null)
				userSeq = u.getUserSeq();
		}
		Long balance = walletService.getCurrentBalance(userSeq);
		model.addAttribute("balance", balance == null ? 0 : balance);
		return "wallet/wallet_success";
	}

	@GetMapping("/wallet/myWallet")
	public String myWallet(@AuthenticationPrincipal CustomUserDetails userInfo, Model model) {
		long userSeq = 0L;
		if (userInfo != null) {
			UserDTO u = userService.searchInfoByUserId(userInfo.getUsername());
			if (u != null) {
				userSeq = u.getUserSeq();
			}
		}

		Long balance = walletService.getCurrentBalance(userSeq);
		model.addAttribute("balance", balance == null ? 0 : balance);
		return "wallet/myWallet";
	}
}
