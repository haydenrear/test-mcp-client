package com.hayden.testmcpclient.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.utilitymodule.stream.StreamUtil;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@Configuration
public class DoTestRun {

    @Autowired
    @Lazy
    ApplicationContext applicationContext;

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

//            var called = m.callTool(new McpSchema.CallToolRequest("redeploy-mcp-server", Map.of("service_name", "commit-diff-context-mcp")));
//            log.info("{}", called.isError());
//            log.info("{}", called);
//            var list = m.listTools();
//            log.info("{} here are the tools.", list.tools().stream().map(McpSchema.Tool::name).collect(Collectors.joining(" ,")));
//            log.info("{}", list.tools().stream().anyMatch(t -> t.name().equals("redeploy-mcp-server")));
//            log.info("{}", list.tools().stream().anyMatch(t -> t.name().equals("test-mcp-server.zoom_trace")));
            var f= """
                            {
                              "filePath": "commit-diff-context-parent/commit-diff-context-mcp/src/main/java/com/hayden/commitdiffcontextmcp/tools/CodeSearchMcpTools.java",
                              "zoomType": "CREATE",
                              "targetSymbol": "zoomTrace",
                              "additionalLines": 3,
                              "maxSize": 10
                            }
                    """;
            var tools = m.listTools();
            var t = new ObjectMapper().readValue(f, new TypeReference<Map<String, Object>>() {});

            var called = m.callTool(new McpSchema.CallToolRequest("commit-diff-context-mcp-zoomTrace", t));

            var cached = StreamUtil.toStream(Files.list(Paths.get(".cache")))
                            .toList();

            log.info("Here are cached files:\n{}", cached);

            m.closeGracefully();
            SpringApplication.exit(applicationContext);
            System.exit(0);
        };
    }

}
