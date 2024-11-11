package com.nuamx.config.property;

import com.nuamx.entity.property.PartitionJob;
import com.nuamx.entity.property.CacheJob;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "jobs")
@PropertySource(value = "classpath:jobs-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
@Validated
public class JobsProperty {

    private String csvPath;
    @NotNull
    private List<PartitionJob> partitions;

}
