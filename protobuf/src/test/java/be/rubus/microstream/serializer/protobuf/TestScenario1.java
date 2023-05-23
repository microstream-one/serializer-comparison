package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import one.microstream.compare.serializer.proto.model.ProductOuterClass;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TestScenario1
{

    @Test
    void test()
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);

        final ProductOuterClass.ProductList.Builder productListBuilder = ProductOuterClass.ProductList.newBuilder();
        allProducts.stream()
                .map(Helper::createProtoVariant)
                .forEach(productListBuilder::addProductEntity);

        final ProductOuterClass.ProductList productList = productListBuilder.build();

        // setup serializer

        // Serializer

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            productList.writeTo(bytes);
            bytes.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        final byte[] serializedContent = bytes.toByteArray();


        // Deserialise
        final List<Product> data;

        final ByteArrayInputStream in = new ByteArrayInputStream(serializedContent);

        final ProductOuterClass.ProductList list;
        try
        {
            list = ProductOuterClass.ProductList.newBuilder()
                    .mergeFrom(in)
                    .build();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        data = list.getProductEntityList()
                .stream()
                .map(Helper::createPojoVariant)
                .collect(Collectors.toList());

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
