package com.ams.gateway.appConfig.config;



import com.ams.commonsecurity.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


/**
 * {@code GatewayConfig} is the main security configuration class for the API Gateway,
 * used in a Spring WebFlux-based microservices architecture.
 * <p>
 * This configuration defines how HTTP requests are filtered and secured at the gateway level,
 * utilizing Spring Security in its reactive flavor.
 * </p>
 *
 * <p><b>Purpose:</b></p>
 * <ul>
 *     <li>Protect internal microservices by enforcing JWT-based authentication</li>
 *     <li>Allow unauthenticated access to endpoints like {@code /login}, {@code /register}, etc.</li>
 *     <li>Disable CSRF and form-based login since the API is stateless and uses token-based auth</li>
 * </ul>
 *
 * <p><b>Key Components:</b></p>
 * <ul>
 *     <li>{@link JwtUtil} – a utility class used for parsing and validating JWT tokens</li>
 *     <li>{@code SecurityWebFilterChain} – defines security rules for routing and access control</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * This class is loaded automatically by Spring Boot through classpath scanning and acts as a centralized
 * place to configure security across all gateway routes.
 *
 * @author Yosef Nago

 */
@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

    private final JwtUtil jwtUtil;

    /**
     * Constructor-based dependency injection of {@link JwtUtil}.
     *
     * @param jwtUtil a utility class for handling JWT token creation and validation
     */
    public GatewayConfig(@Autowired JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Configures the {@link SecurityWebFilterChain} for the Spring Cloud Gateway.
     * <p>
     * This method disables CSRF protection, form login, and HTTP basic auth (as these are not applicable
     * to a stateless JWT-secured API). It allows all requests by default (placeholder logic).
     * </p>
     *
     * <p><b>Important:</b> This current implementation permits all requests via {@code anyExchange().permitAll()},
     * which should be replaced later with fine-grained access control and JWT validation logic.
     * </p>
     *
     * @param http the reactive {@link ServerHttpSecurity} object for defining web security rules
     * @return a configured {@link SecurityWebFilterChain} bean
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
                                .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

}
