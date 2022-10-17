package be.rubus.microstream.serializer.hessian;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import be.rubus.microstream.serializer.hessian.custom.CustomSerializerFactory;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
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
public class HessianPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(HessianPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private Hessian2Output reusedSerializer;
    private Hessian2Input reusedDeserializer;

    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.testData = GenerateData.testData(100_000);

        SerializerFactory factory = SerializerFactory.createDefault();
        factory.addFactory(new CustomSerializerFactory());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.reusedSerializer = new Hessian2Output(bos);

        ByteArrayInputStream bis = new ByteArrayInputStream(new byte[]{});
        this.reusedDeserializer = new Hessian2Input(bis);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {


        final Hessian2Output mapper = new Hessian2Output(new ByteArrayOutputStream());

        this.serializedContent = new ArrayList<>();

        for (final SomeData input : this.testData)
        {

            try
            {
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mapper.init(bos);

                mapper.startMessage();
                mapper.writeObject(input);

                mapper.completeMessage();
                mapper.flush();

                bos.close();

                this.serializedContent.add(bos.toByteArray());

                mapper.free();

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

        final Hessian2Output mapper = new Hessian2Output(new ByteArrayOutputStream());

        try
        {
            for (final SomeData input : this.testData)
            {

                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mapper.init(bos);

                mapper.startMessage();
                mapper.writeObject(input);

                mapper.completeMessage();

                bos.close();
                blackhole.consume(bos.toByteArray());

                mapper.free();

            }
        } catch (final IOException e)
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
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();

                this.reusedSerializer.init(bos);
                this.reusedSerializer.startMessage();
                this.reusedSerializer.writeObject(input);

                this.reusedSerializer.completeMessage();

                bos.close();

                blackhole.consume(bos.toByteArray());

                this.reusedSerializer.free();
            }

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(final Blackhole blackhole)
    {

        ByteArrayInputStream bis = new ByteArrayInputStream(new byte[]{});
        final Hessian2Input mapper = new Hessian2Input(bis);

        try
        {
            for (final byte[] input : this.serializedContent)
            {
                final ByteArrayInputStream data = new ByteArrayInputStream(input);
                mapper.init(data);

                mapper.startMessage();
                blackhole.consume(mapper.readObject(SomeData.class));
                mapper.completeMessage();

                mapper.free();
            }

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }


    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeReuseMapper(final Blackhole blackhole)
    {

        try
        {
            for (final byte[] input : this.serializedContent)
            {
                final ByteArrayInputStream data = new ByteArrayInputStream(input);
                this.reusedDeserializer.init(data);

                this.reusedDeserializer.startMessage();
                blackhole.consume(this.reusedDeserializer.readObject(SomeData.class));
                this.reusedDeserializer.completeMessage();

                this.reusedDeserializer.free();
            }

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

    }

}
