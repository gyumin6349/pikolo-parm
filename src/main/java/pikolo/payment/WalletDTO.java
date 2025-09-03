package pikolo.payment;

import java.sql.Timestamp;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("WalletDTO")
@Getter
@Setter
@ToString
public class WalletDTO {

	private Long userSeq;
	private Long currentBalance;
	private Timestamp lastUpdated;
}
