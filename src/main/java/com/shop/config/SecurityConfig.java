package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/users", "/api/session")
                    .permitAll()
                    .requestMatchers("/api/me", "/api/orders/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .securityContext(
            securityContext ->
                securityContext
                    .securityContextRepository(securityContextRepository())
                    .requireExplicitSave(false))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    (request, response, authException) -> {
                      response.setStatus(HttpStatus.UNAUTHORIZED.value());
                      response.setContentType("application/problem+json");
                      response
                          .getWriter()
                          .write(
                              """
                                {"type":"/problems/unauthorized","title":"Unauthorized","status":401}
                                """);
                    }))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler((req, res, auth) -> res.setStatus(204))
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID"));

    return http.build();
  }

  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public CookieSerializer cookieSerializer() {
    var serializer = new DefaultCookieSerializer();
    serializer.setCookieName("JSESSIONID");
    serializer.setUseHttpOnlyCookie(true);
    serializer.setSameSite("Lax");
    return serializer;
  }
}
