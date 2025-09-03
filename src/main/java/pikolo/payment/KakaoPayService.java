package pikolo.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

  @Value("${kakaopay.secret}") private String secret;
  @Value("${kakaopay.cid}")    private String cid;
  @Value("${app.base-url}")    private String baseUrl;

  private final RestTemplate rt;
  private final PaymentMapper paymentMapper;
  private final WalletService walletService;

  private HttpHeaders headers() {
	  HttpHeaders h = new HttpHeaders();
	  h.set("Authorization", "SECRET_KEY " + secret); // ← KakaoAK 아님!
	  h.setContentType(MediaType.APPLICATION_JSON);   // ← JSON 사용
	  return h;
	}

  @Transactional
  public String ready(long userSeq, int amountKrw, String itemName) {
    String orderId = "ORD-" + System.currentTimeMillis() + "-" + userSeq;

    paymentMapper.insertReady(userSeq, "KAKAOPAY", orderId,
                              String.valueOf(userSeq), amountKrw, itemName);

    // JSON 바디로 전송
    Map<String, Object> p = Map.of(
        "cid", cid,
        "partner_order_id", orderId,
        "partner_user_id", String.valueOf(userSeq),
        "item_name", itemName,
        "quantity", 1,
        "total_amount", amountKrw,
        "tax_free_amount", 0,
        "approval_url", baseUrl + "/api/pay/kakao/success?order_id=" + orderId,
        "cancel_url",   baseUrl + "/api/pay/kakao/cancel?order_id=" + orderId,
        "fail_url",     baseUrl + "/api/pay/kakao/fail?order_id=" + orderId
    );

    ResponseEntity<Map> res = rt.postForEntity(
        "https://open-api.kakaopay.com/online/v1/payment/ready",
        new HttpEntity<>(p, headers()),
        Map.class
    );

    String tid = (String) res.getBody().get("tid");
    String redirect = (String) res.getBody().get("next_redirect_pc_url");
    paymentMapper.updateTid(orderId, tid);
    return redirect;
  }

  @Transactional
  public void approve(String orderId, String pgToken) {
    var pay = paymentMapper.findByOrderIdForUpdate(orderId);
    if (pay == null) throw new IllegalStateException("주문 없음");
    if ("APPROVED".equals(pay.getStatus())) return;

    Map<String, Object> p = Map.of(
        "cid", cid,
        "tid", pay.getTid(),
        "partner_order_id", orderId,
        "partner_user_id", String.valueOf(pay.getUserId()),
        "pg_token", pgToken
    );

    ResponseEntity<Map> res = rt.postForEntity(
        "https://open-api.kakaopay.com/online/v1/payment/approve",
        new HttpEntity<>(p, headers()),
        Map.class
    );

    Map amount = (Map) res.getBody().get("amount");
    Integer total = (Integer) amount.get("total");
    if (total == null || total.intValue() != pay.getAmountKrw().intValue()) {
      throw new IllegalStateException("승인 금액 불일치");
    }
    paymentMapper.markApproved(orderId);
    
    walletService.topUp(pay.getUserId(), pay.getAmountKrw().longValue());
  }
  
//KakaoPayService.java
@Transactional(readOnly = true)
public Long getCurrentBalance(long userSeq) {
   return paymentMapper.findBalance(userSeq);
}


  @Transactional public void markCanceled(String orderId){ paymentMapper.markStatus(orderId, "CANCELED"); }
  @Transactional public void markFailed  (String orderId){ paymentMapper.markStatus(orderId, "FAILED"); }
}
