package Fridge_Chef.team.comment.rest.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public record CommentUpdateRequest(String comment, boolean isImage, ArrayList<MultipartFile> image, double star) {
}
