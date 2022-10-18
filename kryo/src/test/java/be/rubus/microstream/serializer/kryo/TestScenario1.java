package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
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
        final Kryo kryo = new Kryo();

        kryo.register(ArrayList.class);
        kryo.register(Product.class);

        // Serializer
        final byte[] serializedContent;

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final Output output = new Output(bytes);

        kryo.writeClassAndObject(output, allProducts);
        output.close();

        serializedContent = bytes.toByteArray();


        // Deserialise
        final List<Product> data;

        final Input input = new Input(serializedContent);
        data = (List<Product>) kryo.readClassAndObject(input);
        input.close();

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
