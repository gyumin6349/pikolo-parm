package pikolo.payment;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("PaymentDTO")
@Getter
@Setter
@ToString
public class PaymentDTO {
	private Long paymentId; // payment_id
	private Long userId; // user_id (APP_USER.user_seq FK)
	private String provider; // provider ('KAKAOPAY')
	private String partnerOrderId; // partner_order_id (우리쪽 주문ID)
	private String partnerUserId; // partner_user_id (우리쪽 유저식별자)
	private Long amountKrw; // amount_krw
	private String status; // READY / APPROVED / CANCELED / FAILED
	private String tid; // 카카오 TID
	private String note; // 비고/메모
	private LocalDateTime createdAt; // created_at
	private LocalDateTime approvedAt; // approved_at

}
