package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestScenario3
{

    @Test
    void test()
    {
        // Data
        List<Shop> shops = GenerateData.testShopData(false);

        // setup serializer
        final Kryo kryo = new Kryo();

        kryo.setReferences(true);  // Important in this case
        kryo.register(ArrayList.class);
        kryo.register(Shop.class);
        kryo.register(Order.class);
        kryo.register(OrderLine.class);
        kryo.register(ShopProduct.class);
        kryo.register(StockItem.class);
        kryo.register(Warehouse.class);
        kryo.register(LocalDate.class);

        // Serializer
        final byte[] serializedContent;

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final Output output = new Output(bytes);

        kryo.writeClassAndObject(output, shops);
        output.close();

        serializedContent = bytes.toByteArray();


        // Deserialise
        final List<Shop> data;

        final Input input = new Input(serializedContent);
        data = (List<Shop>) kryo.readClassAndObject(input);
        input.close();

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }
}
