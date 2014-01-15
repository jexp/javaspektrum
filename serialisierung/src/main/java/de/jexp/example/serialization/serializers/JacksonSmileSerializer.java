package de.jexp.example.serialization.serializers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

import java.io.OutputStream;

/**
 * @author mh
 * @since 19.11.13
 */
public class JacksonSmileSerializer implements Serializer {

    private ObjectMapper mapper = new ObjectMapper(new SmileFactory().disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET))
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    private OutputStream os;

    @Override
    public void open(OutputStream os) throws Exception {
        this.os = os;
    }

    @Override
    public void serialize(Object object) throws Exception {
        mapper.writeValue(os, object);
    }

    @Override
    public void close() throws Exception {
        os.close();
    }
}
