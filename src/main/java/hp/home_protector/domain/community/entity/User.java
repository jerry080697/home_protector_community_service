package hp.home_protector.domain.community.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("users")
public class User {
    @Id
    private ObjectId userId;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String gender; // "male"/"female"/"other"
    @Indexed(unique = true)
    private String email;
    private String profileImage;
    private String job;
    private double income;
    private LocalDateTime moveInDate;
    private int quizScore;
    private int quizRank;
    private String role; // "USER", "ADMIN" 등
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
