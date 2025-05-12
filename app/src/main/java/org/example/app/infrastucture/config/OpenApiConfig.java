package org.example.app.infrastucture.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация SpringDoc OpenAPI для автоматической генерации документации API.
 * Настраивает автоматическое сканирование контроллеров и создание документации
 * в формате OpenAPI 3.0.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает и настраивает основной OpenAPI bean.
     * Конфигурирует:
     * - Информацию о API (название, описание, версия)
     * - Автоматическое сканирование всех контроллеров
     *
     * @return Объект OpenAPI с настроенной документацией
     * @apiNote Документация будет доступна по стандартным путям:
     *          - /v3/api-docs (JSON)
     *          - /swagger-ui.html (Web интерфейс)
     *          - /swagger-ui/index.html (Альтернативный Web интерфейс)
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API для управления финансами")
                        .description("Система управления финансовыми операциями, транзакциями и пользователями")
                        .version("1.0.0"));
    }
}
