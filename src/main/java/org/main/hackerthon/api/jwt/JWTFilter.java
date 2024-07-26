package org.main.hackerthon.api.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.main.hackerthon.api.domain.User;
import org.main.hackerthon.api.dto.LoginRequest;
import org.main.hackerthon.api.oauth2.CustomOAuth2User;
import org.main.hackerthon.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;

  private UserService userService;

  public JWTFilter(JWTUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();
    if (requestUri.matches("^\\/login(?:\\/.*)?$")) {
      filterChain.doFilter(request, response);
      return;
    }
    if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {
      filterChain.doFilter(request, response);
      return;
    }

    String authorization = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("Authorization")) {
          authorization = cookie.getValue();
        }
      }
    }

    //Authorization 헤더 검증
    if (authorization == null) {
      filterChain.doFilter(request, response);
      return;
    }

    //토큰
    String token = authorization;
    if (jwtUtil.isExpired(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String uniqueId = jwtUtil.getUniqueId(token);

      // DB에서 사용자 확인
      Optional<User> optionalUser = userService.findByUniqueId(uniqueId);
      if (optionalUser.isEmpty()) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      User user = optionalUser.get();

      LoginRequest loginRequest = LoginRequest.builder()
          .uniqueId(jwtUtil.getUniqueId(token))
          .role(jwtUtil.getRole(token))
          .build();

      CustomOAuth2User customOAuth2User = new CustomOAuth2User(loginRequest);

      Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }
}
