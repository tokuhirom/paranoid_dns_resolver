package me.geso.paranoid_dns_resolver;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParanoidDnsResolverTest {
    private final ParanoidDnsResolver paranoidDnsResolver = new ParanoidDnsResolver();
    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(0);

    @Test
    public void testBuild() throws Exception {
        Assertions.assertThatThrownBy(() -> {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setConnectionManager(
                            new PoolingHttpClientConnectionManager(
                                    RegistryBuilder
                                            .<ConnectionSocketFactory>create()
                                            .register("http",
                                                    PlainConnectionSocketFactory
                                                            .getSocketFactory())
                                            .register("https",
                                                    SSLConnectionSocketFactory
                                                            .getSocketFactory())
                                            .build(), null,
                                    new ParanoidDnsResolver()))
                    .build()) {
                HttpGet httpGet = new HttpGet("http://127.0.0.1:" + wireMockRule.port());
                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    System.out.println(response.getStatusLine());
                }
            }
        }).isInstanceOf(ParanoidAgentBlockedException.class);
    }

    private void assertBlocked(String host) {
        Assertions.assertThatThrownBy(() -> paranoidDnsResolver.resolve(host))
                .isInstanceOf(ParanoidAgentBlockedException.class);
    }

    @Test
    public void testResolver() throws Exception {
        // success case.
        assertThat(paranoidDnsResolver.resolve("google.com").length)
                .isGreaterThan(0);

        assertBlocked("127.0.0.1");

        // blocked domains.
        // See http://cpansearch.perl.org/src/SAXJAZMAN/LWPx-ParanoidAgent-1.10/t/00-all.t
        assertBlocked("0x7f.1");
        assertBlocked("0x7f.0xffffff");
        assertBlocked("037777777777"); // → 255.255.255.255.
        assertBlocked("192.052000001");
        assertBlocked("0x00.00");
        assertBlocked("192.0.2.13");
        assertBlocked("192.88.99.77");
        // assertBlocked("012.1.2.1"); // Java の InetAddress は、012.1.2.1 を 12.1.2.1 と解釈するので通らない。
        assertBlocked("167838209");
        assertBlocked("10.2.3.4");
        assertBlocked("LOCALhost");
        assertBlocked("10.0.0.1.xip.io");
        assertBlocked("127.0.0.1.xip.io");

        // IPv6
        assertBlocked("::1");
    }
}