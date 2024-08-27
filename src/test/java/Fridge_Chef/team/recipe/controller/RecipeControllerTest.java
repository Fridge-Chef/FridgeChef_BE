package Fridge_Chef.team.recipe.controller;

import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.recipe.rest.RecipeController;
import Fridge_Chef.team.recipe.rest.request.SampleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest extends RestDocControllerTests {

    @Test
    void testInfo() throws Exception {
        this.mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andDo(document("test-info"));
    }

    @Test
    void testDocs() throws Exception {
       SampleRequest request = new SampleRequest("name", "value");
        String jsonContent = objectMapper.writeValueAsString(request);
        this.mockMvc.perform(post("/api/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andDo(document("api-docs",
                        requestFields(
                                fieldWithPath("name").description("The name of the example"),
                                fieldWithPath("value").description("The value of the example")
                        ),
                        responseFields(
                                fieldWithPath("name").description("The name of the response"),
                                fieldWithPath("value").description("The value of the response")
                        )
                ));
    }
}
