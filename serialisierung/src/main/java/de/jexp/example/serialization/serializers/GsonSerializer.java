package de.jexp.example.serialization.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import de.jexp.example.serialization.serializers.Serializer;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author mh
 * @since 19.11.13
 */
public class GsonSerializer implements Serializer {

    private Gson gson = new Gson();
    private OutputStreamWriter writer;

    @Override
    public void open(OutputStream os) throws Exception {
        writer = new OutputStreamWriter(os);
    }

    @Override
    public void serialize(Object object) throws Exception {
        gson.toJson(object,writer);
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
