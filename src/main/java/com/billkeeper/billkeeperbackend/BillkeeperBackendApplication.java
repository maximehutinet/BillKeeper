package com.billkeeper.billkeeperbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneOffset;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class BillkeeperBackendApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        Locale.setDefault(Locale.ROOT);
        SpringApplication.run(BillkeeperBackendApplication.class, args);
    }
}
