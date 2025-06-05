package hp.home_protector.global.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * MongoDB 연결을 Read/Write 용으로 분리해서 사용할 수 있도록 설정합니다.
 */
@Configuration
public class MongoConfig {

    /**
     * application.yml 에 정의된 쓰기용 MongoDB URI (VM2: 10.0.51.19)
     * spring.data.mongodb.uri 로 바인딩되어 기본 writeMongoClient 에 사용됨.
     */
    @Value("${spring.data.mongodb.uri}")
    private String writeMongoUri;

    /**
     * application.yml 에 정의한 읽기용 MongoDB URI (VM4: 10.0.50.136)
     * custom.mongodb.read-uri 로 읽어 옵니다.
     */
    @Value("${custom.mongodb.read-uri}")
    private String readMongoUri;

    /**
     * ⚙️ 쓰기용 MongoClient (기본 MongoTemplate 에 바인딩됨)
     */
    @Bean
    public MongoClient writeMongoClient() {
        return MongoClients.create(writeMongoUri);
    }

    /**
     * ⚙️ 쓰기용 MongoTemplate (트랜잭션 혹은 save/delete 등의 작업에 사용)
     * 스프링부트가 내부적으로 쓰는 MongoTemplate 과 동일하게 이름을 지어 주면,
     * @Autowired 로 주입 시 다른 이름과 충돌 안 나도록 primary 로 등록할 수 있습니다.
     */
    @Bean(name = "writeMongoTemplate")
    public MongoTemplate writeMongoTemplate() {
        return new MongoTemplate(writeMongoClient(), "homeprotector");
    }

    /**
     * ⚙️ 읽기용 MongoClient (조회(SELECT) 전용)
     */
    @Bean
    public MongoClient readMongoClient() {
        return MongoClients.create(readMongoUri);
    }

    /**
     * ⚙️ 읽기용 MongoTemplate (조회 전용)
     * 필요한 곳에서 @Qualifier("readMongoTemplate") 로 명시적으로 주입해서 사용하세요.
     */
    @Bean(name = "readMongoTemplate")
    public MongoTemplate readMongoTemplate() {
        return new MongoTemplate(readMongoClient(), "homeprotector");
    }
}
