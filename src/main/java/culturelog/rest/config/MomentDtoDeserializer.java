package culturelog.rest.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import culturelog.rest.domain.MomentType;
import culturelog.rest.dto.DateMomentDto;
import culturelog.rest.dto.MomentDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jan Venstermans
 * @see solution6 in http://programmerbruce.blogspot.be/2011/05/deserialize-json-with-jackson-into.html
 * @see http://www.baeldung.com/jackson-deserialization
 * @see https://stackoverflow.com/questions/25503005/stddeserializer-for-abstract-class-and-its-subtypes
 */
public class MomentDtoDeserializer extends StdDeserializer<MomentDto> {

    public MomentDtoDeserializer() {
        this(null);
//        TODO: put it in configuration somewhere?
//        registerByUniqueAttribute("displayDate", DateMomentDto.class);
    }

    public MomentDtoDeserializer(Class<?> vc) {
        super(vc);
    }

//    void registerByUniqueAttribute(String uniqueAttribute, Class<? extends MomentDto> aClass)
//    {
//        registry.put(uniqueAttribute, aClass);
//    }

    @Override
    public MomentDto deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
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
//        ObjectNode root = mapper.readTree(jp);
//        Class<? extends MomentDto> aClass = null;
//        Iterator<Map.Entry<String, JsonNode>> elementsIterator = root.fields();
//        while (elementsIterator.hasNext())
//        {
//            Map.Entry<String, JsonNode> element = elementsIterator.next();
//            String name = element.getKey();
//            if (registry.containsKey(name))
//            {
//                aClass = registry.get(name);
//                break;
//            }
//        }
//        if (aClass == null) {
//            return null;
//        }
//        return mapper.readValue(root.toString(), aClass);
    }
}
