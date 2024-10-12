package fixture;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardType;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class BoardFixture {
    private final static Random random = new Random();

    public static Board create(User user) {
        Board board = new Board(user, "","title", new Context(List.of(), List.of()), ImageFixture.create(), BoardType.USER);
        board.updateId(2L);
        return board;
    }

    public static List<Board> creates(int size, List<User> users) {
        List<Board> boards = new ArrayList<>();

        int index = 0;
        for (int i = 0; i < size; i++) {
            User user = users.get(index);
            String title = CORE_RECIPE_NAMES.get(random.nextInt(CORE_RECIPE_NAMES.size()));
            BoardByRecipeRequest request = boardProvider();

            List<Description> descriptions = request.getRecipeIngredients()
                    .stream().map(instruction -> new Description(instruction.getDetails(), ImageFixture.create())).toList();
            List<RecipeIngredient> ingredients = new ArrayList<>();

            request.getRecipeIngredients().forEach(ingredient -> {
                ingredients.add(RecipeIngredient.ofMyRecipe(new Ingredient(ingredient.getName()), ingredient.getDetails()));
            });

            Board board = new Board(user,"소개글 ~~~~~ ", title, new Context(
                    ingredients,
                    descriptions
            ), ImageFixture.create(), BoardType.USER);

            boards.add(board);
            index = random.nextInt(users.size());
        }
        return boards;
    }


    public static BoardByRecipeRequest boardProvider() {
        MockMultipartFile mainImage = new MockMultipartFile("mainImage", "Fridge_chef.team.image" + random.nextInt(1000) + ".jpg", "image/jpeg", "dummy Fridge_chef.team.image content".getBytes());
        MockMultipartFile instructionImage = new MockMultipartFile("instructionImage", "step" + random.nextInt(1000) + ".jpg", "image/jpeg", "dummy Fridge_chef.team.image content".getBytes());

        String recipeTitle = RECIPE_TITLES.get(random.nextInt(RECIPE_TITLES.size())) + " " + CORE_RECIPE_NAMES.get(random.nextInt(CORE_RECIPE_NAMES.size())) + " " + ADDITIONAL_RECIPE_NAMES.get(random.nextInt(ADDITIONAL_RECIPE_NAMES.size()));
        String recipeDescription = COOKING_STEPS.get(random.nextInt(COOKING_STEPS.size()));

        List<BoardByRecipeRequest.RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            String ingredientName = INGREDIENTS.get(random.nextInt(INGREDIENTS.size()));
            String quantity = (random.nextInt(500) + 50) + "g";
            recipeIngredients.add(new BoardByRecipeRequest.RecipeIngredient(ingredientName, quantity));
        }

        List<BoardByRecipeRequest.Instructions> instructions = new ArrayList<>();
        for (BoardByRecipeRequest.RecipeIngredient recipeIngredient : recipeIngredients) {
            String stepDescription = recipeIngredient.getName() + "을(를) 사용해 " + recipeDescription;
            instructions.add(new BoardByRecipeRequest.Instructions(stepDescription, null));
        }

        return new BoardByRecipeRequest(
                recipeTitle,
                recipeDescription,
                mainImage,
                recipeIngredients,
                instructions
        );
    }

    private static final List<String> INGREDIENTS = Arrays.asList(
            "라면", "김치", "참치", "당근", "양파", "감자", "계란", "우유", "소금", "고추장",
            "대파", "고구마", "소고기", "닭고기", "돼지고기", "마늘", "고춧가루", "참기름", "간장", "설탕",
            "버섯", "미역", "치즈", "스파게티", "햄", "깻잎", "연두부", "콩나물", "멸치", "된장",
            "쌀", "참깨", "새우", "오징어", "조개", "해파리", "토마토", "양상추", "버터", "브로콜리",
            "베이컨", "연어", "떡", "쌀국수", "굴소스", "두부", "감자전분", "초콜릿", "아몬드", "피망"
    );

    private static final List<String> COOKING_STEPS = Arrays.asList(
            "맛있게 조리한다.", "약불에서 천천히 익힌다.", "고소하게 볶는다.", "재료를 잘 섞는다.",
            "끓는 물에 넣고 푹 끓인다.", "바삭하게 튀긴다.", "구수한 맛이 나도록 한다.", "재료를 골고루 버무린다.",
            "풍미가 가득해질 때까지 기다린다.", "재료가 잘 익을 때까지 구워준다.", "재료를 부드럽게 다진다.",
            "새콤달콤하게 양념한다.", "기름에 잘 구워준다.", "냄비에 넣고 졸여준다.", "따뜻한 불에서 구워낸다.",
            "촉촉하게 익힌다.", "감칠맛이 나도록 소스를 더한다.", "쫄깃쫄깃한 식감을 살린다.", "향이 풍부하게 피어오를 때까지 끓인다.",
            "재료의 신선함을 유지한다."
    );

    private static final List<String> RECIPE_TITLES = Arrays.asList(
            "맛있는", "간단한", "빠르게 만드는", "특별한", "정성이 가득한", "집에서 간편하게", "풍부한 맛의", "촉촉한",
            "고소한", "영양이 가득한", "매콤한", "담백한", "기름기 없는", "풍미 가득한", "건강한", "고급스러운",
            "신선한 재료로 만든", "상큼한", "쫄깃한", "따뜻한"
    );

    private static final List<String> CORE_RECIPE_NAMES = Arrays.asList(
            "라면", "김치찌개", "참치 마요 덮밥", "된장국", "비빔밥", "볶음밥", "스파게티", "샐러드", "카레", "탕수육",
            "김밥", "떡볶이", "잡채", "삼겹살 구이", "닭갈비", "계란말이", "파스타", "찜닭", "갈비찜", "초밥",
            "불고기", "순두부찌개", "떡국", "콩나물국", "갈비탕", "육개장", "오므라이스", "부대찌개", "감자탕", "수제비",
            "라자냐", "찹쌀떡", "쌀국수", "닭볶음탕", "전복죽", "오리백숙", "도토리묵", "북엇국", "나물비빔밥", "코다리찜",
            "된장찌개", "홍합탕", "우동", "해물파전", "청국장", "고등어구이", "닭강정", "애호박전", "두부조림", "낙지볶음",
            "조기찜", "돼지불백", "갈치조림", "오징어덮밥", "생선가스", "닭가슴살 샐러드", "장어덮밥", "카프레제", "치킨커리", "훈제연어",
            "토마토 스파게티", "봉골레 파스타", "차돌박이 샤브샤브", "새우튀김", "돈가스", "유부초밥", "매운갈비찜", "짜장면", "짬뽕", "양념치킨",
            "탕탕이", "굴국밥", "매생이국", "김치볶음밥", "소불고기", "문어숙회", "차돌된장찌개", "갈릭버터 새우", "불닭볶음면", "장조림",
            "낙지소면", "어묵탕", "브리또", "찜갈비", "바비큐폭립", "돼지갈비", "게살스프", "시금치프리타타", "고추장불고기", "샤브샤브",
            "감바스", "수제비", "닭가슴살 구이", "양갈비 스테이크", "바지락칼국수", "칠리새우", "우럭찜", "미소된장국", "비프스튜", "해물찜"
    );

    private static final List<String> ADDITIONAL_RECIPE_NAMES = Arrays.asList(
            "특제 소스", "달콤한 양념", "고소한 참기름", "매콤한 맛", "정통 스타일", "집에서", "풍성한 재료", "엄마의 손맛", "전통 방식",
            "간편 레시피", "신선한 재료", "초보 요리사용", "프로 요리사용", "아이들이 좋아하는", "어른들도 좋아하는", "혼자 먹기 좋은"
    );
}
