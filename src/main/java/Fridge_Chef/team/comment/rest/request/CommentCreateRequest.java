package Fridge_Chef.team.comment.rest.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CommentCreateRequest(String comment, List<MultipartFile> images, double star) {
}
