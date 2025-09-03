package pikolo.payment;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentMapper pm;
	
	@Override
	public void insertReady(@Param("userId") long userId,
            @Param("provider") String provider,
            @Param("partnerOrderId") String partnerOrderId,
            @Param("partnerUserId") String partnerUserId,
            @Param("amountKrw") int amountKrw,
            @Param("note") String note) {
		pm.insertReady(userId, provider, partnerOrderId, partnerUserId, amountKrw, note);
	}

}
