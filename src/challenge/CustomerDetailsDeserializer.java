package challenge;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CustomerDetailsDeserializer extends StdDeserializer<CustomerDetails> {

    public CustomerDetailsDeserializer() {
        this(null);
    }

    public CustomerDetailsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CustomerDetails deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        int id = node.get("ID").asInt();
        String name = node.get("name").asText();
        String surname = node.get("surname").asText();

        CustomerDetails customer = new CustomerDetails();
        customer.setId(id);
        customer.setName(name);
        customer.setSurname(surname);

        return customer;
    }
}