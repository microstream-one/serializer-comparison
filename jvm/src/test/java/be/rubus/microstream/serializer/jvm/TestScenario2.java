package be.rubus.microstream.serializer.jvm;

import be.rubus.microstream.serializer.data.SomeData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

public class TestScenario2
{

    @Test
    void test() throws IOException
    {
        // Data
        SomeData someData = new SomeData(42);

        // setup serializer
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bytes);

        // Serializer
        final byte[] serializedContent;

        try
        {
            out.writeObject(someData);
            out.close();

            serializedContent = bytes.toByteArray();


        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final SomeData data;

        final ByteArrayInputStream input = new ByteArrayInputStream(serializedContent);
        final ObjectInputStream in = new ObjectInputStream(input);

        try
        {
            data = (SomeData) in.readObject();

            in.close();


        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
