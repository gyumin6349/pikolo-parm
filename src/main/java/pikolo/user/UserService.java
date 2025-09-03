package pikolo.user;

public interface UserService {
	
	void registerUser(UserDTO userDTO);
	public UserDTO searchInfoByUserId(String userId);
}
