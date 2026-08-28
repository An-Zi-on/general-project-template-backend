package anzihe.com.common_template.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DATE_TIME_PATTERN);
            builder.timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            builder.serializers(new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
            ));
            builder.deserializers(new LocalDateTimeDeserializer(
                DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
            ));
        };
    }
}