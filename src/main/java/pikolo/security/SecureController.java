package pikolo.security;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//테스트용
@RestController
@RequestMapping("/api/secure")
public class SecureController {
 @GetMapping("/me")
 public Map<String, Object> me(org.springframework.security.core.Authentication auth) {
     return Map.of(
         "userId", auth.getName(),
         "roles", auth.getAuthorities().toString()
     );
 }
}

