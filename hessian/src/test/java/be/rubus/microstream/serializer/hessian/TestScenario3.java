package be.rubus.microstream.serializer.hessian;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TestScenario3
{

    // This fails due to the circular data!!
    // Although documentation says it should be able to handle circular references!!

    @Test
    void test()
    {
        // Data
        final List<Shop> shops = GenerateData.testShopData(false);

        // setup serializer
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final Hessian2Output outputMapper = new Hessian2Output(bos);


        // Serializer
        final byte[] serializedContent;

        try
        {
            outputMapper.startMessage();
            outputMapper.writeObject(shops);

            outputMapper.completeMessage();
            outputMapper.flush();

            bos.close();
            serializedContent = bos.toByteArray();
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final ByteArrayInputStream bis = new ByteArrayInputStream(serializedContent);
        final Hessian2Input inputMapper = new Hessian2Input(bis);


        final List<Shop> data;
        try
        {
            inputMapper.startMessage();
            data = (List<Shop>) inputMapper.readObject(List.class);
            inputMapper.completeMessage();

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }


        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }

}
