package qwerty.chaekit.global.config;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import qwerty.chaekit.global.properties.AwsProperties;

@Configuration
@RequiredArgsConstructor
public class FileUploadConfig {
    private final AwsProperties awsProperties;
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        DataSize ebookMaxFileSize = DataSize.of(awsProperties.ebookMaxFileSize(), DataUnit.BYTES);
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(ebookMaxFileSize);
        factory.setMaxRequestSize(ebookMaxFileSize);
        return factory.createMultipartConfig();
    }
}