package websiters.gastroreview.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import websiters.gastroreview.security.JwtAuthenticationEntryPoint;
import websiters.gastroreview.security.JwtAuthenticationFilter;
import websiters.gastroreview.service.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/api/users/signup",
                                "/api/users/signin",
                                "/gastroreview/users/**",
                                "/graphiql",
                                "/graphql",
                                "/api/addresses/**",
                                "/api/alerts/**",
                                "/api/dishes/**",
                                "/api/favoriteRestaurants/**",
                                "/api/favoriteRestaurantIds/**",
                                "/api/favoriteReviews/**",
                                "/api/favoriteReviewIds/**",
                                "/api/friendships/**",
                                "/api/friendshipIds/**",
                                "/api/notifications/**",
                                "/api/ratings/**",
                                "/api/restaurantsAddresses/**",
                                "/api/restaurantsAddressIds/**",
                                "/api/restaurantCategories/**",
                                "/api/restaurantImages/**",
                                "/api/restaurantSchedules/**",
                                "/api/reviewsAudios/**",
                                "/api/reviewComments/**",
                                "/api/reviewCommentAnalysis/**",
                                "/api/reviewImages/**",
                                "/api/usersAchievements/**",
                                "/api/usersPreferences/**",
                                "/api/usersProfiles/**",
                                "/api/usersRoles/**",
                                "/api/usersRoleIds/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).hasRole("ADMIN")

                        .requestMatchers("/api/restaurants/**").hasAnyRole("OWNER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(basic -> basic.realmName("Swagger Realm"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
