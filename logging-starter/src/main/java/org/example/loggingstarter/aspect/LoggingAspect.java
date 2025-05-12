package org.example.loggingstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.loggingstarter.annotation.EnableMethodLogging;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Collectors;

/**
 * Аспект для логирования выполнения методов в приложении.
 * Логирует время выполнения, аргументы и результаты методов,
 * а также детали HTTP запросов для веб-слоя.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnBean(annotation = EnableMethodLogging.class)
public class LoggingAspect {

    /**
     * Advice для логирования выполнения методов.
     * Логирует начало и конец выполнения метода, его аргументы и результат,
     * а также время выполнения. Для веб-запросов дополнительно логирует
     * HTTP метод, URI и заголовки.
     *
     * @param joinPoint точка соединения для перехватываемого метода
     * @return результат выполнения метода
     * @throws Throwable если метод выбрасывает исключение
     */
    @Around("@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Service) || " +
            "@within(org.springframework.stereotype.Repository) && execution(* *(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodName = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();

        if (isWebRequest()) {
            HttpServletRequest request = getCurrentRequest();
            logRequestDetails(request, methodName);
        } else {
            log.debug("Начало выполнения метода {} с аргументами: {}", methodName,
                    Arrays.stream(args).map(this::formatArgument).collect(Collectors.toList()));
        }

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();

            if (isWebRequest()) {
                log.debug("Метод {} успешно выполнился за {} мс",
                        methodName, stopWatch.getTotalTimeMillis());
            } else {
                log.debug("Метод {} успешно выполнился за {} мс. Результат: {}",
                        methodName, stopWatch.getTotalTimeMillis(), formatResult(result));
            }

            return result;
        } catch (Exception e) {
            log.error("Метод {} завершился с ошибкой за {} мс. Исключение: {}",
                    methodName, stopWatch.getTotalTimeMillis(), e.toString(), e);
            throw e;
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }
    }

    /**
     * Логирует детали HTTP запроса.
     *
     * @param request HTTP запрос
     * @param methodName имя вызываемого метода
     */
    private void logRequestDetails(HttpServletRequest request, String methodName) {
        StringBuilder requestInfo = new StringBuilder();
        requestInfo.append("HTTP ").append(request.getMethod()).append(" ").append(request.getRequestURI());

        if (request.getQueryString() != null) {
            requestInfo.append("?").append(request.getQueryString());
        }

        log.debug("Начало обработки запроса: {} -> {}", requestInfo, methodName);

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            StringBuilder headers = new StringBuilder("Headers: ");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.append(headerName).append(": ").append(request.getHeader(headerName));
                if (headerNames.hasMoreElements()) {
                    headers.append(", ");
                }
            }
            log.debug(headers.toString());
        }
    }

    /**
     * Форматирует аргумент метода для безопасного логирования.
     * Маскирует чувствительные данные (например, пароли).
     *
     * @param arg аргумент метода
     * @return отформатированная строка аргумента
     */
    private String formatArgument(Object arg) {
        if (arg == null) {
            return "null";
        }

        if (arg instanceof String strArg && strArg.toLowerCase().contains("password")) {
            return "*****";
        }
        return arg.toString();
    }

    /**
     * Форматирует результат метода для логирования.
     * Специально обрабатывает ResponseEntity для более читаемого вывода.
     *
     * @param result результат выполнения метода
     * @return отформатированная строка результата
     */
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }

        if (result instanceof ResponseEntity<?> responseEntity) {
            return "ResponseEntity(status=" + responseEntity.getStatusCode() + ")";
        }
        return result.toString();
    }

    /**
     * Проверяет, выполняется ли метод в контексте веб-запроса.
     *
     * @return {@code true} если есть текущий HTTP запрос, {@code false} в противном случае
     */
    private boolean isWebRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    /**
     * Получает текущий HTTP запрос.
     *
     * @return текущий HttpServletRequest
     * @throws IllegalStateException если нет текущего запроса
     */
    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}