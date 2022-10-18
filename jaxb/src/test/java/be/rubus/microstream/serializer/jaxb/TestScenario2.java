package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.SomeData;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class TestScenario2
{

    @Test
    void test() throws JAXBException
    {
        // Data
        SomeData someData = new SomeData(42);

        // setup serializer
        XmlModel2 someDataXML = new XmlModel2();
        someDataXML.setSomeData(someData);

        // setup serializer
        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel2.class);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // Serializer
        final byte[] serializedContent;

        try
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(someDataXML, out);
            out.close();

            serializedContent = out.toByteArray();

        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final SomeData data;
        try
        {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedContent);
            data = ((XmlModel2) unmarshaller.unmarshal(inputStream)).getSomeData();

        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
