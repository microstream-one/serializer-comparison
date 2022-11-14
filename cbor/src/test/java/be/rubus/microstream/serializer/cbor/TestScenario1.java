package be.rubus.microstream.serializer.cbor;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestScenario1
{

    @Test
    void test()
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);

        // setup serializer
        CBORMapper mapper = new CBORMapper();

        // Serializer
        final byte[] serializedContent;


        try
        {
            serializedContent = mapper.writeValueAsBytes(allProducts);
        } catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Product> data;
        try
        {
            data = mapper.readValue(serializedContent, new TypeReference<List<Product>>()
            {
            });
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
