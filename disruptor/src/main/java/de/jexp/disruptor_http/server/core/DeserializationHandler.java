package de.jexp.disruptor_http.server.core;

import com.lmax.disruptor.WorkHandler;
import de.jexp.disruptor_http.server.serialization.JsonDeserializer;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;


public class DeserializationHandler implements WorkHandler<RequestEvent> {
    JsonFactory jsonFactory = new JsonFactory(new ObjectMapper());

    public void onEvent(final RequestEvent event) throws Exception {
        final JsonDeserializer deserializer = new JsonDeserializer(jsonFactory, event.getContent());

        final Object data = deserializer.readObject();
        event.setDeserializedContent(data);
    }

}
