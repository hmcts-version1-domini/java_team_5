package challenge;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class CustomerInvoicesDeserializer extends StdDeserializer<CustomerInvoices> {

    public CustomerInvoicesDeserializer() {
        this(null);
    }

    public CustomerInvoicesDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CustomerInvoices deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        int id = node.get("ID").asInt();
        int customerId = node.get("customerId").asInt();
        double amount = node.get("amount").asDouble();

        CustomerInvoices invoice = new CustomerInvoices();
        invoice.setId(id);
        invoice.setCustomerId(customerId);
        invoice.setAmount(amount);

        return invoice;
    }
}
