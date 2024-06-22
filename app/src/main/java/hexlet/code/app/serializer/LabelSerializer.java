package hexlet.code.app.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import hexlet.code.app.label.Label;

import java.io.IOException;

public class LabelSerializer extends StdSerializer<Label> {
    public LabelSerializer() {
        this(null);
    }

    public LabelSerializer(Class<Label> t) {
        super(t);
    }

    @Override
    public void serialize(Label label, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", label.getName());
        jsonGenerator.writeEndObject();
    }
}
