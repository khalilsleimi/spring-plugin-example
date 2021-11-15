package com.vneuron.springplugins.app;

import com.vneuron.springplugins.api.WriterPlugin;
import com.vneuron.springplugins.app.classloader.PluginClassloader;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@SpringBootApplication
@EnablePluginRegistries(WriterPlugin.class)
public class SpringPluginsApplication {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        PluginClassloader classLoader = new PluginClassloader("plugins", Thread.currentThread().getContextClassLoader());
        SpringApplication app = new SpringApplication(SpringPluginsApplication.class);
        app.setResourceLoader(new DefaultResourceLoader(classLoader));
        context = app.run(args);
    }

    @Bean
    ApplicationRunner runner(PluginRegistry <WriterPlugin, String> plugins) {
        return args -> {
            for (var format : "csv,txt".split(","))
                plugins.getPluginFor(format).get().write("Hello, Spring Plugin!");
        };
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(SpringPluginsApplication.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

}
