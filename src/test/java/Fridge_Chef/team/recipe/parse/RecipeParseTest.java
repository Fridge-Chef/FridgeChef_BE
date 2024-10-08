package Fridge_Chef.team.recipe.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeParseTest {

    @Test
    void parse() throws IOException {
        Path jsonFilePath = Path.of("src/test/resources/test.json");
        File file = new File(jsonFilePath.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(file);

        JsonNode rows = rootNode.path("COOKRCP01").path("row");

        if (rows.isArray()) {
            for (JsonNode node : rows) {
                String rcpPartsDtls = node.path("RCP_PARTS_DTLS").asText();
                String name = node.path("RCP_NM").asText();
                Map<String, List<String>> parsedResult = parseIngredientsAndSauces(rcpPartsDtls,name);

                if(parsedResult != null){
                    // TODO 등록될 재료들
                    System.out.println("재료: ");
                    parsedResult.get("ingredients").forEach(System.out::println);
                    System.out.println("소스:");
                    parsedResult.get("sauces").forEach(System.out::println);
                    System.out.println(">>>>>>>>>>>>>>>");
                }
            }
        } else {
            System.out.println("JSON 형식이 배열이 아닙니다.");
        }
    }

    public static Map<String, List<String>> parseIngredientsAndSauces(String rcpPartsDtls,String name) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> ingredients = new ArrayList<>();
        List<String> sauces = new ArrayList<>();

        String regex = "[\\n·●,]+";
        List<String> parts = Arrays.stream(removeCommasInsideParentheses(rcpPartsDtls).split(regex))
                .map(String::trim)
                .filter(part -> !part.isEmpty()).toList();

        String recipeName = name.replaceAll(" ","");
        System.out.println(recipeName);
        boolean isSauces=false;
        String temp = "";
        if(parts.size() == 0){
            return null;
        }
        for (String part : parts) {
            part = part.replaceAll(" ","");
            part = part.replaceAll("<br>","");
            part = part.replaceAll("[2인분]","");
            part = part.replace("]", "");
            part = part.replace("[", "");
            if(part.contains("•[")){
                ingredients.add(part.split("]")[1]);
            }else if(part.contains("곁들이채소") || part.contains("매쉬포테이토") || part.contains("•필수재료") || part.contains("•올리브마늘드레싱") ||
                    part.contains("•소고기곁들임채소") || part.contains("•육수")|| part.contains("•숙성")  || part.contains("•곁들임채소")   ||
                    part.contains("-곁들임")   ) {
                // 기타 필터 분리 •숙성 •곁들임채소 -곁들임
                ingredients.add(part.split(":")[1]);
            }else if(recipeName.contains(part) || (recipeName+":").contains(part)|| part.contains(recipeName) || part.contains("주재료")||
                    part.contains("2인분기준") ){

                // 제거
            }else if (part.contains("•양념장") || part.contains("양념장") ||part.contains("양념") ||part.contains("- 육") ||part.contains("-밀전병") ||
                    part.contains("•소스") ||  part.contains("소스:")  || part.contains("고명") || part.contains("•고명")) {
                //양념 소스 분리
                if(isSauces){
                    sauces.add(temp);
                    temp="";
                }
                isSauces=true;
                temp+=part+",";
            }else if(part.contains(":") ){
                if(isSauces){
                    temp+=part+",";
                }else{
                    if (part.contains("•")){
                        part = part.replace("•", "");
                    }
                    ingredients.add(part.split(":")[1]);
                }
            }else {
                if(isSauces){
                    temp+=part+",";
                }else{
                    if (part.contains("•")){
                        part = part.replace("•", "");
                    }
                    ingredients.add(part);
                }
            }
        }

        if(isSauces ){
            sauces.add(temp);
        }
        result.put("ingredients", ingredients);
        result.put("sauces", sauces);
        return result;
    }

    private final static Pattern pattern = Pattern.compile("\\(([^\\)]+)\\)");
    private static String removeCommasInsideParentheses(String src) {
        Matcher matcher = pattern.matcher(src);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String insideParentheses = matcher.group(1);
            String modifiedInside = insideParentheses.replace(",", "");
            matcher.appendReplacement(result, "(" + modifiedInside + ")");
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
