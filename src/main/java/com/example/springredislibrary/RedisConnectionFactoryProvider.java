package com.example.springredislibrary;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConnectionFactoryProvider {

    @Value("${redis.config.info.cluster.enable:false}")
    private boolean clusterEnable;

    @Value("${redis.config.info.host:}")
    private String host;

    @Value("${redis.config.info.port:0}")
    private Integer port;


    @Value("${redis.config.info.password:}")
    private String password;

    @Value("${redis.config.info.username:}")
    private String username;


    public LettuceConnectionFactory getConnectionFactory() {
        RedisConfiguration configuration;
        if (clusterEnable) {
            configuration = getRedisClusterConfiguration();
        } else {
            configuration = getRedisStandaloneConfiguration();
        }
        return new LettuceConnectionFactory(configuration, getLettuceClientConfiguration());
    }


    protected RedisClusterConfiguration getRedisClusterConfiguration() {
        RedisClusterConfiguration configuration = new RedisClusterConfiguration();
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.clusterNode(host, port);
        return configuration;
    }

    protected RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.setHostName(host);
        configuration.setPort(port);
        return configuration;
    }

    protected LettuceClientConfiguration getLettuceClientConfiguration() {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
        if (clusterEnable) {
            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    .enableAllAdaptiveRefreshTriggers()
                    .build();
            ClientOptions clientOptions = ClusterClientOptions.builder()
                    .topologyRefreshOptions(topologyRefreshOptions)
                    .build();
            builder.clientOptions(clientOptions);
        }

        return builder.build();
    }

}
