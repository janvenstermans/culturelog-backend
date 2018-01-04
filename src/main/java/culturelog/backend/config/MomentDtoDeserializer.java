package culturelog.backend.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import culturelog.backend.domain.MomentType;
import culturelog.backend.dto.DateMomentDto;
import culturelog.backend.dto.MomentDto;

import java.io.IOException;

/**
 * Deserializing abstract class {@link MomentDto} is done via attribute {@link MomentDto#momentType}.
 *
 * @author Jan Venstermans
 * @see solution6 in http://programmerbruce.blogspot.be/2011/05/deserialize-json-with-jackson-into.html
 * @see http://www.baeldung.com/jackson-deserialization
 * @see https://stackoverflow.com/questions/25503005/stddeserializer-for-abstract-class-and-its-subtypes
 */
public class MomentDtoDeserializer extends StdDeserializer<MomentDto> {

    public MomentDtoDeserializer() {
        this(null);
    }

    public MomentDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MomentDto deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);
        String typeString = node.get("momentType").textValue();
        MomentType momentType = MomentType.valueOf(typeString);
        switch (momentType) {
            case DATE:
                return mapper.treeToValue(node, DateMomentDto.class);
            default:
                throw new IllegalArgumentException("momentType " + momentType + " cannot be deserialized");
        }
    }
}
