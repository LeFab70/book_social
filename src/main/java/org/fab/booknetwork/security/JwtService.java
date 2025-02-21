package org.fab.booknetwork.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.fab.booknetwork.user.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${application.security.jwt.expiration}")
   private Long jwtExpiration;
   @Value("${application.security.jwt.secret-Key}")
    private String secretKey;
    public String generateToken(UserDetails userDetails){
    return generateToken(new HashMap<>(),userDetails);
    }

    private String generateToken(Map<String, Object> claims,UserDetails userDetails){

        return buildToken(claims,userDetails,jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long jwtExpiration) {
        var authorities=userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUserName(token);
        return (username.equals(userDetails.getUsername())) && !isTokenValid(token);
    }

    private boolean isTokenValid(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }


    private Key getSignInKey() {

        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
