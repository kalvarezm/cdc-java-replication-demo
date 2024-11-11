package com.nuamx.config.property;

import com.nuamx.entity.property.Database;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "databases")
@PropertySource(value = "classpath:databases-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
@Validated
public class DatabasesProperty {

    @NotNull
    private Database jobStore;
    @NotNull
    private Database source;
    @NotNull
    private List<Database> target;

}
