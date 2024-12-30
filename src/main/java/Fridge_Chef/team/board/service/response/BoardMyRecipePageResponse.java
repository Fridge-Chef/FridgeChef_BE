package Fridge_Chef.team.board.service.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.user.domain.UserId;

import java.time.LocalDateTime;

public record BoardMyRecipePageResponse(SortType sortType, Long boardId, String title, String userName, String mainImage, Long mainImageId, double star, int hit, boolean myHit, boolean myMe, int click, LocalDateTime createTime) {


    public static BoardMyRecipePageResponse ofEntity(SortType sortType, Board entity, UserId userId) {
        String link = "";
        if (entity.getMainImage() != null && entity.getMainImage().getType() != null) {
            link = entity.getMainImageLink();
        }
        return new BoardMyRecipePageResponse(
                sortType,
                entity.getId(),
                entity.getTitle(),
                entity.getUser().getUsername(),
                link,
                entity.getMainImageId(),
                entity.getTotalStar(),
                entity.getHit(),
                entity.getIsMyHit(userId),
                entity.getUser().getUserId().equals(userId),
                entity.getCount(),
                entity.getCreateTime()
        );
    }
}
