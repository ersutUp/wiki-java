package xyz.ersut.service.account.client.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import xyz.ersut.service.account.client.fallback.Flag;

@AutoConfiguration
@ComponentScan(basePackageClasses = Flag.class)
public class FallbackBeanAutoConfiguration {

}
