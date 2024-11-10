package Fridge_Chef.team.board.service.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardIssue;
import Fridge_Chef.team.recipe.domain.Difficult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
public class BoardMyRecipeResponse {
    private String title;
    private String username;
    private String intro;
    private double rating;
    private int hitTotal;
    private int starTotal;
    private String mainImage;
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
        private String content;
        private String imageLink;
    }

    public BoardMyRecipeResponse(String title, String username, String intro,double rating, int hitTotal, int starTotal, String mainImage, String issueInfo, String dishTime, String dishLevel, String dishCategory, List<OwnedIngredientResponse> ownedIngredients, List<RecipeIngredientResponse> recipeIngredients, List<StepResponse> instructions, Long boardId) {
        this.title = title;
        this.username = username;
        this.rating = rating;
        this.intro=intro;
        this.hitTotal = hitTotal;
        this.starTotal = starTotal;
        this.mainImage = mainImage;
        this.issueInfo = issueInfo;
        this.dishTime = dishTime;
        this.dishLevel = dishLevel;
        this.dishCategory = dishCategory;
        this.ownedIngredients = ownedIngredients;
        this.recipeIngredients = recipeIngredients;
        this.instructions = instructions;
        this.boardId = boardId;
    }

    public static BoardMyRecipeResponse of(Board board) {
        System.out.println("---of "+board.toString());
        var ownedIngredients = board.getContext().getBoardIngredients().stream()
                .map(ingredient -> new OwnedIngredientResponse(ingredient.getId(), ingredient.getIngredient().getName()))
                .collect(Collectors.toList());

        var recipeIngredients = board.getContext().getBoardIngredients().stream()
                .map(ingredient -> new RecipeIngredientResponse(ingredient.getIngredient().getId(), ingredient.getIngredient().getName(), ingredient.getQuantity()))
                .collect(Collectors.toList());

        var instructions = board.getContext().getDescriptions().stream()
                .map(step -> new StepResponse(step.getDescription(), step.getLink()))
                .collect(Collectors.toList());

        List<BoardIssue> boardIssues = board.getBoardIssues();
        String issueInfo = generateIssueInfo(boardIssues);

        return new BoardMyRecipeResponse(board.getTitle(),
                board.getUser().getUsername(),
                board.getIntroduction(),
                board.getTotalStar(),
                board.hitTotalCount(),
                board.starTotalCount(),
                board.getMainImageLink(),
                issueInfo,
                board.getContext().getDishTime(),
                board.getContext().getDishLevel(),
                board.getContext().getDishCategory(),
                ownedIngredients,
                recipeIngredients,
                instructions,
                board.getId());
    }

    private static String level(String level){
        Difficult difficult = Difficult.of(level);
        return difficult.getValue();
    }
    private static String generateIssueInfo(List<BoardIssue> boardIssues) {
        if (boardIssues == null || boardIssues.isEmpty()) {
            return "";
        }
        LocalDateTime now = LocalDateTime.now();
        return boardIssues.stream().anyMatch(issue -> isThisMonth(issue.getCreateTime(), now))
                ? "이달의 레시피"
                : boardIssues.stream().anyMatch(issue -> isThisWeek(issue.getCreateTime(), now))
                ? "이주의 레시피"
                : "";
    }

    private static boolean isThisMonth(LocalDateTime issueTime, LocalDateTime now) {
        return !issueTime.isBefore(now.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS));
    }

    private static boolean isThisWeek(LocalDateTime issueTime, LocalDateTime now) {
        return !issueTime.isBefore(now.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS));
    }
}
