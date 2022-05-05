package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JAXBPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(JAXBPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private Marshaller reusedMarshaller;

    private Unmarshaller reusedUnmarshaller;

    private List<byte[]> serializedContent;

    @Setup
    public void init() throws JAXBException
    {
        this.testData = GenerateData.testData(100_000);

        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel2.class);
        this.reusedMarshaller = jaxbContext.createMarshaller();

        this.reusedUnmarshaller = jaxbContext.createUnmarshaller();

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData() throws JAXBException
    {
        final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel2.class);
        final Marshaller marshaller = jaxbContext.createMarshaller();

        this.serializedContent = new ArrayList<>();

        for (final SomeData input : this.testData)
        {

            try
            {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final XmlModel2 model2 = new XmlModel2();
                model2.setSomeData(input);
                marshaller.marshal(model2, out);
                out.close();
                this.serializedContent.add(out.toByteArray());

            } catch (final IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @TearDown
    public void shutdown() throws Exception
    {
        // So that we are consistent with all other tests
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeWithInitialization(final Blackhole blackhole)
    {

        try
        {
            final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel2.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();

            for (final SomeData input : this.testData)
            {
                final XmlModel2 model2 = new XmlModel2();
                model2.setSomeData(input);

                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                marshaller.marshal(model2, out);
                out.close();
                blackhole.consume(out.toByteArray());
            }

        } catch (final JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeReuseMapper(final Blackhole blackhole)
    {

        try
        {

            for (final SomeData input : this.testData)
            {
                final XmlModel2 model2 = new XmlModel2();
                model2.setSomeData(input);

                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                this.reusedMarshaller.marshal(model2, out);
                out.close();
                blackhole.consume(out.toByteArray());
            }

        } catch (final JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(final Blackhole blackhole)
    {

        XmlModel2 data;
        try
        {
            final JAXBContext jaxbContext = JAXBContext.newInstance(XmlModel2.class);

            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            for (final byte[] input : this.serializedContent)
            {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
                data = (XmlModel2) unmarshaller.unmarshal(inputStream);
                inputStream.close();
                blackhole.consume(data);
            }
        } catch (final JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeReuseMapper(final Blackhole blackhole)
    {

        XmlModel2 data;
        try
        {

            for (final byte[] input : this.serializedContent)
            {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
                data = (XmlModel2) this.reusedUnmarshaller.unmarshal(inputStream);
                inputStream.close();
                blackhole.consume(data);
            }
        } catch (final JAXBException | IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}
