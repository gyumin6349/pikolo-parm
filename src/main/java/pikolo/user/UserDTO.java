package pikolo.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("UserDTO")
@Getter
@Setter
@ToString
public class UserDTO {
	
	Long userSeq;
	String userId;
	String passwordHash;
	String userRole;
	LocalDateTime createdAt;
	String userEmail;
	String userPhone;
	String userName;
	LocalDate birthDate;
	String bankName;
	String userBankAccount;
	String userAddress;
}
