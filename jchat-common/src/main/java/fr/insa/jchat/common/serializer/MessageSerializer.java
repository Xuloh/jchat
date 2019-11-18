package fr.insa.jchat.common.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import fr.insa.jchat.common.Message;

import java.lang.reflect.Type;

public class MessageSerializer implements JsonSerializer<Message> {

    @Override
    public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("uuid", new JsonPrimitive(message.getUuid().toString()));
        result.add("text", new JsonPrimitive(message.getText()));

        JsonArray userTags = new JsonArray();
        message.getUserTags().forEach(tag -> userTags.add(new JsonPrimitive(tag)));
        result.add("userTags", userTags);

        JsonArray links = new JsonArray();
        message.getLinks().forEach(link -> userTags.add(new JsonPrimitive(link)));
        result.add("links", links);

        JsonPrimitive sender = message.getSender() == null ? null : new JsonPrimitive(message.getSender().getUsername());
        result.add("sender", sender);

        JsonPrimitive recipient = message.getRecipient() == null ? null : new JsonPrimitive(message.getRecipient().getUsername());
        result.add("recipient", recipient);

        result.add("date", new JsonPrimitive(message.getDate().getTimeInMillis()));

        return result;
    }
}
