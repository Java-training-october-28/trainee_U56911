package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Enable form-based login
            .formLogin(Customizer.withDefaults())
            
            // Enable HTTP Basic authentication
            .httpBasic(Customizer.withDefaults())
            
            // Configure session management to be stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/", "/health", "/favicon.ico", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                
                // User management endpoints
                .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                
                // Project management endpoints
                .requestMatchers(HttpMethod.GET, "/api/projects").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/projects/{id}").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/projects/{id}").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/projects/{id}").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                
                // Task management endpoints
                .requestMatchers(HttpMethod.GET, "/api/tasks/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/tasks").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                .requestMatchers(HttpMethod.PUT, "/api/tasks/{id}").hasAnyRole("ADMIN", "PROJECT_MANAGER", "DEVELOPER")
                .requestMatchers(HttpMethod.DELETE, "/api/tasks/{id}").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                
                // Comment endpoints
                .requestMatchers(HttpMethod.GET, "/api/comments/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/comments").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/comments/{id}").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/comments/{id}").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins in development
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
