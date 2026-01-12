package Library.backend.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key SECRET_KEY;

    @Autowired
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateToken(int studentId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("studentId", studentId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 ))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractStudentId(String token) {
        return String.valueOf(getClaims(token).get("studentId"));
    }

    public boolean validateToken(String token, int studentId) {
        String id = extractStudentId(token);
        return id.equals(String.valueOf(studentId));
    }

    private Claims getClaims(String token) {

        System.out.println("SECRET: " + Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded()));
        System.out.println("TOKEN: " + token);
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
