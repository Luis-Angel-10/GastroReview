package websiters.gastroreview.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import websiters.gastroreview.dto.AddressRequest;
import websiters.gastroreview.dto.AddressResponse;
import websiters.gastroreview.mapper.Mappers;
import websiters.gastroreview.model.Address;
import websiters.gastroreview.repository.AddressRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class AddressService {

    private final AddressRepository repo;

    public AddressService(AddressRepository repo) {
        this.repo = repo;
    }

    public Page<AddressResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(Mappers::toResponse);
    }

    public AddressResponse get(UUID id) {
        Address a = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        return Mappers.toResponse(a);
    }

    public AddressResponse update(UUID id, AddressRequest in) {
        Address a = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (in.getStreet() != null) a.setStreet(in.getStreet());
        if (in.getSite() != null) a.setSite(in.getSite());
        if (in.getNeighborhood() != null) a.setNeighborhood(in.getNeighborhood());
        if (in.getCity() != null) a.setCity(in.getCity());
        if (in.getState_region() != null) a.setStateRegion(in.getState_region());
        if (in.getPostal_code() != null) a.setPostalCode(in.getPostal_code());
        if (in.getCountry() != null) a.setCountry(in.getCountry());
        if (in.getLatitude() != null) a.setLatitude(in.getLatitude());
        if (in.getLongitude() != null) a.setLongitude(in.getLongitude());

        try {
            a = repo.save(a);
            return Mappers.toResponse(a);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid address data");
        }
    }

    public Page<AddressResponse> findByCity(String city, Pageable pageable) {
        return repo.findByCityIgnoreCase(city, pageable)
                .map(Mappers::toResponse);
    }

    public Page<AddressResponse> findByCountry(String country, Pageable pageable) {
        return repo.findByCountryIgnoreCase(country, pageable)
                .map(Mappers::toResponse);
    }

    public Page<AddressResponse> findByStreet(String street, Pageable pageable) {
        return repo.findByStreetContainingIgnoreCase(street, pageable)
                .map(Mappers::toResponse);
    }

    public Page<AddressResponse> findByCityAndCountry(String city, String country, Pageable pageable) {
        return repo.findByCityIgnoreCaseAndCountryIgnoreCase(city, country, pageable)
                .map(Mappers::toResponse);
    }

    @Service
    public static class JwtService {

        @Value("${security.jwt.secret-key}")
        private String secretKey;

        @Value("${security.jwt.expiration-time}")
        private long jwtExpirationMs;

        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }

        public String generateToken(UserDetails userDetails) {
            return generateToken(Map.of(), userDetails);
        }

        public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
            return buildToken(extraClaims, userDetails, jwtExpirationMs);
        }

        private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        private boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        private Claims extractAllClaims(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }

        private Key getSigningKey() {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        }

        public boolean isTokenValid(String token, UserDetails userDetails) {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        }
    }
}
