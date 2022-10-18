package be.rubus.microstream.serializer.gson;

import be.rubus.microstream.serializer.data.SomeData;
import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class TestScenario2
{

    @Test
    void test()
    {
        // Data
        SomeData someData = new SomeData(42);

        // setup serializer
        Gson gson = new Gson();

        // Serializer
        final byte[] serializedContent;

        serializedContent = gson.toJson(someData)
                .getBytes(StandardCharsets.UTF_8);

        // Deserialise
        final SomeData data;
        try
        {
            data = gson.fromJson(new String(serializedContent), SomeData.class);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
