package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import com.google.protobuf.InvalidProtocolBufferException;
import one.microstream.compare.serializer.proto.model.SomeDataOuterClass;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestScenario2
{

    @Test
    void test() throws InvalidProtocolBufferException
    {
        // Data
        SomeData someData = GenerateData.testData(1)
                .get(0);

        SomeDataOuterClass.SomeData variant = Helper.createProtoVariant(someData);

        // setup serializer

        // Serializer

        final byte[] serializedContent = variant.toByteArray();

        // Deserialise

        SomeDataOuterClass.SomeData dataProto = SomeDataOuterClass.SomeData.newBuilder()
                .mergeFrom(serializedContent)
                .build();


        SomeData data = Helper.createPojoVariant(dataProto);

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }

}
