package hp.home_protector.global.config;

import hp.home_protector.domain.community.repository.PostRepository;
import hp.home_protector.domain.community.service.PostIndexService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    private final PostRepository postRepository;
    private final PostIndexService postIndexService;

    public DataInitializer(PostRepository postRepository,
                           PostIndexService postIndexService) {
        this.postRepository   = postRepository;
        this.postIndexService = postIndexService;
    }

    @Override
    public void run(ApplicationArguments args) {
        postRepository.findAll().forEach(postIndexService::index);
    }
}
