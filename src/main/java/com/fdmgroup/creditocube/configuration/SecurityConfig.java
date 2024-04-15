package com.fdmgroup.creditocube.configuration;

 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
 
/**
* Security configuration class that sets up Spring Security for the application.
* This configuration includes defining how the application handles login, logout,
* and which URLs are secured.
*/
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
	/**
     * Bean definition for password encoder that uses the BCrypt hashing function.
     * This encoder is used to secure user passwords before they are stored in the database.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configures the security filter chain that dictates the security policy for HTTP requests.
     * This includes configuration for protected URLs, the custom login page, and the logout process.
     *
     * @param http the HttpSecurity object provided by Spring Security to configure web-based security
     * @return the fully configured SecurityFilterChain
     * @throws Exception if an error occurs during the configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login").permitAll() // Allow public access to registration and login pages
                .anyRequest().authenticated()) // Require authentication for all other requests
            .formLogin(form -> form
                .loginPage("/login") // Custom login page URL
                .defaultSuccessUrl("/home", true) // Redirect to the home page upon successful login
                .permitAll()) // Allow all users to access the login page
            .logout(logout -> logout
                .logoutSuccessUrl("/login")); // Redirect to the login page upon successful logout
        
        return http.build();
    }
}
 