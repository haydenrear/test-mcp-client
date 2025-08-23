package com.hayden.testmcpclient;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class TestMcpClientApplicationTests {

    @Autowired
    private List<McpSyncClient> mcpSyncClients;
    @Test
    void contextLoads() {
        assertThat(mcpSyncClients.size()).isEqualTo(1);

        var m = mcpSyncClients.getFirst();
        await().during(Duration.ofSeconds(1))
                .atMost(Duration.ofSeconds(45))
                .until(() -> {
                    var tools = m.listTools();
                    return tools.tools().size() > 1;
                });

        var list = m.listTools();
        assertThat(list.tools().stream().anyMatch(t -> t.name().equals("redeploy-mcp-server"))).isTrue();
        assertThat(list.tools().stream().anyMatch(t -> t.name().equals("test-mcp-server.doSomething"))).isTrue();
    }

}
