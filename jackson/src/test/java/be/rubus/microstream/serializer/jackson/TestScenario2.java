package be.rubus.microstream.serializer.jackson;

import be.rubus.microstream.serializer.data.SomeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestScenario2
{

    @Test
    void test()
    {
        // Data
        SomeData someData = new SomeData(42);

        // setup serializer
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Serializer
        final byte[] serializedContent;

        try
        {
            serializedContent = mapper.writeValueAsBytes(someData);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final SomeData data;
        try
        {
            data = mapper.readValue(serializedContent, SomeData.class);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
