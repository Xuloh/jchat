package fr.insa.jchat.common.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.lang.reflect.Type;

public class FileSerializer implements JsonSerializer<File> {

    @Override
    public JsonElement serialize(File src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getAbsolutePath());
    }
}
