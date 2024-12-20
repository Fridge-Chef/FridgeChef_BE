package Fridge_Chef.team.common.docs;

import Fridge_Chef.team.common.docs.CustomPart;
import jakarta.servlet.http.Part;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

/**
 * MockPart 를 대처하기 위한 클래스
 *
 * @author JHkoder
 */
public class CustomMockPart implements Part, CustomPart {
    private final String name;

    private final String filename;
    private final String content;
    private final byte[] contents;

    private final String description;
    private final boolean optional;

    private final HttpHeaders headers = new HttpHeaders();


    /**
     * optional 이 false 인 경우 필수값
     * true 인 경우 선택값
     */
    public CustomMockPart(String name, String content) {
        this(name, null, content, " @see CustomMockPart. No description ", true);
    }

    public CustomMockPart(String name, String content, boolean optional) {
        this(name, null, content, " @see CustomMockPart. No description ", optional);
    }

    public CustomMockPart(String name, String content, String description) {
        this(name, null, content, description, true);
    }

    public CustomMockPart(String name, String content, String description, boolean optional) {
        this(name, null, content, description, optional);
    }

    public CustomMockPart(String name, @Nullable String filename, @Nullable String content, String description, boolean optional) {
        this(name, filename, content, null, description, optional);
    }

    public CustomMockPart(String name, @Nullable String filename, @Nullable String content, @Nullable MediaType contentType, String description, boolean optional) {
        Assert.hasLength(name, "'name' must not be empty");
        this.name = name;
        this.filename = filename;
        this.content = content;
        this.contents = (content != null ? content.getBytes() : new byte[0]);
        this.headers.setContentDispositionFormData(name, filename);
        this.headers.setContentType(contentType);
        this.description = description;
        this.optional = optional;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public String getSubmittedFileName() {
        return this.filename;
    }

    @Override
    @Nullable
    public String getContentType() {
        MediaType contentType = this.headers.getContentType();
        return (contentType != null ? contentType.toString() : null);
    }

    @Override
    public long getSize() {
        return this.contents.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.contents);
    }

    @Override
    public void write(String fileName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public String getHeader(String name) {
        return this.headers.getFirst(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        Collection<String> headerValues = this.headers.get(name);
        return (headerValues != null ? headerValues : Collections.emptyList());
    }

    @Override
    public Collection<String> getHeaderNames() {
        return this.headers.keySet();
    }

    public final HttpHeaders getHeaders() {
        return this.headers;
    }

    public byte[] getContent() {
        return contents;
    }

    public String getContents() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOptional() {
        return optional;
    }
}

