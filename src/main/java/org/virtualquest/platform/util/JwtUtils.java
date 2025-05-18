    package org.virtualquest.platform.util;

    import io.jsonwebtoken.*;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Component;

    import jakarta.annotation.PostConstruct;
    import java.security.Key;
    import java.util.Date;

    @Component
    public class JwtUtils {

        @Value("${jwt.jwtRefreshExpirationMs}")
        private int jwtRefreshExpirationMs;


        @Value("${jwt.jwtExpirationMs}")
        private int jwtExpirationMs;

        private Key key;

        @PostConstruct
        public void init() {
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }

        public String generateAccessToken(UserDetails userDetails) {
            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        }

        public String getUsernameFromJwtToken(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }

        public boolean validateJwtToken(String token) {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (JwtException e) {
                return false;
            }
        }

        public String generateRefreshToken(UserDetails userDetails) {
            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        }
    }