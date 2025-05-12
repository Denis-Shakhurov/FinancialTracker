package org.example.auditstarter.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.auditstarter.model.AuditLog;
import org.example.auditstarter.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Аспект для аудита вызовов REST-эндпоинтов.
 * Логирует успешные операции (ответы с кодом 2xx) в репозиторий аудита.
 */
@Aspect
@RequiredArgsConstructor
public class AuditAspect {
    private final String RESPONSE = "response";
    private final AuditLogRepository auditLogRepository;

    /**
     * Определение точки среза для методов REST-эндпоинтов.
     * Охватывает методы, аннотированные Spring Web аннотациями:
     * GetMapping, PostMapping, DeleteMapping, PatchMapping или RequestMapping.
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void restEndpointMethods() {}

    /**
     * Совет, выполняемый после успешного завершения метода REST-эндпоинта.
     * Создает и сохраняет запись аудита для успешных операций (ответы с кодом 2xx).
     *
     * @param joinPoint точка соединения, представляющая выполнение метода
     * @param response ResponseEntity, возвращаемый методом
     */
    @AfterReturning(pointcut = "restEndpointMethods()", returning = RESPONSE)
    public void auditRestEndpoint(JoinPoint joinPoint, ResponseEntity<?> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String action = determineActionType(signature);
            String endpoint = signature.getMethod().getDeclaringClass().getSimpleName() +
                    "." + signature.getMethod().getName();

            createAndSaveAuditLog(joinPoint, action, endpoint, response);
        }
    }

    /**
     * Создает и сохраняет запись аудита на основе деталей выполнения метода.
     *
     * @param joinPoint точка соединения, представляющая выполнение метода
     * @param action тип HTTP-действия (GET, POST и т.д.)
     * @param endpoint вызываемый эндпоинт (в формате класс.метод)
     * @param response ResponseEntity, возвращаемый методом
     */
    private void createAndSaveAuditLog(JoinPoint joinPoint, String action, String endpoint, ResponseEntity<?> response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = null;
        String email = null;
        String username = null;

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            username = authentication.getName();

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                email = userDetails.getUsername();
            } else if (principal instanceof String) {
                email = (String) principal;
            }
        }

        AuditLog audit = new AuditLog();
        audit.setAction(action);
        audit.setUserId(userId);
        audit.setEmail(email != null ? email : username);
        audit.setDetails(String.format("%s вызван: %s", action, endpoint));
        audit.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(audit);
    }

    /**
     * Определяет тип HTTP-действия на основе аннотаций метода.
     *
     * @param signature сигнатура метода для анализа
     * @return тип HTTP-действия в виде строки (GET, POST и т.д.)
     */
    private String determineActionType(MethodSignature signature) {
        Optional<RequestMapping> requestMapping =
                Optional.ofNullable(signature.getMethod().getAnnotation(RequestMapping.class));

        if (requestMapping.isPresent()) {
            RequestMethod[] methods = requestMapping.get().method();
            if (methods.length > 0) {
                return methods[0].name();
            }
            return "REQUEST_MAPPING";
        }

        if (signature.getMethod().getAnnotation(GetMapping.class) != null) return "GET";
        if (signature.getMethod().getAnnotation(PostMapping.class) != null) return "POST";
        if (signature.getMethod().getAnnotation(DeleteMapping.class) != null) return "DELETE";
        if (signature.getMethod().getAnnotation(PatchMapping.class) != null) return "PATCH";

        return "UNKNOWN_ACTION";
    }
}
