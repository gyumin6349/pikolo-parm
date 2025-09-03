package pikolo.user;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

	void registerUser(UserDTO userDTO);
	public UserDTO searchInfoByUserId(String userId);
}
