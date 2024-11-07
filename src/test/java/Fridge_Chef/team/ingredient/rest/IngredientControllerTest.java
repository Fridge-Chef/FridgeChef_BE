package Fridge_Chef.team.ingredient.rest;

import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.ingredient.rest.response.IngredientSearchResponse;
import Fridge_Chef.team.ingredient.service.IngredientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("재료")
@WebMvcTest(IngredientController.class)
public class IngredientControllerTest extends RestDocControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IngredientService ingredientService;

    @Test
    @DisplayName("재료 전체 조회")
    void find() throws Exception {
        IngredientSearchResponse response = new IngredientSearchResponse(List.of("가지", "간 마늘"));
        when(ingredientService.findAllIngredients()).thenReturn(response);

        ResultActions actions = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/ingredient/search")
                        .contentType("application/json")
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredientNames").value(hasSize(2)))
                .andExpect(jsonPath("$.ingredientNames[0]").value("가지"))
                .andExpect(jsonPath("$.ingredientNames[1]").value("간 마늘"))
                .andDo(document("재료 전체 검색",
                        responseFields(
                                fieldWithPath("ingredientNames").description("전체 재료 이름 목록"),
                                fieldWithPath("ingredientNames[]").description("각 재료 이름")
                        )
                ));
    }
}
