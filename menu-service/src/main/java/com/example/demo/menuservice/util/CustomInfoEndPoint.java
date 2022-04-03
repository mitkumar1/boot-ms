package com.example.demo.menuservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomInfoEndPoint implements InfoContributor {

    @Autowired
    private ConfigurableApplicationContext ctx;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        details.put("bean-definition-count", ctx.getBeanDefinitionCount());
        details.put("startup-date", ctx.getStartupDate());

        builder.withDetail("context", details);
    }
}
