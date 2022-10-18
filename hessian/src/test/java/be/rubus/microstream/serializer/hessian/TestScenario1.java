package be.rubus.microstream.serializer.hessian;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TestScenario1
{

    @Test
    void test()
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);

        // setup serializer
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final Hessian2Output outputMapper = new Hessian2Output(bos);


        // Serializer
        final byte[] serializedContent;

        try
        {
            outputMapper.startMessage();
            outputMapper.writeObject(allProducts);

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


        final List<Product> data;
        try
        {
            inputMapper.startMessage();
            data = (List<Product>) inputMapper.readObject(List.class);
            inputMapper.completeMessage();

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }


        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
