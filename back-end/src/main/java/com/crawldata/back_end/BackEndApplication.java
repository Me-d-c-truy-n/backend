package com.crawldata.back_end;

import com.crawldata.back_end.utils.AppUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class BackEndApplication {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(BackEndApplication.class, args);
		AppUtils.doLoad();
	}
}
