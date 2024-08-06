package com.nsn.nsnupdatesbackend.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class APIExceptionSerializer extends JsonSerializer<APIException> {
    @Override
    public void serialize(APIException e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("path", e.getPath());
        jsonGenerator.writeStringField("status", e.getStatus().toString());
        jsonGenerator.writeStringField("message", e.getMessage());
        jsonGenerator.writeObjectField("timestamp", e.getTimestamp());
        jsonGenerator.writeEndObject();
    }
}
