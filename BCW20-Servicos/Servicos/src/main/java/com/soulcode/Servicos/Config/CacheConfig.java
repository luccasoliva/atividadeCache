package com.soulcode.Servicos.Config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {
    // converter de json para redis e vice-versa
    private final RedisSerializationContext.SerializationPair<Object> serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {// customizar a config padrão do redis cache
        return RedisCacheConfiguration
                .defaultCacheConfig() // customizar informações padrões
                .entryTtl(Duration.ofMinutes(5)) // todos os caches terão 5 min por padrão (tempo de vida)
                .disableCachingNullValues() // não salva valores nulos
                .serializeValuesWith(serializationPair); // converte do redis p/ json e vice-versa
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("clientesCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(30))
                                .serializeValuesWith(serializationPair)
                ).withCacheConfiguration("chamadosCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(10))
                                .serializeValuesWith(serializationPair)
                ).withCacheConfiguration("userServiceCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(5))
                                .serializeValuesWith(serializationPair)
                ).withCacheConfiguration("authCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofHours(1))
                                .serializeValuesWith(serializationPair)
                );
    }
}
