package be.rubus.microstream.serializer.jvm;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

public class TestScenario1
{

    @Test
    void test() throws IOException
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);

        // setup serializer
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bytes);


        // Serializer
        final byte[] serializedContent;

        try
        {
            out.writeObject(allProducts);
            out.close();

            serializedContent = bytes.toByteArray();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Product> data;
        final ByteArrayInputStream input = new ByteArrayInputStream(serializedContent);
        final ObjectInputStream in = new ObjectInputStream(input);

        try
        {
            data = (List<Product>) in.readObject();

            in.close();

        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
