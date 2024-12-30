package Fridge_Chef.team.comment.rest.request;

import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CommentCreateRequest(
        @Size(max = 300,message = "댓글 입력 최대 글자수는 300 입니다.") String comment,
        List<MultipartFile> images,
        double star) {
}
