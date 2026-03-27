package com.esun.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    private String secret = "mySecretKeyForEsunDemoProject2026";
    private long expiration = 86400000; // 24 小時

    // 產生 Token
    public String generateToken(String phoneNumber) {
        return Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret) // 注意：舊版順序是 (演算法, 字串)
                .compact();
    }

    // 從 Token 提取手機號碼
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 通用的提取方法
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // 檢查 Token 是否有效
    public boolean validateToken(String token, String phoneNumber) {
        try {
            final String extractedPhone = extractUsername(token);
            // 檢查手機號碼是否相符，且 Token 尚未過期
            return (extractedPhone.equals(phoneNumber) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}