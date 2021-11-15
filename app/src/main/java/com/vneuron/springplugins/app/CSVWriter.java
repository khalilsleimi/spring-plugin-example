package com.vneuron.springplugins.app;

import com.vneuron.springplugins.api.WriterPlugin;
import org.springframework.stereotype.Component;

@Component
public class CSVWriter implements WriterPlugin {

    @Override
    public void write(String message) {
        System.out.println("Supposedly Writting to CSV file:");
        System.out.println("CSV message:" + message);
    }

    @Override
    public boolean supports(String s) {
        return s.equalsIgnoreCase("csv");
    }
}
