package pikolo.login;

import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import pikolo.security.JwtUtil;
import pikolo.user.UserDTO;
import pikolo.user.UserService;

@Controller
@RequiredArgsConstructor
public class LoginController {
	
	private final UserService us;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	@GetMapping("login/login_frm")
	public String Login() {
		
		return "login/login_frm";
	}
	
	@PostMapping("/login/doLogin")
	public String doLogin(@RequestParam String id, 
						  @RequestParam String password, 
						  Model model, 
						  HttpServletResponse res) {

			UserDTO userDTO = us.searchInfoByUserId(id);
			if(userDTO == null || !passwordEncoder.matches(password, userDTO.getPasswordHash())) {
				model.addAttribute("msg", "비밀번호가 틀립니다 다시 입력해주세요");
				
				return "login/login_frm";
			}
			
		       // 세션에 최소 정보 저장 (JWT 도입 전 임시)
//	        session.setAttribute("LOGIN_USER_ID", userDTO.getUserId());
//	        session.setAttribute("LOGIN_USER_NAME", userDTO.getUserName());
//	        session.setAttribute("LOGIN_USER_ROLE", userDTO.getUserRole()); // 'user' 기본
	        String accessToken = jwtUtil.generateAccessToken(userDTO.getUserId(), userDTO.getUserRole());
	        String refreshToken = jwtUtil.generateRefreshToken(userDTO.getUserId());
	        
	        addHttpOnlyCookie(res, "ACCESS_TOKEN", accessToken, 15*60);
	        addHttpOnlyCookie(res, "REFRESH_TOKEN", refreshToken, 7*24*3600);
	        
	        model.addAttribute("userDTO", userDTO);
			
			return "redirect:/";
		}
	
	@PostMapping("/login/logout")
	public String doLogout(HttpServletResponse res) {
		
		addHttpOnlyCookie(res, "ACCESS_TOKEN", "", 0);
		addHttpOnlyCookie(res, "REFRESH_TOKEN", "", 0);
		
		return "redirect:/";
	}
	
	private void addHttpOnlyCookie(HttpServletResponse res, String name, String value, int maxAgeSeconds) {
		Cookie c = new Cookie(name, value);
		c.setHttpOnly(true);
		c.setPath("/");
		c.setMaxAge(maxAgeSeconds);
		res.addCookie(c);
	}
		
	}
	
