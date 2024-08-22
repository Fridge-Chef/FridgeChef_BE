package Fridge_Chef.team.recipe.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeServiceTest {


    //
    @Test
    public void save() {
        String str = "새우두부계란찜\n연두부 75g(3/4모), 칵테일새우 20g(5마리), 달걀 30g(1/2개), 생크림 13g(1큰술), 설탕 5g(1작은술), 무염버터 5g(1작은술)\n고명\n시금치 10g(3줄기)";

        // 예외 단어 목록
        String[] excludeWords = {"약간", "재료", "양념", "다진", "붉은"};

        String[] ingredientsArray = str.split(",");

        List<String> ingredientList = new ArrayList<>();

        // 정규식을 이용해 "숫자g" 부분을 추출
        Pattern pattern = Pattern.compile("(\\D+)(\\d+g.*)");

        for (String ingredient : ingredientsArray) {
            ingredient = ingredient.trim();  // 공백 제거
            boolean exclude = false;

            // 예외 단어 포함 여부 확인
            for (String word : excludeWords) {
                if (ingredient.contains(word)) {
                    exclude = true;
                    break;
                }
            }

            if (!exclude) {
                Matcher matcher = pattern.matcher(ingredient);
                if (matcher.find()) {
                    String ingredientName = matcher.group(1).trim();
                    String quantity = matcher.group(2).trim();
                    ingredientList.add("재료: " + ingredientName + ", 양: " + quantity);
                } else {
                    // '숫자g' 패턴이 없는 경우 그대로 추가
                    ingredientList.add("재료: " + ingredient.trim());
                }
            }
        }

        // 결과 출력
        for (String item : ingredientList) {
            System.out.println(item);
        }
    }

}
