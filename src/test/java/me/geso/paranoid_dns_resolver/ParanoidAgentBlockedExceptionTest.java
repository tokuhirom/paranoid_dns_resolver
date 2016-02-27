package me.geso.paranoid_dns_resolver;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParanoidAgentBlockedExceptionTest {
    @Test
    public void getHost() throws Exception {
        ParanoidAgentBlockedException e = new ParanoidAgentBlockedException("example.com");
        assertThat(e.getHost())
                .isEqualTo("example.com");
    }
}