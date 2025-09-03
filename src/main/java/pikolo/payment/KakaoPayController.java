package pikolo.payment;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pikolo.user.CustomUserDetails;
import pikolo.user.UserDTO;
import pikolo.user.UserService;

@RestController
@RequestMapping("/api/pay/kakao")
@RequiredArgsConstructor
public class KakaoPayController {

  private final KakaoPayService kakao;
  private final UserService userService;

  /** 결제 준비: 카카오 ready 호출 후 redirectUrl 반환 */
  @PostMapping("/ready")
  public ResponseEntity<Map<String,String>> ready(@RequestBody Map<String,Object> body,
                                                  @AuthenticationPrincipal CustomUserDetails userInfo) {

    long userSeq = 0L;
    if (userInfo != null) {
      UserDTO user = userService.searchInfoByUserId(userInfo.getUsername()); // loginId -> user
      if (user != null) userSeq = user.getUserSeq(); // NUMBER -> long
    }
    int amountKrw = ((Number) body.get("amountKrw")).intValue();
    String itemName = (String) body.getOrDefault("itemName", "마일리지 충전");

    String redirectUrl = kakao.ready(userSeq, amountKrw, itemName);
    return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
  }

  /** 승인 콜백: 카카오가 ?pg_token&order_id 로 리다이렉트해줌 */
  @GetMapping("/success")
  public RedirectView success(@RequestParam("pg_token") String pgToken,
                              @RequestParam("order_id") String orderId) {
    kakao.approve(orderId, pgToken);
    
//    long userSeq = 0L;
//    if (userInfo != null) {
//        UserDTO u = userService.searchInfoByUserId(userInfo.getUsername());
//        if (u != null) userSeq = u.getUserSeq();
//    }
//
//    // 지갑 테이블에서 현재 잔액 조회
//    Long balance = kakao.getCurrentBalance(userSeq);
//
//    model.addAttribute("balance", balance == null ? 0 : balance);
    
    
    
    return new RedirectView("/wallet"); // -> templates/wallet/success.html
  }

  @GetMapping("/cancel")
  public RedirectView cancel(@RequestParam("order_id") String orderId) {
    kakao.markCanceled(orderId);
    return new RedirectView("/charge/canceled?orderId=" + orderId);
  }

  @GetMapping("/fail")
  public RedirectView fail(@RequestParam("order_id") String orderId) {
    kakao.markFailed(orderId);
    return new RedirectView("/charge/failed?orderId=" + orderId);
  }
}
