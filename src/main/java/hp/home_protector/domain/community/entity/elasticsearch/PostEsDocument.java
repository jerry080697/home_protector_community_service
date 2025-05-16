package hp.home_protector.domain.community.entity.elasticsearch;

import hp.home_protector.domain.community.entity.BoardType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;
import java.util.List;

@Document(indexName = "posts")
@Setting(settingPath = "/es-settings.json")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PostEsDocument {
    @Id
    private String postId;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Keyword)
    private BoardType boardType;

    @Field(type = FieldType.Text,
            analyzer = "nori",
            searchAnalyzer = "nori")
    private String title;

    @Field(type = FieldType.Text,
            analyzer = "nori",
            searchAnalyzer = "nori")
    private String content;

    @Field(type = FieldType.Keyword)
    private List<String> attachments;

    @Field(type = FieldType.Integer)
    private int likeCount;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private OffsetDateTime createdAt;
}
