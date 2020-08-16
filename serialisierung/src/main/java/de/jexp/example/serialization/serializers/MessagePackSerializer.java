package de.jexp.example.serialization.serializers;

import de.jexp.example.serialization.*;
import org.msgpack.MessagePack;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * @author mh
 * @since 22.11.13
 */
public class MessagePackSerializer implements Serializer {

    private final static MessagePack messagePack = new MessagePack();
    static {
        messagePack.register(Child.class);
        messagePack.register(Root.class);
    }

    private OutputStream os;

    public MessagePackSerializer() {
    }

    @Override
    public void open(OutputStream os) throws Exception {
        this.os = new BufferedOutputStream(os);
    }

    @Override
    public void serialize(Object object) throws Exception {
        messagePack.write(os,object);
    }

    @Override
    public void close() throws Exception {
        this.os.close();
    }
}
