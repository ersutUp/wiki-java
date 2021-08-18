package top.ersut.java.gson;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.time.*;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 时间戳，兼容10位时间戳解析
 */
public class TimestampTypeAdapter<T> extends TypeAdapter<T> {

    public final static TypeAdapterFactory DATE_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return type.getRawType() == Date.class ? (TypeAdapter<T>) new DateTypeAdapter() : null;
        }
    };

    public final static TypeAdapterFactory LOCALDATETIME_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return type.getRawType() == LocalDateTime.class ? (TypeAdapter<T>) new LocalDateTimeTypeAdapter() : null;
        }
    };

    final Class<T> clazz;

    private TimestampTypeAdapter(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 反序列化
     */
    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String json = in.nextString();
        //验证
        if (!Pattern.matches("\\d{10}|\\d{13}", json)) {
            throw new JsonSyntaxException(json, new Exception("not timestamp"));
        }

        int len = json.length();
        //10位时间戳进行修正
        if (len == 10) {
            json += "000";
        }

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(Long.valueOf(json)).atZone(ZoneId.systemDefault());
        if(clazz.isAssignableFrom(LocalDateTime.class)){
            return (T)zonedDateTime.toLocalDateTime();
        } else if(clazz.isAssignableFrom(Date.class)){
            return (T)Date.from(zonedDateTime.toInstant());
        }

        return null;
    }

    /**
     * 序列化
     */
    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        if(clazz.isAssignableFrom(LocalDateTime.class)){
            LocalDateTime localDateTime = (LocalDateTime) value;
            out.value(localDateTime.toEpochSecond(OffsetDateTime.now().getOffset())*1000);
        } else if(clazz.isAssignableFrom(Date.class)){
            Date date = (Date) value;
            out.value(date.getTime());
        }
    }


    public final static class LocalDateTimeTypeAdapter extends TimestampTypeAdapter<LocalDateTime>{

        public LocalDateTimeTypeAdapter() {
            super(LocalDateTime.class);
        }

    }
    public final static class DateTypeAdapter extends TimestampTypeAdapter<Date>{

        public DateTypeAdapter() {
            super(Date.class);
        }

    }

}
