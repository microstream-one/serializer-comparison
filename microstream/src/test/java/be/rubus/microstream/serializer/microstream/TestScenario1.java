package be.rubus.microstream.serializer.microstream;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import one.microstream.persistence.binary.util.Serializer;
import one.microstream.persistence.binary.util.SerializerFoundation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
        final SerializerFoundation<?> foundation = SerializerFoundation.New();
        foundation.registerEntityTypes(ArrayList.class, Product.class);

        // Serializer
        final byte[] serializedContent;

        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {
            serializedContent = serializer.serialize(allProducts);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Product> data;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {
            data = serializer.deserialize(serializedContent);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
