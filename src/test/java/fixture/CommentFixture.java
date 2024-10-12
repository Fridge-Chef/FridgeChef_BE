package fixture;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.user.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommentFixture {
    private final static Random random = new Random();

    public static Comment create(Board board, User user) {
        return new Comment(board, user,List.of(ImageFixture.create()), "댓글 내용", 3.5).updateId(2L);
    }

    public static List<Comment> creates(int size, Board board, List<User> users) {
        List<String> commentContents = getComment();
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            User user = users.get(random.nextInt(users.size()));
            String content = commentContents.get(random.nextInt(commentContents.size()));
            double starRating = 0.5 * (random.nextInt(9) + 2);
            comments.add(new Comment(board, user, List.of(ImageFixture.create()), content, starRating));
        }
        return comments;
    }

    private static List<String> getComment() {
        return List.of(
                "정말 유용한 정보네요!", "잘 보고 갑니다.", "좋은 글 감사합니다.", "이 글 정말 좋네요!", "유익한 내용이네요.",
                "감사합니다.", "정말 도움 됐어요.", "이 레시피 꼭 써먹을게요!", "좋은 정보 감사합니다.", "매번 감사합니다.",
                "많은 도움이 되었어요.", "앞으로도 기대할게요!", "꼭 해봐야겠어요!", "이 글 정말 유익해요.", "덕분에 많은 정보 얻었어요.",
                "완전 유용하네요!", "도움이 많이 되었어요.", "글 잘 봤어요.", "덕분에 문제 해결했어요.", "정말 감사합니다!",
                "알찬 정보네요.", "정말 유용한 팁이에요.", "감사합니다, 많은 도움 됐어요.", "또 올게요!", "글 잘 보고 갑니다!",
                "정말 많은 도움이 되었어요.", "또 배우고 갑니다.", "훌륭한 정보 감사합니다.", "계속 참고할게요!", "레시피 정말 좋아요!"
        );
    }
}
