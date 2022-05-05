package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.GenerateData;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JAXBPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(JAXBPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private XmlModel data;

    private Marshaller reusedMarshaller;

    private Unmarshaller reusedUnmarshaller;

    private byte[] serializedContent;

    @Setup
    public void init() throws JAXBException, IOException
    {
        this.data = new XmlModel();
        this.data.setProducts(GenerateData.products(10_000));

        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel.class);
        this.reusedMarshaller = jaxbContext.createMarshaller();

        this.reusedUnmarshaller = jaxbContext.createUnmarshaller();

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData() throws JAXBException, IOException
    {
        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel.class);
        final Marshaller marshaller = jaxbContext.createMarshaller();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(this.data, out);
        out.close();

        this.serializedContent = out.toByteArray();

    }

    @TearDown
    public void shutdown() throws Exception
    {
        // So that we are consistent with all other tests
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeWithInitialization()
    {

        final byte[] bytes;
        try
        {
            final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();


            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(this.data, out);
            out.close();

            bytes = out.toByteArray();
        } catch (final JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeReuseMapper()
    {
        final byte[] bytes;
        try
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            this.reusedMarshaller.marshal(this.data, out);
            out.close();

            bytes = out.toByteArray();
        } catch (final JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public XmlModel deserializeWithInitialization()
    {
        final XmlModel data;
        try
        {
            final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel.class);

            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(this.serializedContent);
            data = (XmlModel) unmarshaller.unmarshal(inputStream);
        } catch (final JAXBException e)
        {
            throw new RuntimeException(e);
        }
        return data;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public XmlModel deserializeReuseMapper()
    {

        final XmlModel data;
        try
        {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(this.serializedContent);
            data = (XmlModel) this.reusedUnmarshaller.unmarshal(inputStream);
        } catch (final JAXBException e)
        {
            throw new RuntimeException(e);
        }
        return data;

    }
}
