package Fridge_Chef.team.common;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CustomMockPartFile implements CustomPart, MultipartFile{
    private final String name;

    private final String originalFilename;
    private final String contentType;

    private final byte[] content;
    private final String description;
    private final boolean optional;


    public CustomMockPartFile(String name, @Nullable byte[] content) {
        this(name, "", null, content);
    }

    public CustomMockPartFile(String name, InputStream contentStream) throws IOException {
        this(name, "", null, FileCopyUtils.copyToByteArray(contentStream));
    }

    public CustomMockPartFile(
            String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content) {
        this(name, originalFilename, contentType, content, "", true);
    }

    public CustomMockPartFile(String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content, String description, boolean option) {
        Assert.hasLength(name, "Name must not be empty");
        this.name = name;
        this.originalFilename = (originalFilename != null ? originalFilename : "");
        this.contentType = contentType;
        this.content = (content != null ? content : new byte[0]);
        this.description = description;
        this.optional = option;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @NonNull
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return (this.content.length == 0);
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }

    public String getDescription() {
        return description;
    }

    public boolean isOptional() {
        return optional;
    }
}
