package fr.insa.jchat.common.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import fr.insa.jchat.common.Message;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.UUID;

public class MessageSerializer implements JsonSerializer<Message> {

    @Override
    public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        Optional
            .ofNullable(message.getUuid())
            .ifPresentOrElse(
                uuid -> result.add("uuid", new JsonPrimitive(uuid.toString())),
                () -> result.add("uuid", JsonNull.INSTANCE)
            );

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

        Optional
            .ofNullable(message.getDate())
            .ifPresentOrElse(
                date -> result.add("date", new JsonPrimitive(date.getTimeInMillis())),
                () -> result.add("date", JsonNull.INSTANCE)
            );

        return result;
    }
}
