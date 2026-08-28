package anzihe.com.common_template.config;

import anzihe.com.common_template.utils.DateTimeFormatterUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;


@Slf4j
@Configuration
@EnableConfigurationProperties(JacksonProperties.class)
public class JacksonConfiguration{

    @Resource
    private JacksonProperties jacksonProperties;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer autoJackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            // 1. 基础环境配置 (时区、Locale)
            TimeZone timeZone = jacksonProperties.getTimeZone();
            if (timeZone == null) {
                timeZone = TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai"));
            }
            jacksonObjectMapperBuilder.timeZone(timeZone);

            Locale locale = jacksonProperties.getLocale();
            if (locale == null) {
                locale = Locale.CHINA;
            }
            jacksonObjectMapperBuilder.locale(locale);

            // 2. 日期格式化配置 (使用 DateTimeFormatterUtil)
            String dateFormat = this.jacksonProperties.getDateFormat();
            if (dateFormat == null) {
                dateFormat = DateTimeFormatterUtil.DATE_TIME_STR;
            }
            jacksonObjectMapperBuilder.simpleDateFormat(dateFormat);


            // 注册 Java8 时间模块的序列化器
            jacksonObjectMapperBuilder.serializers(
                    new LocalDateTimeSerializer(DateTimeFormatterUtil.DATE_TIME_FORMAT),
                    new LocalDateSerializer(DateTimeFormatterUtil.DATE_FORMAT),
                    new LocalTimeSerializer(DateTimeFormatterUtil.TIME_FORMAT));

            // 注册 Java8 时间模块的反序列化器
            jacksonObjectMapperBuilder.deserializers(
                    new LocalDateTimeDeserializer(DateTimeFormatterUtil.DATE_TIME_FORMAT),
                    new LocalDateDeserializer(DateTimeFormatterUtil.DATE_FORMAT),
                    new LocalTimeDeserializer(DateTimeFormatterUtil.TIME_FORMAT));

            // 3. 特性开关配置
            // 序列化：对象为空时不抛异常
            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            // 序列化：禁用时间戳，使用格式化字符串
            jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // 反序列化：遇到未知属性不抛异常
            jacksonObjectMapperBuilder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            log.info("Jackson 全局配置已加载: TimeZone={}, Locale={}", timeZone.getID(), locale);
            ////序列化处理
            ////是否允许出现未转义的制表符和换行符等(若出现了就会抛异常)
            //jacksonObjectMapperBuilder.featuresToEnable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
            ////是否支持反斜杠引用机制
            //jacksonObjectMapperBuilder.featuresToEnable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
            ////ALLOW_SINGLE_QUOTES(false, JsonParser.Feature.ALLOW_SINGLE_QUOTES)：是否允许单引号’包裹着也行，默认是不允许的（因为这不是JSON规范）
            //jacksonObjectMapperBuilder.featuresToEnable(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        };
    }




}
