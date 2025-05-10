package bloodfinders.blood_api.jwt;

import bloodfinders.blood_api.jwt.JwtUtil;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.repository.UserRepository;
import bloodfinders.blood_api.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static bloodfinders.blood_api.jwt.SecurityConfig.PUBLIC_ENDPOINTS;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("JWT token missing or invalid format for URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            logger.warn("JWT token validation failed for token: {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UUID userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("Could not find user information from JWT");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean shouldSkip = PUBLIC_ENDPOINTS.contains(path);
        if (shouldSkip) {
            logger.info("Skipping JWT filter for public path: {}", path);
        }
        return shouldSkip;
    }
}