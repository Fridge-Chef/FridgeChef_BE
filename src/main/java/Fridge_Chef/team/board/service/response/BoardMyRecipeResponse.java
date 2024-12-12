package Fridge_Chef.team.board.service.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardIssue;
import Fridge_Chef.team.recipe.domain.Difficult;
import Fridge_Chef.team.user.domain.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
public class BoardMyRecipeResponse {
    private String title;
    private String username;
    private boolean myMe;
    private String description;
    private double rating;
    private int hitTotal;
    private int starTotal;
    private String mainImage;
    private Long mainImageId;
    private String issueInfo;
    private String dishTime;
    private String dishLevel;
    private String dishCategory;

    private List<OwnedIngredientResponse> ownedIngredients;
    private List<RecipeIngredientResponse> recipeIngredients;
    private List<StepResponse> instructions;
    private Long boardId;


    @Getter
    @AllArgsConstructor
    public static class OwnedIngredientResponse {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class RecipeIngredientResponse {
        private Long id;
        private String name;
        private String details;
    }

    @Getter
    @AllArgsConstructor
    public static class StepResponse {
        private Long id;
        private String content;
        private String imageLink;
    }

    public BoardMyRecipeResponse(String title, boolean myMe, String username, String intro, double rating, int hitTotal, int starTotal, String mainImage, Long imageId, String issueInfo, String dishTime, String dishLevel, String dishCategory, List<OwnedIngredientResponse> ownedIngredients, List<RecipeIngredientResponse> recipeIngredients, List<StepResponse> instructions, Long boardId) {
        this.title = title;
        this.username = username;
        this.myMe = myMe;
        this.rating = rating;
        this.description = intro;
        this.hitTotal = hitTotal;
        this.starTotal = starTotal;
        this.mainImage = mainImage;
        this.mainImageId = imageId;
        this.issueInfo = issueInfo;
        this.dishTime = dishTime;
        this.dishLevel = dishLevel;
        this.dishCategory = dishCategory;
        this.ownedIngredients = ownedIngredients;
        this.recipeIngredients = recipeIngredients;
        this.instructions = instructions;
        this.boardId = boardId;
    }

    public static BoardMyRecipeResponse of(Board board, Optional<UserId> userId) {
        var ownedIngredients = board.getContext().getBoardIngredients().stream()
                .map(ingredient -> new OwnedIngredientResponse(ingredient.getId(), ingredient.getIngredient().getName()))
                .collect(Collectors.toList());

        var recipeIngredients = board.getContext().getBoardIngredients().stream()
                .map(ingredient -> new RecipeIngredientResponse(ingredient.getIngredient().getId(),
                        ingredient.getIngredient().getName(),
                        ingredient.getQuantity() == null ? "" : ingredient.getQuantity()))
                .collect(Collectors.toList());


        var instructions = board.getContext().getDescriptions()
                .stream()
                .map(step -> new StepResponse(step.getId(),step.getDescription(), step.getLink()))
                .collect(Collectors.toList());


        List<BoardIssue> boardIssues = board.getBoardIssues();
        String issueInfo = generateIssueInfo(boardIssues);
        Difficult diff = Difficult.of(board.getContext().getDishLevel());
        String level = diff.getValue();
        boolean myMe = false;
        if (userId.isPresent()) {
            if (userId.get().equals(board.getUser().getUserId())) {
                myMe = true;
            }
        }

        return new BoardMyRecipeResponse(board.getTitle(),
                myMe,
                board.getUser().getUsername(),
                board.getIntroduction(),
                board.getTotalStar(),
                board.hitTotalCount(),
                board.starTotalCount(),
                board.getMainImageLink(),
                board.getMainImageId(),
                issueInfo,
                board.getContext().getDishTime(),
                level,
                board.getContext().getDishCategory(),
                ownedIngredients,
                recipeIngredients,
                instructions,
                board.getId());
    }

    private static String generateIssueInfo(List<BoardIssue> boardIssues) {
        if (boardIssues == null || boardIssues.isEmpty()) {
            return "";
        }
        LocalDateTime now = LocalDateTime.now();
        boolean isWeek = boardIssues.stream().anyMatch(issue -> isThisWeek(issue.getCreateTime(), now));
        boolean isMoon = boardIssues.stream().anyMatch(issue -> isThisMonth(issue.getCreateTime(), now));
        if (isWeek) {
            return "이주의 레시피";
        }
        if (isMoon) {
            return "이달의 레시피";
        }
        return "";
    }

    private static boolean isThisMonth(LocalDateTime issueTime, LocalDateTime now) {
        return !issueTime.isBefore(now.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS));
    }

    private static boolean isThisWeek(LocalDateTime issueTime, LocalDateTime now) {
        return !issueTime.isBefore(now.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS));
    }
}
