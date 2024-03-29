package be.rubus.microstream.serializer.data;

import be.rubus.microstream.serializer.data.shop.*;
import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class GenerateData
{

    private static final Faker faker = new Faker(
            new Locale("en-US"), new RandomService());

    // A fixed seed so that generated data are almost the same everytime.
    private static final Random rnd = new Random(123456789L);

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

    public static List<Shop> testShopData(final boolean largeSet)
    {
        final List<ShopProduct> products = generateProducts(largeSet);
        final List<Shop> shops = generateShops(largeSet);
        final Map<Shop, List<ShopProduct>> productsByShop = assignProductsToShops(shops, products, largeSet);
        createOrders(productsByShop, largeSet);
        createStock(productsByShop);

        return shops;
    }

    private static void createStock(final Map<Shop, List<ShopProduct>> productsByShop)
    {
        for (final Map.Entry<Shop, List<ShopProduct>> entry : productsByShop.entrySet())
        {
            final Warehouse store = entry.getKey()
                    .getStore();
            for (final ShopProduct shopProduct : entry.getValue())
            {
                final int stockAmount = rnd.nextInt(30);
                store.addStockItem(new StockItem(shopProduct, stockAmount));
            }
        }
    }

    private static void createOrders(final Map<Shop, List<ShopProduct>> productsByShop, final boolean largeSet)
    {
        final int size = largeSet ? 5000 : 250;
        for (final Map.Entry<Shop, List<ShopProduct>> entry : productsByShop.entrySet())
        {
            for (int i = 0; i < size; i++)
            {
                final List<ShopProduct> productList = entry.getValue();
                final List<OrderLine> orderLines = new ArrayList<>();
                final int orderItems = rnd.nextInt(3) + 1; // minimum 1.
                for (int j = 0; j < orderItems; j++)
                {

                    final int productIdx = rnd.nextInt(productList.size());
                    final int amount = rnd.nextInt(3) + 1;
                    orderLines.add(new OrderLine(productList.get(productIdx), amount));
                }

                final Order order = new Order(faker.name()
                                                      .name()
                        , faker.date()
                                                      .past(600, TimeUnit.DAYS)
                                                      .toInstant()
                                                      .atZone(ZoneId.systemDefault())
                                                      .toLocalDate()
                        , orderLines);
                entry.getKey()
                        .addOrder(order);
            }
        }
    }

    private static Map<Shop, List<ShopProduct>> assignProductsToShops(final List<Shop> shops
            , final List<ShopProduct> products
            , final boolean largeSet)
    {
        final Map<Shop, List<ShopProduct>> result = new HashMap<>();
        final int size = largeSet ? 15 : 1;
        for (final ShopProduct product : products)
        {
            final int nbrOfShops = rnd.nextInt(size) + 1;
            while (product.getShops()
                    .size() < nbrOfShops)
            {
                final int idx = rnd.nextInt(shops.size());
                final Shop shop = shops.get(idx);
                if (!product.getShops()
                        .contains(shop))
                {
                    product.addToShop(shop);

                    final List<ShopProduct> productsOfShop = result.computeIfAbsent(shop, s -> new ArrayList<>());
                    productsOfShop.add(product);
                }
            }
        }
        return result;
    }

    private static List<Shop> generateShops(final boolean largeSet)
    {
        final int size = largeSet ? 35 : 2;
        final List<Shop> result = new ArrayList<>();
        for (int idx = 1; idx <= size; idx++)
        {
            result.add(new Shop("Shop " + idx, new Warehouse()));
        }
        return result;
    }

    private static List<ShopProduct> generateProducts(final boolean largeSet)
    {
        final int size = largeSet ? 10_000 : 500;
        final List<ShopProduct> result = new ArrayList<>();
        for (int i = 1; i <= size; i++)
        {
            result.add(newShopProduct(i));
        }
        return result;
    }

    private static ShopProduct newShopProduct(final int idx)
    {
        final double price = rnd.nextDouble() * 150.0d;
        final double profit = rnd.nextDouble() * 10.0d;

        return new ShopProduct("product " + idx, price, price * (1 + profit / 100.0));
    }
}
