package nl.hsleiden.notifier.Model;

import com.activeandroid.serializer.TypeSerializer;

import org.joda.time.DateTime;

/**
 * Created by Ruben van Til on 22-1-2017.
 */

public final class dbDateTimeSerializer extends TypeSerializer {

    @Override
    public Class<?> getDeserializedType() {
        return DateTime.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return long.class;
    }

    @Override
    public Long serialize(Object data) {
        if (data == null) {
            return null;
        }
        return ((DateTime) data).getMillis();
    }

    @Override
    public DateTime deserialize(Object data) {
        if (data == null) {
            return null;
        }
        return new DateTime(data);
    }
}
