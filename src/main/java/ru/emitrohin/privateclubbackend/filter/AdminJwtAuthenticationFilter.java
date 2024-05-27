package ru.emitrohin.privateclubbackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.emitrohin.privateclubbackend.model.AdminUser;
import ru.emitrohin.privateclubbackend.service.JwtService;
import ru.emitrohin.privateclubbackend.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
@RequiredArgsConstructor
//TODO  выглядит идентичным с фильтром для телеграм пользователей, сделать обощенным
public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Получаем токен из заголовка
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Обрезаем префикс и получаем имя пользователя из токена
            String jwt = authHeader.substring(BEARER_PREFIX.length());

            UUID adminId = jwtService.extractAdminId(jwt);

            // Если токен валиден, то ищем в базе по adminId и если есть создаем токен Spring по adminId
            if (jwtService.isAdminTokenValid(jwt, adminId)) {
                Optional<AdminUser> authUser = userService.findAdminByIdForFilter(adminId); //FIXME странное название метода
                if (authUser.isPresent()) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            authUser.get().getId(),
                            null,
                            List.of(new SimpleGrantedAuthority(authUser.get().getRole().name()))
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}