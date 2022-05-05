package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("JAXB test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);

        final XmlModel data = new XmlModel();
        data.setProducts(allProducts);

        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel.class);
        final Marshaller marshaller = jaxbContext.createMarshaller();

        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        // warmup
        final byte[] bytes = serialize(data, marshaller);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(data, marshaller));
        }
        System.out.println("timings");
        System.out.println(timings);

        System.out.printf("Deserialize %n");
        timings.clear();
        for (int i = 0; i < 10; i++)
        {
            timings.add(deserializeWithTiming(bytes, unmarshaller));
        }
        System.out.println("timings");
        System.out.println(timings);

    }

    private static long serializeWithTiming(final XmlModel data, final Marshaller marshaller)
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(data, marshaller);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final XmlModel data, final Marshaller marshaller)
    {

        final byte[] bytes;
        try
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(data, out);
            out.close();

            bytes = out.toByteArray();
        } catch (final JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    private static long deserializeWithTiming(final byte[] bytes, final Unmarshaller unmarshaller)
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, unmarshaller);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static XmlModel deserialize(final byte[] bytes, final Unmarshaller unmarshaller)
    {

        final XmlModel data;
        try
        {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            data = (XmlModel) unmarshaller.unmarshal(inputStream);
        } catch (final JAXBException e)
        {
            throw new RuntimeException(e);
        }
        return data;


    }

}
