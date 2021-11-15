package com.vneuron.springplugins;

import org.springframework.stereotype.Component;

@Component
public class TXTWriter implements WriterPlugin {

    @Override
    public void write(String message) {
        System.out.println("Supposedly Writting to text file:");
        System.out.println("TXT message:" + message);
    }

    @Override
    public boolean supports(String s) {
        return s.equalsIgnoreCase("txt");
    }
}
