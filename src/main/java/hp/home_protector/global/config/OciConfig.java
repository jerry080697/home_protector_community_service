package hp.home_protector.global.config;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class OciConfig {
    @Value("${oci.objectstorage.region}")
    private String region;

    @Bean
    public ObjectStorage objectStorageClient() {
        try {
            AuthenticationDetailsProvider provider =
                    new ConfigFileAuthenticationDetailsProvider("DEFAULT");
            ObjectStorageClient client = new ObjectStorageClient(provider);
            client.setRegion(Region.fromRegionId(region));

            return client;
        } catch (IOException e) {
            throw new IllegalStateException("OCI 설정 로드 실패", e);
        }
    }
}
