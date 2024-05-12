package com.erijl.flightvisualizer.backend.config.converter;

import com.google.protobuf.Message;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ProtobufJsonFormatHttpMessageConverter implements HttpMessageConverter<List<Message>> {

    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    public List<Message> read(Class<? extends List<Message>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        // This converter can only write to application/x-protobuf media type
        return List.class.isAssignableFrom(clazz) && new MediaType("application", "x-protobuf").equals(mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(new MediaType("application", "x-protobuf"));
    }

    @Override
    public void write(List<Message> messages, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        for (Message message : messages) {
            String base64 = Base64.getEncoder().encodeToString(message.toByteArray());
            outputMessage.getBody().write(base64.getBytes());
        }
    }

}