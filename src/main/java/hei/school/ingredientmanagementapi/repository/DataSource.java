package hei.school.ingredientmanagementapi.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSource {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String user;

  @Value("${spring.datasource.password}")
  private String password;

  @Bean
  public Connection getConnection() {

    try {
      return DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      throw new RuntimeException("Failed to connect to the database", e);
    }

  }
}
