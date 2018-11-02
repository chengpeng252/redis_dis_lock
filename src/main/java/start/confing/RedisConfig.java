package start.confing;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching//¿ªÆô×¢½â
public class RedisConfig {
    @Bean
    public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory factory) {
      RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
      redisTemplate.setConnectionFactory(factory);
      RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
      redisTemplate.setKeySerializer(stringRedisSerializer);
      return redisTemplate;
    }
}
