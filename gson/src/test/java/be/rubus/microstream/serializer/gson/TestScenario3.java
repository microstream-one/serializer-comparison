package be.rubus.microstream.serializer.gson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestScenario3
{

    // JSON cannot handle circular references.

    @Test
    void test()
    {
        // Data
        final List<Shop> shops = GenerateData.testShopData(false);

        // setup serializer
        final Gson gson = new Gson();

        // Serializer
        final byte[] serializedContent = gson.toJson(shops)
                .getBytes(StandardCharsets.UTF_8);

        // Deserialise
        final List<Shop> data;
        try
        {
            data = gson.fromJson(new String(serializedContent), new TypeToken<ArrayList<Shop>>()
            {
            }.getType());
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }

}
