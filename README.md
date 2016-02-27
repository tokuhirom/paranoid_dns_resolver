# paranoid_dns_resolver for Java

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.geso/paranoid_dns_resolver/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.geso/paranoid_dns_resolver)
[![Build Status](https://travis-ci.org/tokuhirom/paranoid_dns_resolver.svg?branch=master)](https://travis-ci.org/tokuhirom/paranoid_dns_resolver)

The paranoid_dns_resolver is a class subclassing `org.apache.http.impl.conn.SystemDefaultDnsResolver`,
but paranoid against attackers. It's to be used when you're fetching a remote resource on behalf of a possibly malicious user.

## SYNOPSIS

    try (CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setConnectionManager(
                    new PoolingHttpClientConnectionManager(
                            RegistryBuilder
                                    .<ConnectionSocketFactory>create()
                                    .register("http",  PlainConnectionSocketFactory.getSocketFactory())
                                    .register("https", SSLConnectionSocketFactory.getSocketFactory())
                                    .build(), null,
                            new ParanoidDnsResolver()))
            .build()) {
        HttpGet httpGet = new HttpGet("http://127.0.0.1");
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            System.out.println(response.getStatusLine());
        }
    }

If the url contains such as loopback address, private address, multicast address, it throws `me.geso.paranoid_dns_resolver.ParanoidAgentBlockedException`
exception.

## Requirements

Java 8, Apache HttpClient 4.5.2+

## See Also

 * http://search.cpan.org/~bradfitz/LWPx-ParanoidAgent-1.02/lib/LWPx/ParanoidAgent.pm

## LICENSE

    The MIT License (MIT)
    Copyright © 2016 Tokuhiro Matsuno, http://64p.org/ <tokuhirom@gmail.com>

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the “Software”), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.

