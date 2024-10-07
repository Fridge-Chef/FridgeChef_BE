package Fridge_Chef.team.recipe.dump;

import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DumpServiceTest {

    @InjectMocks
    private DumpService dumpService;

    @Mock
    private IngredientRepository ingredientRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void extract_ingredient_names() {
        //sample 1
        String name1 = "새우 두부 계란찜";
        String ingredient1 = "새우두부계란찜\n연두부 75g(3/4모), 칵테일새우 20g(5마리), 달걀 30g(1/2개), 생크림 13g(1큰술), 설탕 5g(1작은술), 무염버터 5g(1작은술)\n고명\n시금치 10g(3줄기)";
        List<RecipeIngredient> result1 = dumpService.extractIngredients(ingredient1, name1);

        assertThat(result1).extracting("ingredient.name").containsExactly("연두부", "칵테일새우", "달걀", "생크림", "설탕", "무염버터", "고명", "시금치");
        assertThat(result1).extracting("quantity")
                .containsExactly("75g(3/4모)", "20g(5마리)", "30g(1/2개)", "13g(1큰술)", "5g(1작은술)", "5g(1작은술)", "X", "10g(3줄기)");

        //sample 2
        String name2 = "부추 콩가루 찜";
        String ingredient2 = "[1인분]조선부추 50g, 날콩가루 7g(1⅓작은술)\n·양념장 : 저염간장 3g(2/3작은술), 다진 대파 5g(1작은술), 다진 마늘 2g(1/2쪽), 고춧가루 2g(1/3작은술), 요리당 2g(1/3작은술), 참기름 2g(1/3작은술), 참깨 약간";
        List<RecipeIngredient> result2 = dumpService.extractIngredients(ingredient2, name2);

        assertThat(result2).extracting("ingredient.name").containsExactly("조선부추", "날콩가루", "저염간장", "다진 대파", "다진 마늘", "고춧가루", "요리당", "참기름", "참깨");
        assertThat(result2).extracting("quantity").containsExactly("50g", "7g(1⅓작은술)", "3g(2/3작은술)", "5g(1작은술)", "2g(1/2쪽)", "2g(1/3작은술)", "2g(1/3작은술)", "2g(1/3작은술)", "약간");

        //sample 3
        String name3 = "방울토마토 소박이";
        String ingredient3 = "●방울토마토 소박이 : \n방울토마토 150g(5개), 양파 10g(3×1cm), 부추 10g(5줄기)\n●양념장 : \n고춧가루 4g(1작은술), 멸치액젓 3g(2/3작은술), 다진 마늘 2.5g(1/2쪽), 매실액 2g(1/3작은술), 설탕 2g(1/3작은술), 물 2ml(1/3작은술), 통깨 약간";
        List<RecipeIngredient> result3 = dumpService.extractIngredients(ingredient3, name3);

        assertThat(result3).extracting("ingredient.name").containsExactly(
                "방울토마토", "양파", "부추", "고춧가루", "멸치액젓", "다진 마늘", "매실액", "설탕", "물", "통깨");
        assertThat(result3).extracting("quantity").containsExactly(
                "150g(5개)", "10g(3×1cm)", "10g(5줄기)", "4g(1작은술)", "3g(2/3작은술)",
                "2.5g(1/2쪽)", "2g(1/3작은술)", "2g(1/3작은술)", "2ml(1/3작은술)", "약간");

        //sample 4
        String name4 = "순두부 사과 소스 오이무침";
        String ingredient4 = "●오이무침 :\n오이 70g(1/3개), 다진 땅콩 10g(1큰술)\n●순두부사과 소스 : \n순두부 40g(1/8봉지), 사과 50g(1/3개)";
        List<RecipeIngredient> result4 = dumpService.extractIngredients(ingredient4, name4);

        assertThat(result4).extracting("ingredient.name").containsExactly(
                "오이", "다진 땅콩", "순두부", "사과");
        assertThat(result4).extracting("quantity").containsExactly(
                "70g(1/3개)", "10g(1큰술)", "40g(1/8봉지)", "50g(1/3개)");

        //sample 5
        String name5 = "사과 새우 북엇국";
        String ingredient5 = "북엇국\n북어채 25g(15개), 새우 10g(3마리), 사과 30g(1/5개), 양파 40g(1/4개),\n표고버섯 20g(2장), 물 300ml(1½컵)";
        List<RecipeIngredient> result5 = dumpService.extractIngredients(ingredient5, name5);

        assertThat(result5).extracting("ingredient.name").containsExactly(
                "북엇국", "북어채", "새우", "사과", "양파", "표고버섯", "물");
        assertThat(result5).extracting("quantity").containsExactly(
                "X", "25g(15개)", "10g(3마리)", "30g(1/5개)", "40g(1/4개)", "20g(2장)", "300ml(1½컵)");

        //sample6
        String name6 = "양배추감자전";
        String ingredient6 = "●주재료 : 감자 100g(1개), 양배추 150g(1/2개), 당근 15g(1/10개), 두부 20g(1/20모), 돼지고기 30g, 청양고추 5g(1개), 부침가루 45g(3큰술), 달걀 60g(1개), 식용유 15g(1큰술)\n●소스 : 오렌지즙 15g(1큰술), 간장 2g(1/2작은술), 식초 10g(2작은술)";
        List<RecipeIngredient> result6 = dumpService.extractIngredients(ingredient6, name6);

        assertThat(result6).extracting("ingredient.name").containsExactly(
                "감자", "양배추", "당근", "두부", "돼지고기", "청양고추", "부침가루", "달걀", "식용유",
                "오렌지즙", "간장", "식초");
        assertThat(result6).extracting("quantity").containsExactly(
                "100g(1개)", "150g(1/2개)", "15g(1/10개)", "20g(1/20모)", "30g", "5g(1개)",
                "45g(3큰술)", "60g(1개)", "15g(1큰술)", "15g(1큰술)", "2g(1/2작은술)", "10g(2작은술)");

        //sample 7
        String name7 = "그린매쉬드포테이토";
        String ingredient7 = "●그린매쉬드포테이토 : 감자 80g(1/2개), 시금치우유 소스 5g(1작은술), 아몬드 2g(1알), 설탕 2g(1/3작은술), 크랜베리 3g, 치커리 약간\n●시금치우유 소스 : 시금치 10g, 우유 10g(2작은술)";
        List<RecipeIngredient> result7 = dumpService.extractIngredients(ingredient7, name7);

        assertThat(result7).extracting("ingredient.name").containsExactly(
                "감자", "시금치우유 소스", "아몬드", "설탕", "크랜베리", "치커리", "시금치", "우유");
        assertThat(result7).extracting("quantity").containsExactly(
                "80g(1/2개)", "5g(1작은술)", "2g(1알)", "2g(1/3작은술)", "3g", "약간", "10g", "10g(2작은술)");

        //sample 8
        String name8 = "나가사키부대찌개";
        String ingredient8 = "재료 통조림 햄(30g), 우유(100g), 실곤약(50g), 양파(30g), 느타리버섯(25g)\n표고버섯(10g), 팽이버섯(30g), 청양고추(5g), 붉은 고추(5g), 감자(30g)\n무(30g), 두부(40g), 애호박(30g), 깻잎(3g), 쑥갓(10g)\n완자 다진 돼지고기(20g), 다진 파(5g), 다진 마늘(5g), 후춧가루(1g)\n소금(1g), 밀가루(10g), 달걀노른자(20g)\n육수 사골육수(300g), 다시마(5g)\n양념 들깻가루(15g)";

        List<RecipeIngredient> result8 = dumpService.extractIngredients(ingredient8, name8);

        assertThat(result8).extracting("ingredient.name").containsExactly(
                "통조림 햄", "우유", "실곤약", "양파", "느타리버섯", "표고버섯", "팽이버섯",
                "청양고추", "붉은 고추", "감자", "무", "두부", "애호박", "깻잎", "쑥갓",
                "완자 다진 돼지고기", "다진 파", "다진 마늘", "후춧가루", "소금",
                "밀가루", "달걀노른자", "육수 사골육수", "다시마", "양념 들깻가루");
        assertThat(result8).extracting("quantity").containsExactly(
                "(30g)", "(100g)", "(50g)", "(30g)", "(25g)", "(10g)", "(30g)",
                "(5g)", "(5g)", "(30g)", "(30g)", "(40g)", "(30g)", "(3g)", "(10g)",
                "(20g)", "(5g)", "(5g)", "(1g)", "(1g)", "(10g)", "(20g)",
                "(300g)", "(5g)", "(15g)");

        //sample 9
        String name9 = "삼색꼬치구이";
        String ingredient9 = "재료 돼지고기(목살, 120g), 파인애플(30g), 브로콜리(20g), 방울토마토(40g)\n배사과소스 배(30g), 사과(50g), 레몬즙(5g), 꿀(5g)";
        List<RecipeIngredient> result9 = dumpService.extractIngredients(ingredient9, name9);

        assertThat(result9).extracting("ingredient.name").containsExactly(
                "돼지고기", "파인애플", "브로콜리", "방울토마토", "배", "사과", "레몬즙", "꿀");
        assertThat(result9).extracting("quantity").containsExactly(
                "(목살, 120g)", "(30g)", "(20g)", "(40g)", "(30g)", "(50g)", "(5g)", "(5g)");
    }
}
