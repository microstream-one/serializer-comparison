package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TestScenario3
{

    // XML cannot handle circular references.

    @Test
    void test() throws JAXBException
    {
        // Data
        final List<Shop> shops = GenerateData.testShopData(false);
        final XmlModel3 shopXML = new XmlModel3();
        shopXML.setShops(shops);

        // setup serializer
        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel3.class);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // Serializer
        final byte[] serializedContent;

        try
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(shopXML, out);
            out.close();

            serializedContent = out.toByteArray();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Shop> data;
        try
        {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedContent);
            data = ((XmlModel3) unmarshaller.unmarshal(inputStream)).getShops();

        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }

}
