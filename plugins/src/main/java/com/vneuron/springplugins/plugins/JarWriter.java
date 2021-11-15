package com.vneuron.springplugins.plugins;

import com.vneuron.springplugins.api.WriterPlugin;
import org.springframework.stereotype.Component;

@Component
public class JarWriter implements WriterPlugin {

    @Override
    public void write(String message) {
        System.out.println("Supposedly Writing through Jar plugin:");
        System.out.println("JAR message:" + message);
    }

    @Override
    public boolean supports(String s) {
        return s.equalsIgnoreCase("jar");
    }
}
