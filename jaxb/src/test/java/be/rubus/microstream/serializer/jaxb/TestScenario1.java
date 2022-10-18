package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
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

public class TestScenario1
{

    @Test
    void test() throws JAXBException
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);
        XmlModel productsXML = new XmlModel();
        productsXML.setProducts(allProducts);

        // setup serializer
        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel.class);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // Serializer
        final byte[] serializedContent;

        try
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(productsXML, out);
            out.close();

            serializedContent = out.toByteArray();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // Deserialise
        final List<Product> data;
        try
        {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedContent);
            data = ((XmlModel) unmarshaller.unmarshal(inputStream)).getProducts();

        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
