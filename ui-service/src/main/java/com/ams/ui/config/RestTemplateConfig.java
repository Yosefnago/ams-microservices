package com.ams.ui.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * {@code RestTemplateConfig} is a configuration class that provides application-wide beans
 * for HTTP communication between microservices and external REST APIs.
 *
 * <p>
 * This class currently defines a reusable singleton {@link RestTemplate} bean.
 * It can be extended to include HTTP interceptors, error handlers, or token injectors.
 * </p>
 *
 * <p><b>Typical Usage:</b></p>
 * <pre>{@code
 * @Autowired
 * private RestTemplate restTemplate;
 * }</pre>
 *
 * <p><b>Example Use Cases:</b></p>
 * <ul>
 *     <li>Calling the <code>user-service</code> to retrieve authenticated user data</li>
 *     <li>Sending client registration data to <code>client-service</code></li>
 *     <li>Performing internal API calls through the <code>gateway-service</code></li>
 * </ul>
 *
 *
 * @author Yosef Nago
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Defines a singleton {@link RestTemplate} bean used to make synchronous HTTP requests.
     * <p>
     * This instance can be reused throughout the application to call internal services
     * registered with Eureka or external third-party APIs.
     * </p>
     *
     * @return a plain {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
