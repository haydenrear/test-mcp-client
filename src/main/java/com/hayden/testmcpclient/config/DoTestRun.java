package com.hayden.testmcpclient.config;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@Configuration
public class DoTestRun {

    @Bean
    public CommandLineRunner commandLineRunner(List<McpSyncClient> mcpSyncClients) {
        return args -> {

            log.info("{}", mcpSyncClients.size());

            var m = mcpSyncClients.getFirst();
            await().during(Duration.ofSeconds(5))
                    .atMost(Duration.ofSeconds(240))
                    .until(() -> {
                        var tools = m.listTools();
                        return tools.tools().size() > 1;
                    });

            var called = m.callTool(new McpSchema.CallToolRequest("redeploy-mcp-server", Map.of("service_name", "commit-diff-context-mcp")));
            log.info("{}", called.isError());
            log.info("{}", called);
            var list = m.listTools();
            log.info("{} here are the tools.", list.tools().stream().map(McpSchema.Tool::name).collect(Collectors.joining(" ,")));
            log.info("{}", list.tools().stream().anyMatch(t -> t.name().equals("redeploy-mcp-server")));
            log.info("{}", list.tools().stream().anyMatch(t -> t.name().equals("test-mcp-server.zoom_trace")));

            m.closeGracefully();

            System.exit(0);
        };
    }

}
