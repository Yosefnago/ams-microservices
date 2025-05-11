package com.ams.gateway.appConfig.config;


import com.ams.commonsecurity.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

/**
 * {@code JwtGlobalFilter} is a {@link GlobalFilter} implementation used in Spring Cloud Gateway
 * to enforce JWT-based authentication across all protected routes.
 * <p>
 * This global filter intercepts incoming HTTP requests at the gateway level,
 * checks for the presence and validity of a JWT in the `Authorization` header,
 * and forwards the request only if the token is valid.
 * </p>
 *
 * <p><b>Main Responsibilities:</b></p>
 * <ul>
 *   <li>Allowing unauthenticated access to public endpoints such as login, register, and static resources</li>
 *   <li>Extracting and validating JWT tokens from the request headers</li>
 *   <li>Injecting user identity (username) into the request via custom header {@code X-User-Name}</li>
 *   <li>Blocking requests with invalid or missing JWT tokens</li>
 * </ul>
 *
 * <p><b>Note:</b> The filter is globally applied to all requests due to implementing {@link GlobalFilter}.</p>
 *
 * @author Yosef Nago
 */
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    /**
     * Constructs a {@code JwtGlobalFilter} using the injected {@link JwtUtil} instance.
     *
     * @param jwtUtil a utility class for extracting and validating JWT tokens.
     */
    public JwtGlobalFilter(@Autowired JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    /**
     * Applies the JWT validation logic to every incoming HTTP request.
     * <p>
     * - If the request targets a public path, it proceeds without checks.<br>
     * - Otherwise, it validates the JWT from the {@code Authorization} header.<br>
     * - If valid, injects the username into the request and continues.<br>
     * - If invalid, responds with {@code 401 Unauthorized}.
     * </p>
     *
     * @param exchange the current server exchange
     * @param chain    the gateway filter chain
     * @return {@code Mono<Void>} indicating when request handling is complete
     */
    @Override
    public  Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();


        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "Invalid JWT token");
        }

        String username = jwtUtil.extractUsername(token);
        if (username == null || username.isEmpty()) {
            return unauthorized(exchange, "Token does not contain valid username");
        }

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Name", username)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    /**
     * Checks if the given path should be treated as public and bypass authentication.
     *
     * @param path the request path
     * @return {@code true} if the path is public; {@code false} otherwise
     */
    private boolean isPublicPath(String path) {
        return path.equals("/auth/login") ||
                path.equals("/auth/register") ||
                path.equals("/client/login") ||
                path.equals("/") ||
                path.equals("/index") ||
                path.equals("/index.html") ||
                path.startsWith("/frontend/") ||
                path.startsWith("/VAADIN/") ||
                path.startsWith("/app/") ||
                path.equals("/favicon.ico");
    }
    /**
     * Sends an HTTP 401 Unauthorized response with a custom message.
     *
     * @param exchange the current server exchange
     * @param message  the error message to send in the response body
     * @return {@code Mono<Void>} that completes after writing the response
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
    /**
     * Specifies the filter's order of execution in the global filter chain.
     * <p>
     * A lower value means higher precedence.
     *
     * @return the order value; in this case, -1 for high priority
     */
    @Override
    public int getOrder() {
        return -1;
    }



}
