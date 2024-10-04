package Fridge_Chef.team.common;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.board.repository.ContextRepository;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.comment.repository.CommentRepository;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.repository.UserHistoryRepository;
import Fridge_Chef.team.user.repository.UserRepository;
import fixture.BoardFixture;
import fixture.CommentFixture;
import fixture.UserFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class JpaTest extends QueryDslTest {
    private static Random random = new Random();
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserHistoryRepository userHistoryRepository;
    @Autowired
    protected ImageRepository imageRepository;
    @Autowired
    protected BoardRepository boardRepository;
    @Autowired
    protected ContextRepository contextRepository;
    @Autowired
    protected IngredientRepository ingredientRepository;
    @Autowired
    private DescriptionRepository descriptionRepository;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BoardUserEventRepository boardUserEventRepository;

    @Rollback(false)
    @Transactional
    public void userBoardCommentMetaData() {
        List<User> users = creates();
        List<Board> boards = BoardFixture.creates(50, users);

        for (Board board : boards) {
            board = saveBoard(board);

            List<Comment> comments = CommentFixture.creates(random.nextInt(100) + 100, board, users);
            for (Comment comment : comments) {
                comment.updateImage(imageRepository.save(comment.getCommentImage()));
            }
            commentRepository.saveAll(comments);
            randomEvent(board, users);

            double totalStar = comments.stream().mapToDouble(Comment::getStar).sum();
            double total = (totalStar) / (comments.size() + 1);

            board.updateStar(total);
        }
    }

    private List<User> creates() {
        return userRepository.saveAll(UserFixture.creates(300));
    }

    private void randomEvent(Board board, List<User> users) {

        for (User user : users) {
            BoardUserEvent event = new BoardUserEvent(board, user);
            if (random.nextBoolean() || random.nextBoolean() && random.nextBoolean()) {
                event.hitUp();
            }
            if (random.nextBoolean() || random.nextBoolean()) {
                board.updateCount();
            }
            boardUserEventRepository.save(event);
        }
    }


    private Board saveBoard(Board board) {
        List<Description> descriptions = new ArrayList<>();
        for (Description description : board.getContext().getDescriptions()) {
            Description descr = new Description(description.getDescription(), imageRepository.save(description.getImage()));
            descriptions.add(descr);
        }

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (RecipeIngredient recipeIngredient : board.getContext().getBoardIngredients()) {
            Ingredient ingredient = saveOrSelectIngredient(recipeIngredient.getIngredient().getName());
            recipeIngredients.add(RecipeIngredient.ofMyRecipe(ingredient, recipeIngredient.getQuantity()));
        }

        Board result = new Board(board.getUser(),board.getIntroduction(), board.getTitle(), contextRepository.save(new Context(
                recipeIngredientRepository.saveAll(recipeIngredients),
                descriptionRepository.saveAll(descriptions))), saveImage(board.getMainImage()), board.getType());
        return boardRepository.save(result);
    }

    private Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public Ingredient saveOrSelectIngredient(String name) {
        return ingredientRepository.findByName(name)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(name)));
    }
}
