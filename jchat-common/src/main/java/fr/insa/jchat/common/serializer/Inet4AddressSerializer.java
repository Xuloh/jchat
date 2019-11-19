package fr.insa.jchat.common.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.net.Inet4Address;

public class Inet4AddressSerializer implements JsonSerializer<Inet4Address> {
    @Override
    public JsonElement serialize(Inet4Address src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getHostAddress());
    }
}
