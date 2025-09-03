package pikolo.payment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

	void insertReady(@Param("userId") long userId,
            @Param("provider") String provider,
            @Param("partnerOrderId") String partnerOrderId,
            @Param("partnerUserId") String partnerUserId,
            @Param("amountKrw") int amountKrw,
            @Param("note") String note);
	
	void updateTid(@Param("orderId") String orderId, @Param("tid") String tid);

	  PaymentDTO findByOrderIdForUpdate(@Param("orderId") String orderId);

	  void markApproved(@Param("orderId") String orderId);

	  void markStatus(@Param("orderId") String orderId, @Param("status") String status);
	  
	  Long findBalance(Long userSeq);
	  
	  WalletDTO findWallet(Long userSeq);
	  void insertWallet(Long userSeq, Long amount);
	  void updateWalletBalance(Long userSeq, Long amount);
}
