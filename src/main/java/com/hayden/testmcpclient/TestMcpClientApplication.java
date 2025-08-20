package com.hayden.testmcpclient;

import org.springframework.ai.mcp.client.autoconfigure.McpClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class })
public class TestMcpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestMcpClientApplication.class, args);
    }

}
