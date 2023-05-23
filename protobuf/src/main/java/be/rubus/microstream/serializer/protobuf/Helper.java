package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.SomeData;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import one.microstream.compare.serializer.proto.model.ProductOuterClass;
import one.microstream.compare.serializer.proto.model.SomeDataOuterClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public final class Helper
{

    private Helper()
    {
    }

    public static Product createPojoVariant(final ProductOuterClass.Product product)
    {
        final Product result = new Product();
        result.setId(product.getId());
        result.setName(product.getName());
        result.setDescription(product.getDescription());
        result.setRating(product.getRating());
        return result;
    }

    public static SomeData createPojoVariant(final SomeDataOuterClass.SomeData someData)
    {
        final SomeData result = new SomeData();
        result.setName(someData.getName());
        result.setValue(someData.getValue());
        result.setNow(toLocalDateTime(someData.getNow()));
        result.setX(someData.getX());
        result.setBig(toBigDecimal(someData.getBig()));
        result.setIntArray(someData.getIntArrayList().stream()
                                   .mapToInt(i -> i)  // From Integer to int
                                   .toArray());

        return result;
    }

    public static ProductOuterClass.Product createProtoVariant(final Product product)
    {
        return ProductOuterClass.Product.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setRating(product.getRating())
                .build();
    }

    public static SomeDataOuterClass.SomeData createProtoVariant(final SomeData someData)
    {
        SomeDataOuterClass.SomeData.Builder builder = SomeDataOuterClass.SomeData.newBuilder()
                .setName(someData.getName())
                .setValue(someData.getValue())
                .setNow(toGoogleTimestamp(someData.getNow()))
                .setX(someData.getX())
                .setBig(toDecimalValue(someData.getBig()));
        Arrays.stream(someData.getIntArray())
                .forEach(builder::addIntArray);
        return builder.build();
    }

    private static SomeDataOuterClass.DecimalValue toDecimalValue(final BigDecimal big)
    {
        return SomeDataOuterClass.DecimalValue.newBuilder()
                .setScale(big.scale())
                .setPrecision(big.precision())
                .setValue(ByteString.copyFrom(big.unscaledValue()
                                                      .toByteArray()))
                .build();
    }

    private static BigDecimal toBigDecimal(final SomeDataOuterClass.DecimalValue big)
    {
        BigInteger unscaled = new BigInteger(big.getValue()
                                                     .toByteArray());
        return new BigDecimal(unscaled, big.getScale());

    }

    private static Timestamp toGoogleTimestamp(final LocalDateTime localDateTime)
    {
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private static LocalDateTime toLocalDateTime(final Timestamp timestamp)
    {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());

        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

}
