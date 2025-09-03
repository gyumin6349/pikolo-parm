package pikolo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public void registerUser(UserDTO userDTO) {
		userDTO.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));
		if(userDTO.getUserRole() == null) {
			userDTO.setUserRole("USER");
		}
		userMapper.registerUser(userDTO);
	}

	@Override
	public UserDTO searchInfoByUserId(String userId) {
		
		UserDTO user = userMapper.searchInfoByUserId(userId);
		
		return user;
	}

	
}
