package be.rubus.microstream.serializer.gson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestScenario1
{

    @Test
    void test()
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);

        // setup serializer
        Gson gson = new Gson();

        // Serializer
        final byte[] serializedContent;


        serializedContent = gson.toJson(allProducts)
                .getBytes(StandardCharsets.UTF_8);

        // Deserialise
        final List<Product> data;
        try
        {
            data = gson.fromJson(new String(serializedContent), new TypeToken<ArrayList<Product>>()
            {
            }.getType());
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
