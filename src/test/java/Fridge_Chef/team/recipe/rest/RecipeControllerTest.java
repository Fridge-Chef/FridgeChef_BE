package Fridge_Chef.team.recipe.rest;

import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.common.docs.CustomPart;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.recipe.service.RecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@DisplayName("레시피")
@WebMvcTest(RecipeController.class)
public class RecipeControllerTest extends RestDocControllerTests {

    @MockBean
    public RecipeService recipeService;


    @Test
    @WithMockCustomUser
    @DisplayName("레시피 검색")
    void recipeSelect() throws Exception {
        when(recipeService.searchRecipe(any(), anyList(), anyList(), any()))
                .thenReturn(recipeProvider());

        List<CustomPart> formData = List.of(
                part("must", "치킨"),
                part("ingredients", "닭고기"),
                part("page", "0"),
                part("size", "50"),
                part("sort", "LATEST")
        );

        ResultActions actions = mockMvc.perform(jwtFormGetWhen("/api/recipes/", formData));

        actions.andExpect(status().isOk())
                .andDo(document("추천 레시피 검색",
                        requestPartsForm(formData),
                        responseFields(
                                fieldWithPath("content[]").description("레시피 목록"),
                                fieldWithPath("content[].id").description("레시피 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].title").description("레시피 제목"),
                                fieldWithPath("content[].username").description("레시피 작성자"),
                                fieldWithPath("content[].pathMainImage").description("레시피 이미지"),
                                fieldWithPath("content[].totalStar").description("레시피 평점").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].hit").description("조회수").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].isUserHit").description("조회 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("content[].isMyMe").description("내가 작성한 레시피 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("content[].count").description("조회수").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].createTime").description("작성일"),
                                fieldWithPath("content[].pickCount").description("찜 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].withoutCount").description("제외 재료 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].without").description("제외 재료").type(JsonFieldType.ARRAY),
                                fieldWithPath("page.size").description("페이지 크기").type(JsonFieldType.NUMBER),
                                fieldWithPath("page.number").description("페이지 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("page.totalElements").description("전체 요소 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("page.totalPages").description("전체 페이지 수").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    private Page<RecipeSearchResponse> recipeProvider() {
        List<RecipeSearchResponse> recipeSearchResponses = Arrays.asList(
                new RecipeSearchResponse(1L, "치킨", "치킨 레시피", "치킨 레시피입니다.", 3.5, 1,
                        true, false, 5, LocalDateTime.now(), 5, 5, List.of("치킨", "닭고기")),
                new RecipeSearchResponse(2L, "김치", "김치 레시피", "김치 레시피입니다.", 3.5, 1,
                        true, false, 5, LocalDateTime.now(), 5, 5, List.of("배추", "고추")));

        return new PageImpl<>(recipeSearchResponses, PageRequest.of(1, 50), recipeSearchResponses.size());
    }
}
