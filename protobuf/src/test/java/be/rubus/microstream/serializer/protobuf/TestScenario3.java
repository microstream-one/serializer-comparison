package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import com.google.protobuf.InvalidProtocolBufferException;
import one.microstream.compare.serializer.proto.model.ShopOuterClass;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TestScenario3
{

    @Test
    void testSingle() throws InvalidProtocolBufferException
    {
        final List<Shop> shops = GenerateData.testShopData(false);
        final ShopOuterClass.Shop variant = Helper.createProtoVariant(shops.get(0));

        // setup serializer

        // Serializer
        final byte[] serializedContent = variant.toByteArray();

        // Deserialise
        final ShopOuterClass.Shop dataProto = ShopOuterClass.Shop.newBuilder()
                .mergeFrom(serializedContent)
                .build();


        final Shop data = Helper.createPojoVariant(dataProto);
        final List<Shop> allShops = Arrays.asList(data);

        Helper.useShopReference(allShops);

        // test for equality based on fields, not equals()
        Assertions.assertThat(data)
                .usingRecursiveComparison()
                .isEqualTo(shops.get(0));
    }


    @Test
    void testMultiple() throws InvalidProtocolBufferException
    {
        final List<Shop> shops = GenerateData.testShopData(false);
        final ShopOuterClass.Shops variant = Helper.createProtoVariant(shops);

        final byte[] serializedContent = variant.toByteArray();

        // Deserialise
        final ShopOuterClass.Shops dataProto = ShopOuterClass.Shops.newBuilder()
                .mergeFrom(serializedContent)
                .build();

        final List<Shop> data = Helper.createPojoVariant(dataProto);
        Helper.useShopReference(data);
        for (int i = 0; i < shops.size(); i++)
        {
            // test for equality based on fields, not equals()
            Assertions.assertThat(data.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(shops.get(i));
        }

    }
}
