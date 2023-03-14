package com.team3.central.security.filterChain;

import com.team3.central.services.JwtService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtSecurityFilterChain extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
//    final String authHeader = request.getHeader("Authorization");
//    final String jwt;
//    final String userEmail;
//    if(authHeader == null || !authHeader.startsWith("Bearer ")) {
//      filterChain.doFilter(request, response);
//      return;
//    }
//    jwt = authHeader.substring(7);
//    userEmail = jwtService.extractUserEmail(jwt);
//    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//      // fetch user from DB
//      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//      if(jwtService.isTokenValid(jwt, userDetails)) {
//        UsernamePasswordAuthenticationToken authToken =
//            new UsernamePasswordAuthenticationToken(userDetails,
//                null,
//                userDetails.getAuthorities());
//        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//      }
//    }
    filterChain.doFilter(request, response);
  }
}
