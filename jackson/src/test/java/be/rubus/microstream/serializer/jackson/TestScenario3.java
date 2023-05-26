package be.rubus.microstream.serializer.jackson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestScenario3
{

    // Jackson cannot handle circular data.
    @Test
    void test()
    {
        // Data
        final List<Shop> shops = GenerateData.testShopData(false);

        // setup serializer
        final ObjectMapper mapper = new ObjectMapper();

        // Serializer
        final byte[] serializedContent;

        try
        {
            serializedContent = mapper.writeValueAsBytes(shops);
        } catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Shop> data;
        try
        {
            data = mapper.readValue(serializedContent, new TypeReference<List<Shop>>()
            {
            });
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }

}
