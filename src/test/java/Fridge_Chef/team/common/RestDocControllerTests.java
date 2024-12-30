package Fridge_Chef.team.common;


import Fridge_Chef.team.common.docs.CustomMockPart;
import Fridge_Chef.team.common.docs.CustomMockPartFile;
import Fridge_Chef.team.common.docs.CustomPart;
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
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestPartDescriptor;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static fixture.ImageFixture.partMockImage;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * RestDocControllerTests
 * RestDocs + Redocly 통합 테스트를 위한 공통 테스트 클래스
 *
 * @author JHKoder
 */
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
public class RestDocControllerTests {
    protected static ObjectMapper objectMapper = new ObjectMapper();
    protected static JSONParser jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
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

    public static RequestPartsSnippet requestPartsForm(List<CustomPart> form) {
        List<RequestPartDescriptor> parts = new ArrayList<>();
        for (CustomPart part : form) {
            if (part instanceof MultipartFile file) {
                CustomMockPartFile mockPart = (CustomMockPartFile) part;
                RequestPartDescriptor partToMock = partWithName(file.getName()).description(mockPart.getDescription() + " 이미지 파일");
                requestPartDetails(parts, mockPart, partToMock);
            } else if (part instanceof CustomMockPart mockPart) {
                MockPart partToMock = new MockPart(mockPart.getName(), mockPart.getContent());
                RequestPartDescriptor partDescriptor = partWithName(partToMock.getName()).description(mockPart.getContents());
                requestPartDetails(parts, mockPart, partDescriptor);
            }
        }
        return requestParts(parts);
    }

    protected static void failJwtResultAction(ResultActions actions, String message, RequestPartsSnippet snippet, ErrorCode errorCode) throws Exception {
        actions.andExpect(status(errorCode))
                .andDo(document(message + " - " + errorCode.getMessage(),
                        jwtTokenRequest(),
                        snippet, errorFields(errorCode)));
    }

    protected static void failResultAction(ResultActions actions, String message, AbstractFieldsSnippet snippet, ErrorCode errorCode) throws Exception {
        actions.andExpect(status(errorCode))
                .andDo(document(message + " - " + errorCode.getMessage(),
                        snippet, errorFields(errorCode)));
    }

    protected static void failResultAction(ResultActions actions, String message, RequestFieldsSnippet snippet, ErrorCode errorCode, String errorMessage) throws Exception {
        actions.andExpect(status(errorCode))
                .andDo(document(message + " - " + errorCode.getMessage(),
                        snippet, errorFields(errorCode.getStatus(), errorMessage)));
    }

    protected static RequestHeadersSnippet jwtTokenRequest() {
        return requestHeaders(headerWithName("Authorization").description("Bearer token for authentication"));
    }

    protected static CustomPart part(String name, byte[] content) {
        return new CustomMockPartFile(name, content);
    }

    protected static CustomPart part(String name, String content) {
        return new CustomMockPart(name, content);
    }

    protected static CustomPart part(String name, String content, boolean option) {
        return new CustomMockPart(name, content, option);
    }

    protected static CustomPart part(String name, String content, String description) {
        return new CustomMockPart(name, content, description);
    }

