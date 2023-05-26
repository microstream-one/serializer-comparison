package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import com.google.protobuf.InvalidProtocolBufferException;
import one.microstream.compare.serializer.proto.model.SomeDataOuterClass;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class ProtobufPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(ProtobufPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> originalTestData = new ArrayList<>();

    private List<SomeDataOuterClass.SomeData> testData = new ArrayList<>();


    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.originalTestData = GenerateData.testData(100_000);
        this.testData = new ArrayList<>();

        this.originalTestData.stream()
                .map(Helper::createProtoVariant)
                .forEach(this.testData::add);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        this.serializedContent = new ArrayList<>();

        for (final SomeDataOuterClass.SomeData input : this.testData)
        {
            this.serializedContent.add(input.toByteArray());
        }

    }

    @TearDown
    public void shutdown() throws Exception
    {
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serialize(final Blackhole blackhole)
    {

        for (final SomeDataOuterClass.SomeData input : this.testData)
        {
            blackhole.consume(input.toByteArray());
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeWithMapping(final Blackhole blackhole)
    {

        for (final SomeData input : this.originalTestData)
        {
            blackhole.consume(Helper.createProtoVariant(input).toByteArray());
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserialize(final Blackhole blackhole)
    {

        for (final byte[] input : this.serializedContent)
        {

            try
            {
                final SomeDataOuterClass.SomeData data = SomeDataOuterClass.SomeData.newBuilder()
                        .mergeFrom(input)
                        .build();
                blackhole.consume(data);
            } catch (InvalidProtocolBufferException e)
            {
                throw new RuntimeException(e);
            }
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithMapping(final Blackhole blackhole)
    {

        for (final byte[] input : this.serializedContent)
        {
            try
            {
                final SomeDataOuterClass.SomeData data = SomeDataOuterClass.SomeData.newBuilder()
                        .mergeFrom(input)
                        .build();
                blackhole.consume(Helper.createPojoVariant(data)) ;
            } catch (InvalidProtocolBufferException e)
            {
                throw new RuntimeException(e);
            }
        }

    }
}
