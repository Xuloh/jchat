package fr.insa.jchat.common.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Inet4AddressDeserializer implements JsonDeserializer<Inet4Address> {
    @Override
    public Inet4Address deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return (Inet4Address)Inet4Address.getByName(json.getAsString());
        }
        catch(UnknownHostException e) {
            return null;
        }
    }
}
