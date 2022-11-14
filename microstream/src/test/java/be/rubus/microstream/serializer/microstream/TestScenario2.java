package be.rubus.microstream.serializer.microstream;

import be.rubus.microstream.serializer.data.SomeData;
import one.microstream.persistence.binary.util.Serializer;
import one.microstream.persistence.binary.util.SerializerFoundation;
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
        final SerializerFoundation<?> foundation = SerializerFoundation.New();
        foundation.registerEntityTypes(SomeData.class);

        // Serializer
        final byte[] serializedContent;

        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {
            serializedContent = serializer.serialize(someData);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final SomeData data;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {
            data = serializer.deserialize(serializedContent);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