    protected static CustomPart part(String name, String content, String description, boolean option) {
        return new CustomMockPart(name, content, description, option);
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
        );
    }

    protected ResultActions jsonUpdateWhen(String uri, String request) throws Exception {
        return mockMvc.perform(put(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }

    protected ResultActions jsonDeleteWhen(String uri, String request) throws Exception {
        return mockMvc.perform(delete(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
        );
    }

    protected ResultActions jsonPatchWhen(String uri, String request) throws Exception {
        return mockMvc.perform(patch(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request)
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

    protected ResultActions jsonGetParamWhen(String uri, MultiValueMap<String, String> params) throws Exception {
        return mockMvc.perform(get(uri)
                .queryParams(params)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jwtJsonGetParamWhen(String uri, MultiValueMap<String, String> params) throws Exception {
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
        );
    }

    protected ResultActions jsonUpdatesWhen(String uri) throws Exception {
        return mockMvc.perform(patch(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jsonUpdateWhen(String uri) throws Exception {
        return mockMvc.perform(put(uri)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
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
        );
    }

    protected ResultActions jsonPostPathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(post(uri, path)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions jsonDeletePathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(delete(uri, path)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
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
        );
    }

    protected ResultActions jwtJsonPostPathWhen(String uri, String json, Object... path) throws Exception {
        return mockMvc.perform(post(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
        );
    }

    protected ResultActions jwtDeletePathWhen(String uri, Object... path) throws Exception {
        return mockMvc.perform(delete(uri, path)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
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

    protected static MockHttpServletRequestBuilder jwtFormGetWhen(String uri, List<CustomPart> formData) {
        return jwtForm(RestDocumentationRequestBuilders.multipart(uri), formData)
                .with(request -> {
                    request.setMethod("GET");
                    return request;
                });
    }

    protected static MockHttpServletRequestBuilder jwtFormPatchWhen(String uri, CustomPart formData) {
        return jwtForm(RestDocumentationRequestBuilders.multipart(uri), List.of(formData))
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                });
    }

    protected static RequestBuilder jwtFormPostWhen(String uri, CustomPart formData) {
        return jwtForm(RestDocumentationRequestBuilders.multipart(uri), List.of(formData));
    }

    protected static RequestBuilder jwtFormPostPathWhen(String uri, List<CustomPart> formData, Object... path) {
        return jwtForm(RestDocumentationRequestBuilders.multipart(uri, path), formData);
    }

    protected static RequestBuilder jwtFormPutPathWhen(String uri, List<CustomPart> formData, Object... path) {
        return jwtForm(RestDocumentationRequestBuilders.multipart(uri, path), formData)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                });
    }

    protected MockHttpServletRequestBuilder jwtFormPostWhen(String uri, List<CustomPart> parts) {
        return jwtForm(RestDocumentationRequestBuilders.multipart(uri), parts);
    }

    protected MockHttpServletRequestBuilder jwtFormPutWhen(String uri, List<CustomPart> parts) {
        return jwtFormPostWhen(uri, parts)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                });
    }

    private static MockHttpServletRequestBuilder jwtForm(MockMultipartHttpServletRequestBuilder builder, List<CustomPart> formData) {
        for (CustomPart part : formData) {
            if (part instanceof MultipartFile file) {
                builder.file(partMockImage(file.getName()));
            } else if (part instanceof CustomMockPart customMockPart) {
                builder.part(new MockPart(customMockPart.getName(), customMockPart.getContent()));
            }
        }
        return builder.header(AUTHORIZATION, "Bearer ")
                .contentType(MediaType.MULTIPART_FORM_DATA);
    }

    private static void requestPartDetails(List<RequestPartDescriptor> temp, CustomPart part, RequestPartDescriptor descriptor) {
        if (part.isOptional()) {
            descriptor.attributes(key("required").value("true"));
        } else {
            descriptor.attributes(key("required").value("false"));
        }

        if (part instanceof MultipartFile file) {
            String contentType = file.getContentType();
            if (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg")) {
                descriptor.attributes(key("type").value("이미지 파일"));
            } else {
                descriptor.attributes(key("type").value("파일"));
            }
        } else if (part instanceof CustomMockPart customMockPart) {
            if (isNumber(customMockPart.getContents())) {
                descriptor.attributes(key("type").value("Integer"));
            } else if (isBoolean(customMockPart.getContents())) {
                descriptor.attributes(key("type").value("Boolean"));
            } else {
                descriptor.attributes(key("type").value("String"));
            }
        }
        temp.add(descriptor);
    }

    private static boolean isBoolean(String bool) {
        return bool.equals("true") || bool.equals("false");
    }

    private static boolean isNumber(String num) {
        try {
            Integer.parseInt(num);
            Long.parseLong(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
