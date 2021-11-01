package com.github.rafaelsilvestri.sequence.config;

import net.spy.memcached.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MemcachedConfig {

    @Bean
    public MemcachedClient memcachedClient(@Value("${memcached.servers}") final String cacheServer,
                                           @Value("${memcached.timeout}") final int cacheTimeout) throws IOException {
        final ConnectionFactoryBuilder connectionFactoryBuilder = new ConnectionFactoryBuilder()
                .setClientMode(ClientMode.Static) // only for aws elasticache-java-cluster-client
                .setHashAlg(DefaultHashAlgorithm.KETAMA_HASH)
                .setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT)
                .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                .setFailureMode(FailureMode.Redistribute)
                .setUseNagleAlgorithm(false)
                .setOpTimeout(cacheTimeout);

        MemcachedClient client = new MemcachedClient(connectionFactoryBuilder.build(),
                AddrUtil.getAddresses(cacheServer));
        return client;
    }
}
