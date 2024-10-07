package Fridge_Chef.team.comment.rest.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CommentUpdateRequest(String comment,
                                   boolean isImage,
                                   List<MultipartFile> image,
                                   double star) {
}
