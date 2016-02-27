package me.geso.paranoid_dns_resolver;

import java.util.Objects;

public class ParanoidAgentBlockedException extends RuntimeException {
    private static final long serialVersionUID = 9022632987462656058L;

    private final String host;

    public ParanoidAgentBlockedException(final String host) {
        super("Access denied by ParanoidAgent: " + host);
        this.host = Objects.requireNonNull(host);
    }

    public String getHost() {
        return host;
    }

    @Override
    public String toString() {
        return "ParanoidAgentBlockedException{" +
                "host='" + host + '\'' +
                '}';
    }
}
