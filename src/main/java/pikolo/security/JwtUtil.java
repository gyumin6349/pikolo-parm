package pikolo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final Key key;
//	Keys.secretKeyFor(SignatureAlgorithm.HS256); // 비밀키
	private final long accessTokenValidity; 
//	1000 * 60 * 15; // access토큰 유지시간 15분
	private final long refreshTokenValidity;
//	1000L * 60 * 60 * 24 * 7; // refresh토큰 7일

	  public JwtUtil(
	            @Value("${jwt.secret}") String secret,
	            @Value("${jwt.access-validity}") long accessTokenValidity,
	            @Value("${jwt.refresh-validity}") long refreshTokenValidity
	    ) {
	        // secret 문자열을 바이트 배열로 바꿔서 HS256 key 생성
	        this.key = Keys.hmacShaKeyFor(secret.getBytes());
	        this.accessTokenValidity = accessTokenValidity;
	        this.refreshTokenValidity = refreshTokenValidity;
	    }
	
	public String generateAccessToken(String userId, String role) {
		return
		Jwts.builder()
		.setSubject(userId)//주제
		.claim("role", role)//추가데이터
		.setIssuedAt(new Date()) //발급날짜
		.setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))//만료시간
		.signWith(key)
		.compact(); //문자열로 압축
	
	}
	
	
	public String generateRefreshToken(String userId) {
		
		return Jwts.builder()
				.setSubject(userId)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+refreshTokenValidity))
				.signWith(key)
				.compact();
	}//generateRefresh
	
	public Jws<Claims> validateToken(String token){
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token); //parseClaim 역할 header + payload(정보) + signature 분리 키확인 -> 만료시간 정보 확인 -> 빌드 
	}//토큰 검증
	
	public String getUserId(String token) {
		return validateToken(token).getBody().getSubject(); //유저아이디뽑기 getSubject
	}//토큰꺼내기 userID
	
	public String getRole(String token) {
	    return validateToken(token).getBody().get("role", String.class);
	}//role
}
