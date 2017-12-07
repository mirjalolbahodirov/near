package uz.algo.near.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private static final Integer CACHE_PERIOD = 86400*10;

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/", "classpath:/META-INF/resources/webjars/"};

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/static/assets/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS).setCachePeriod(CACHE_PERIOD);
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS).setCachePeriod(CACHE_PERIOD);
    }

}
