package nl.hsleiden.notifier.Model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Gson serializer for DateTime objects
 *
 * @author Ruben van Til
 */
public class gsonDateTimeSerializer implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {
    @Override
    public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
            return DateTime.parse(json.getAsString(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
    }
}