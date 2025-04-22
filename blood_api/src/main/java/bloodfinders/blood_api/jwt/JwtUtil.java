//package blood_dontation.blood_api.jwt;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import java.util.Date;
//import java.util.UUID;
//
//public class JwtUtil {
//
//    private final String SECRET_KEY = "your_super_secret_key_should_be_long_enough";
//
//    private final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 365;
//
//    public String generateToken(UUID userId) {
//        return Jwts.builder()
//                .setSubject(userId.toString())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public UUID extractUserId(String token) {
//        String userId = Jwts.parserBuilder()
//                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//        return UUID.fromString(userId);
//    }
//
//    public boolean isTokenValid(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//}
