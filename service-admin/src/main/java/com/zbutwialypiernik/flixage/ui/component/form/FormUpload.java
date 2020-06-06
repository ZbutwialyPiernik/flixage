package com.zbutwialypiernik.flixage.ui.component.form;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.zbutwialypiernik.flixage.service.resource.AbstractResource;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Log4j2
public abstract class FormUpload<T extends AbstractResource> extends CustomField<T> {

    private T resource;

    /**
     *
     * @param maxFileSize the max file size of uploaded file
     * @param acceptedTypes the list of accepted mime types
     */
    protected FormUpload(int maxFileSize, Set<String> acceptedTypes) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(acceptedTypes.toArray(new String[0]));
        upload.setMaxFiles(1);
        upload.setMaxFileSize(maxFileSize);
        upload.addFinishedListener(event -> {
            try (var inputStream = buffer.getInputStream()) {
                resource = transform(inputStream, buffer.getFileData());
                updateValue();
            } catch (IOException e) {
                log.error("Error during upload of file: ", e);
            }
        });

        add(upload);
    }

    protected abstract T transform(InputStream inputStream, FileData fileData) throws IOException;

    @Override
    protected T generateModelValue() {
        return resource;
    }

    @Override
    protected void setPresentationValue(T newPresentationValue) {
        throw new IllegalStateException("Operation is not supported on server-side");
    }

}
