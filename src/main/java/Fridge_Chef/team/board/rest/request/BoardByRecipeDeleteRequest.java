package Fridge_Chef.team.board.rest.request;

import jakarta.validation.constraints.NotBlank;

public record BoardByRecipeDeleteRequest(
        @NotBlank(message = "게시글 ID 는 필수 입니다.")
        Long id) {
}
