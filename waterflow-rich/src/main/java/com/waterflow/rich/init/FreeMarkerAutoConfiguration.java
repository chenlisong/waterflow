package com.waterflow.rich.init;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

@Configuration
@ConditionalOnClass({ freemarker.template.Configuration.class,
        FreeMarkerConfigurationFactory.class })
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(FreeMarkerProperties.class)
public class FreeMarkerAutoConfiguration {
}