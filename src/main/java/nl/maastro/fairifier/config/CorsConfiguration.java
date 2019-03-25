package nl.maastro.fairifier.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsConfigurationProperties.class)
public class CorsConfiguration implements WebMvcConfigurer {

    private CorsConfigurationProperties corsConfigurationProperties;

    public CorsConfiguration(CorsConfigurationProperties corsConfigurationProperties) {
        this.corsConfigurationProperties = corsConfigurationProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins(
                        corsConfigurationProperties.getAllowedOrigins())
                .allowedMethods("GET", "POST", "PUT")
                .exposedHeaders("Content-disposition")
                .allowCredentials(true);
    }

}
