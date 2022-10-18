package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.SomeData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestScenario2
{

    @Test
    void test()
    {
        // Data
        SomeData someData = new SomeData(42);

        // setup serializer
        final Kryo kryo = new Kryo();

        kryo.register(SomeData.class);
        kryo.register(LocalDateTime.class);
        kryo.register(BigDecimal.class);
        kryo.register(int[].class);

        // Serializer
        final byte[] serializedContent;

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final Output output = new Output(bytes);

        kryo.writeClassAndObject(output, someData);
        output.close();

        serializedContent = bytes.toByteArray();


        // Deserialise
        final SomeData data;

        final Input input = new Input(serializedContent);
        data = (SomeData) kryo.readClassAndObject(input);
        input.close();

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
