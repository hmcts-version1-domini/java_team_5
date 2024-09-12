package challenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CustomerSpendingCalculator {

    public static void main(String[] args) {
        try {

            String customersJson = fetchDataFromServer("http://localhost:9090");
            String invoicesJson = fetchDataFromServer("http://localhost:9092");

            List<CustomerDetails> customers = deserializeCustomers(customersJson);
            List<CustomerInvoices> invoices = deserializeInvoices(invoicesJson);

            Map<Integer, Double> customerSpending = calculateCustomerSpending(invoices);

            Optional<Map.Entry<Integer, Double>> maxSpendingEntry = customerSpending.entrySet().stream()
                .max(Map.Entry.comparingByValue());

            if (maxSpendingEntry.isPresent()) {
                int maxCustomerId = maxSpendingEntry.get().getKey();
                double maxAmountSpent = maxSpendingEntry.get().getValue();

                CustomerDetails topCustomer = customers.stream()
                    .filter(c -> c.getId() == maxCustomerId)
                    .findFirst()
                    .orElse(null);

                if (topCustomer != null) {
                    System.out.println("Customer who spent the most money:");
                    System.out.println("Name: " + topCustomer.getName());
                    System.out.println("Surname: " + topCustomer.getSurname());
                    System.out.println("Total Amount Spent: $" + maxAmountSpent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchDataFromServer(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();
        return content.toString();
    }

    private static List<CustomerDetails> deserializeCustomers(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CustomerDetails.class, new CustomerDetailsDeserializer());
        mapper.registerModule(module);

        JsonNode customersNode = mapper.readTree(json).get("customers");
        List<CustomerDetails> customers = new ArrayList<>();
        for (JsonNode node : customersNode) {
            CustomerDetails customer = mapper.treeToValue(node, CustomerDetails.class);
            customers.add(customer);
        }
        return customers;
    }

    private static List<CustomerInvoices> deserializeInvoices(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CustomerInvoices.class, new CustomerInvoicesDeserializer());
        mapper.registerModule(module);

        JsonNode invoicesNode = mapper.readTree(json).get("invoices");
        List<CustomerInvoices> invoices = new ArrayList<>();
        for (JsonNode node : invoicesNode) {
            CustomerInvoices invoice = mapper.treeToValue(node, CustomerInvoices.class);
            invoices.add(invoice);
        }
        return invoices;
    }

    private static Map<Integer, Double> calculateCustomerSpending(List<CustomerInvoices> invoices) {
        Map<Integer, Double> spendingMap = new HashMap<>();
        for (CustomerInvoices invoice : invoices) {
            int customerId = invoice.getCustomerId();
            double amount = invoice.getAmount();
            spendingMap.put(customerId, spendingMap.getOrDefault(customerId, 0.0) + amount);
        }
        return spendingMap;
    }
}

