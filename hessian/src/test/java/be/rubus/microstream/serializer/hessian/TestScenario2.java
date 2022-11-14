package be.rubus.microstream.serializer.hessian;

import be.rubus.microstream.serializer.data.SomeData;
import be.rubus.microstream.serializer.hessian.custom.CustomSerializerFactory;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestScenario2
{

    @Test
    void test()
    {
        // Data
        SomeData someData = new SomeData(42);

        // Setup framework
        final SerializerFactory factory = SerializerFactory.createDefault();
        factory.addFactory(new CustomSerializerFactory());

        // setup serializer
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final Hessian2Output outputMapper = new Hessian2Output(bos);


        // Serializer
        final byte[] serializedContent;

        try
        {
            outputMapper.startMessage();
            outputMapper.writeObject(someData);

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

        final SomeData data;
        try
        {
            inputMapper.startMessage();
            data = (SomeData) inputMapper.readObject(SomeData.class);
            inputMapper.completeMessage();

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
