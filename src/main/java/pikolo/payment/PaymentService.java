package pikolo.payment;

import org.apache.ibatis.annotations.Param;

public interface PaymentService {
	 void insertReady(@Param("userId") long userId,
             @Param("provider") String provider,
             @Param("partnerOrderId") String partnerOrderId,
             @Param("partnerUserId") String partnerUserId,
             @Param("amountKrw") int amountKrw,
             @Param("note") String note);
}
