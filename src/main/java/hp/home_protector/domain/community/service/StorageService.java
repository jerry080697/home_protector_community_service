package hp.home_protector.domain.community.service;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StorageService {
    private final ObjectStorage client;
    private final String namespace;
    private final String bucket;
    private final String region;

    public StorageService(
            ObjectStorage client,
            @Value("${oci.objectstorage.namespace}") String namespace,
            @Value("${oci.objectstorage.bucket}")    String bucket,
            @Value("${oci.objectstorage.region}")    String region
    ) {
        this.client    = client;
        this.namespace = namespace;
        this.bucket    = bucket;
        this.region    = region;
    }

    public List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream().map(file -> {
            String objectName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            try (InputStream in = file.getInputStream()) {
                PutObjectRequest request = PutObjectRequest.builder()
                        .namespaceName(namespace)
                        .bucketName(bucket)
                        .objectName(objectName)
                        .contentType(file.getContentType())
                        .putObjectBody(in)
                        .contentLength(file.getSize())
                        .build();
                client.putObject(request);

                return String.format(
                        "https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                        region, namespace, bucket, objectName
                );
            } catch (IOException e) {
                throw new UncheckedIOException(
                        String.format("OCI 업로드 실패 (bucket=%s, file=%s)", bucket, file.getOriginalFilename()),
                        e
                );
            }
        }).collect(Collectors.toList());
    }
}