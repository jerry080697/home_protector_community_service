package hp.home_protector.global.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("prod")
public class MongoDualTemplateConfig {

    // 1) 쓰기용(기본) MongoDB URI (VM2)
    @Value("${spring.data.mongodb.uri}")
    private String writeMongoUri;

    // 2) 읽기 전용 MongoDB URI (VM4)
    @Value("${custom.mongodb.read-uri}")
    private String readMongoUri;

    /**
     * 기본(default) MongoClient → 쓰기(INSERT/UPDATE/DELETE) 용
     * 스프링이 @Primary로 지정된 MongoTemplate을 자동 주입하므로,
     * 기존 Repository / MongoTemplate을 그대로 사용해서 쓰기가 이루어집니다.
     */
    @Bean
    @Primary
    public MongoClient primaryMongoClient() {
        return MongoClients.create(writeMongoUri);
    }

    @Bean(name = "primaryMongoTemplate")
    @Primary
    public MongoTemplate primaryMongoTemplate(@Qualifier("primaryMongoClient") MongoClient client) {
        // homeprotector 데이터베이스 이름은 URI에서 이미 포함되었다고 가정
        return new MongoTemplate(client, "homeprotector");
    }

    /**
     * 읽기 전용(read-only) MongoClient → 조회(SELECT) 용
     */
    @Bean
    public MongoClient readMongoClient() {
        return MongoClients.create(readMongoUri);
    }

    @Bean(name = "readMongoTemplate")
    public MongoTemplate readMongoTemplate(@Qualifier("readMongoClient") MongoClient client) {
        return new MongoTemplate(client, "homeprotector");
    }
}
