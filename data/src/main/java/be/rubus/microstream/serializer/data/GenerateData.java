package be.rubus.microstream.serializer.data;

import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GenerateData
{

    private static final FakeValuesService faker = new FakeValuesService(
            new Locale("en-US"), new RandomService());

    private GenerateData()
    {
    }

    public static List<Product> products(final int size)
    {
        final List<Product> result = new ArrayList<>();
        for (int idx = 0; idx < size; idx++)
        {
            result.add(newProduct(idx));
        }

        return result;
    }

    private static Product newProduct(final Integer idx)
    {
        return new Product(idx, faker.regexify("[a-z1-9]{10}"), faker.regexify("[a-z1-9]{100}"), Integer.parseInt(faker.regexify("[0-9]{1}")));
    }

    public static List<SomeData> testData(final int size)
    {
        final List<SomeData> testData = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
        {
            testData.add(i, new SomeData(i));
        }
        return testData;

    }
}
