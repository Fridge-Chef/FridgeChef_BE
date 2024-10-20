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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
public class RestDocControllerTests {
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

    protected static ResultMatcher status(int errorCode) {
        return status().is(errorCode);
    }

    public static ResponseFieldsSnippet errorFields(ErrorCode errorCode) {
        return responseFields(Arrays.asList(
                fieldWithPath("status").description(errorCode.getStatus()),
                fieldWithPath("message").description(errorCode.getMessage())));
    }

    public static ResponseFieldsSnippet errorFields(int status, String message) {
        return responseFields(Arrays.asList(
                fieldWithPath("status").description(status),
                fieldWithPath("message").description(message)));
    }

    protected static void failResultAction(ResultActions actions, String message, RequestFieldsSnippet snippet, ErrorCode errorCode) throws Exception {
        actions.andExpect(status(errorCode))
                .andDo(document(message + " - " + errorCode.getMessage(),
                        snippet, errorFields(errorCode)));
    }

    protected static void failResultAction(ResultActions actions, String message, RequestFieldsSnippet snippet, String errorMessage, int errorCode) throws Exception {
        actions.andExpect(status(errorCode))
                .andDo(document(message + " - " + errorMessage,
                        snippet, errorFields(errorCode, errorMessage)));
    }

    protected static void failResultAction(ResultActions actions, String message, RequestFieldsSnippet snippet, ErrorCode errorCode, String errorMessage) throws Exception {
        actions.andExpect(status(errorCode))
                .andDo(document(message + " - " + errorCode.getMessage(),
                        snippet, errorFields(errorCode.getStatus(), errorMessage)));
    }

    protected RequestHeadersSnippet jwtTokenRequest() {
        return requestHeaders(headerWithName("Authorization").description("Bearer token for authentication"));
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

    protected ResultActions jsonPatchWhen(String uri, String request) throws Exception {
        return mockMvc.perform(patch(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
                .with(csrf())
        );
    }

    protected ResultActions jwtGetWhen(String uri) throws Exception {
        return mockMvc.perform(get(uri)
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jwtJsonGetWhen(String uri, String request) throws Exception {
        return mockMvc.perform(get(uri)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }

    protected ResultActions jwtJsonPostWhen(String uri, String request) throws Exception {
        return mockMvc.perform(post(uri)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }


    protected ResultActions jwtJsonPatchWhen(String uri, String request) throws Exception {
        return mockMvc.perform(patch(uri)
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }


    protected ResultActions jwtJsonPutWhen(String uri, String request) throws Exception {
        return mockMvc.perform(put(uri)
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }

    protected ResultActions jwtJsonDeleteWhen(String uri, String request) throws Exception {
        return mockMvc.perform(delete(uri)
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }

    protected ResultActions jsonGetWhen(String uri) throws Exception {
        return mockMvc.perform(get(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jsonGetParamWhen(String uri, MultiValueMap<String,String> params) throws Exception {
        return mockMvc.perform(get(uri)
                .queryParams(params)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jwtJsonGetParamWhen(String uri, MultiValueMap<String,String> params) throws Exception {
        return mockMvc.perform(get(uri)
                .queryParams(params)
                .header(AUTHORIZATION, "Bearer ")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
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
        );
    }

    protected ResultActions jwtJsonGetPathWhen(String uri, String json, Object... path) throws Exception {
        return mockMvc.perform(get(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf())
        );
    }

    protected ResultActions jwtJsonPostPathWhen(String uri, String json, Object... path) throws Exception {
        return mockMvc.perform(post(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
                .with(csrf())
        );
    }

    protected ResultActions jwtDeletePathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(delete(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
        );
    }

    protected ResultActions jwtGetPathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(get(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jwtPatchPathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(patch(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jwtJsonPutPathWhen(String uri, String json, Object... path) throws Exception {
        return mockMvc.perform(put(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
        );
    }

    protected ResultActions jwtFormPostWhen(
            String uri,
            List<MockMultipartFile> files,
            MultiValueMap<String, String> params) throws Exception {
        return mockMvc.perform(formFiles(uri, files)
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .params(params)
        );
    }

    private MockMultipartHttpServletRequestBuilder formFiles(String uri, List<MockMultipartFile> files) {
        var part = multipart(uri);
        files.forEach(part::file);
        return part;
    }
}
