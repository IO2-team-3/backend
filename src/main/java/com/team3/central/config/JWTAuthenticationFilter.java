package com.team3.central.config;

import com.team3.central.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
      final String jwt = request.getHeader("sessionToken");
      final String userEmail;
      if(jwt == null) {
        filterChain.doFilter(request, response);
        return;
      }
      try{
        userEmail = jwtService.extractUserEmail(jwt);
      } catch (Exception e) {
        if(e instanceof ExpiredJwtException) {
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        else {
          response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
        return;
      }
      if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        if(jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
          authToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request)
          );
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    filterChain.doFilter(request,response);
  }
}