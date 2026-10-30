package com.shop.config;

import com.shop.seed.Seeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataSeederRunner implements ApplicationRunner {

    private final Seeder seeder;

    public DataSeederRunner(Seeder seeder) {
        this.seeder = seeder;
    }

    @Override
    public void run(ApplicationArguments args) {
        int count = seeder.seed();
        log.info("Данные залиты: {} товаров", count);
    }
}
