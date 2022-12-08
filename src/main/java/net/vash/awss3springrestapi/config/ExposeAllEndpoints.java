package net.vash.awss3springrestapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Slf4j
public class ExposeAllEndpoints implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Method start");
        event
                .getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods()
                .forEach(
                        (requestMappingInfo, handlerMethod) -> {
                            log.info("mapping info: {}", requestMappingInfo);
                            log.info("handler method: {}", handlerMethod);
                        }
                );
        log.info("Method end");
    }
}
