package Fridge_Chef.team.user.rest.request;

import org.springframework.web.multipart.MultipartFile;

public record UserProfileImageUpdateRequest(MultipartFile picture) {
}
