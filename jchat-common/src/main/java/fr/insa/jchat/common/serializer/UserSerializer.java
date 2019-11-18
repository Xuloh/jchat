package fr.insa.jchat.common.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import fr.insa.jchat.common.User;

import java.lang.reflect.Type;

public class UserSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("username", new JsonPrimitive(user.getUsername()));
        result.add("password", new JsonPrimitive(user.getPassword()));
        result.add("image", new JsonPrimitive(user.getImage().getAbsolutePath()));
        result.add("color", new JsonPrimitive(user.getColor()));
        return result;
    }
}
