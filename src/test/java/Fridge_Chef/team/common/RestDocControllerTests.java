package Fridge_Chef.team.common;


import Fridge_Chef.team.config.TestSecurityConfig;
import Fridge_Chef.team.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.util.Arrays;

import static Fridge_Chef.team.common.RestDocControllerTests.SCHEME;
import static org.springframework.http.HttpHeaders.HOST;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = SCHEME, uriHost = HOST)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
public class RestDocControllerTests {
    public static final String SCHEME = "https";
    public static final String HOST = "localhost";
    protected static ObjectMapper objectMapper = new ObjectMapper();
    protected static JSONParser jsonParser = new JSONParser();
    @Autowired
    protected MockMvc mockMvc;


    protected static StatusResultMatchers status() {
        return MockMvcResultMatchers.status();
    }

    protected static ResultMatcher status(ErrorCode errorCode) {
        return status().is(errorCode.getStatus());
    }

    public static ResponseFieldsSnippet errorFields() {
        return responseFields(Arrays.asList(
                fieldWithPath("status").description("에러 상태"),
                fieldWithPath("message").description("에러 메시지")));
    }

    public static ResponseFieldsSnippet errorFields(ErrorCode errorCode) {
        return responseFields(Arrays.asList(
                fieldWithPath("status").description(errorCode.getStatus()),
                fieldWithPath("message").description(errorCode.getMessage())));
    }

    protected static String strToJson(String id, String value) {
        try {
            Object obj = jsonParser.parse("{\"" + id + "\": \"" + value + "\"}");
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("RestDocControllerTests.strToJson.parse ERROR");
        }
    }

    protected static String strToJson(String id, int value) {
        try {
            Object obj = jsonParser.parse("{\"" + id + "\": " + value + "}");
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("RestDocControllerTests.strToJson.parse ERROR");
        }
    }

    protected static String strToJson(String id, Long value) {
        try {
            Object obj = jsonParser.parse("{\"" + id + "\": " + value + "}");
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("RestDocControllerTests.strToJson.parse ERROR");
        }
    }


    protected ResultActions jsonGetWhen(String uri, String request) throws Exception {
        return mockMvc.perform(get(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
                .with(csrf())
        );
    }

    protected ResultActions jsonPostWhen(String uri, String request) throws Exception {
        return mockMvc.perform(post(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }

    protected ResultActions jsonUpdatesWhen(String uri, String request) throws Exception {
        return mockMvc.perform(patch(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
                .with(csrf())
        );
    }

    protected ResultActions jsonUpdateWhen(String uri, String request) throws Exception {
        return mockMvc.perform(put(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
                .with(csrf())
        );
    }

    protected ResultActions jsonDeleteWhen(String uri, String request) throws Exception {
        return mockMvc.perform(delete(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
                .with(csrf())
        );
    }

    //        mockMvc.perform(get(uri,))
    protected ResultActions jsonGetWhen(String uri) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jsonPostWhen(String uri) throws Exception {
        return mockMvc.perform(post(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())

        );
    }

    protected ResultActions jsonUpdatesWhen(String uri) throws Exception {
        return mockMvc.perform(patch(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jsonUpdateWhen(String uri) throws Exception {
        return mockMvc.perform(put(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jsonDeleteWhen(String uri) throws Exception {
        return mockMvc.perform(delete(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jsonGetPathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(get(uri, path)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jsonPostPathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(post(uri, path)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jsonDeletePathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(delete(uri, path)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jsonUpdatePathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(patch(uri, path)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }


    protected ResultActions jsonUpdatePathAndJsonWhen(String uri, int path, String json) throws Exception {
        return mockMvc.perform(patch(uri, path)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf())
        );
    }
}
