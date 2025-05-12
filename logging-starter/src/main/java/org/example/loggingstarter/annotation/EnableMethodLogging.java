package org.example.loggingstarter.annotation;

import org.example.loggingstarter.config.LoggingAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для включения логирования методов в Spring-приложении.
 *
 * @see LoggingAutoConfiguration Конфигурация, которая активируется этой аннотацией
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoggingAutoConfiguration.class)
public @interface EnableMethodLogging {
    /**
     * Уровень логирования по умолчанию для методов.
     * <p>
     * По умолчанию используется уровень DEBUG. Можно переопределить в свойствах приложения.
     *
     * @return уровень логирования (DEBUG, INFO, WARN, ERROR)
     */
    String level() default "DEBUG";

    /**
     * Флаг для включения/выключения логирования входных параметров методов.
     * <p>
     * Если true, параметры метода будут логироваться перед его выполнением.
     *
     * @return true - логировать параметры, false - не логировать
     */
    boolean logParameters() default true;

    /**
     * Флаг для включения/выключения логирования результата выполнения методов.
     * <p>
     * Если true, возвращаемое значение метода будет логироваться после выполнения.
     *
     * @return true - логировать результат, false - не логировать
     */
    boolean logResult() default true;

    /**
     * Флаг для включения/выключения логирования времени выполнения методов.
     * <p>
     * Если true, будет залогировано время выполнения метода в миллисекундах.
     *
     * @return true - логировать время выполнения, false - не логировать
     */
    boolean logExecutionTime() default true;
}
