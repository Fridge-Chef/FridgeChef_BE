package Fridge_Chef.team.comment.rest.request;

import org.springframework.web.multipart.MultipartFile;

public record CommentUpdateRequest(String comment, MultipartFile image, double star) {
}
