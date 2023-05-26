package be.rubus.microstream.serializer.microstream;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import be.rubus.microstream.serializer.data.shop.Shop;
import one.microstream.persistence.binary.util.Serializer;
import one.microstream.persistence.binary.util.SerializerFoundation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestScenario3
{

    @Test
    void test()
    {
        // Data
        final List<Shop> shops = GenerateData.testShopData(false);

        // setup serializer
        final SerializerFoundation<?> foundation = SerializerFoundation.New();
        foundation.registerEntityTypes(SomeData.class);

        // Serializer
        final byte[] serializedContent;

        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {
            serializedContent = serializer.serialize(shops);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Shop> data;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {
            data = serializer.deserialize(serializedContent);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }
}
