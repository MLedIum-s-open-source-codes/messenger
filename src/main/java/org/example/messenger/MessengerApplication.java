package org.example.messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class MessengerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MessengerApplication.class, args);
  }

}
