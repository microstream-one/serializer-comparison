package be.rubus.microstream.serializer.jvm;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

public class TestScenario3
{

    @Test
    void test() throws IOException
    {
        // Data
        final List<Shop> shops = GenerateData.testShopData(false);

        // setup serializer
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bytes);


        // Serializer
        final byte[] serializedContent;

        try
        {
            out.writeObject(shops);
            out.close();

            serializedContent = bytes.toByteArray();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Shop> data;
        final ByteArrayInputStream input = new ByteArrayInputStream(serializedContent);
        final ObjectInputStream in = new ObjectInputStream(input);

        try
        {
            data = (List<Shop>) in.readObject();

            in.close();

        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }

}
