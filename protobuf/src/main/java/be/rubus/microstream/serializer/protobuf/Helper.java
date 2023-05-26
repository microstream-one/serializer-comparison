package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.SomeData;
import be.rubus.microstream.serializer.data.shop.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import one.microstream.compare.serializer.proto.model.ProductOuterClass;
import one.microstream.compare.serializer.proto.model.ShopOuterClass;
import one.microstream.compare.serializer.proto.model.SomeDataOuterClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        final SomeDataOuterClass.SomeData.Builder builder = SomeDataOuterClass.SomeData.newBuilder()
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
        final BigInteger unscaled = new BigInteger(big.getValue()
                                                     .toByteArray());
        return new BigDecimal(unscaled, big.getScale());

    }

    private static Timestamp toGoogleTimestamp(final LocalDateTime localDateTime)
    {
        final Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private static Timestamp toGoogleTimestamp(final LocalDate localDate)
    {
        final Instant instant = localDate.atTime(0,0).toInstant(ZoneOffset.UTC);

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private static LocalDateTime toLocalDateTime(final Timestamp timestamp)
    {
        final Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());

        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static LocalDate toLocalDate(final Timestamp timestamp)
    {
        final Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());

        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate();
    }

    // Scenario 3
    public static ShopOuterClass.Shops createProtoVariant(final List<Shop> shops)
    {
        final ShopOuterClass.Shops.Builder builder = ShopOuterClass.Shops.newBuilder()
                .addAllShops(shops.stream().map(Helper::createProtoVariant).collect(Collectors.toList()));
        return builder.build();
    }


    public static ShopOuterClass.Shop createProtoVariant(final Shop shop)
    {
        final ShopOuterClass.Shop.Builder builder = ShopOuterClass.Shop.newBuilder()
                .setName(shop.getName())
                .setStore(createProtoVariant(shop.getStore()))
                .addAllOrders(shop.getOrders().stream().map(Helper::createProtoVariant).collect(Collectors.toList()));
        return builder.build();
    }

    private static ShopOuterClass.Order createProtoVariant(final Order order)
    {
        final ShopOuterClass.Order.Builder builder = ShopOuterClass.Order.newBuilder()
                .setCustomerName(order.getCustomerName())
                .setOrderDate(toGoogleTimestamp(order.getOrderDate()))
                .addAllOrderLines(order.getOrderLines().stream().map(Helper::createProtoVariant).collect(Collectors.toList()));
        return builder.build();
    }

    private static ShopOuterClass.OrderLine createProtoVariant(final OrderLine orderLine)
    {
        final ShopOuterClass.OrderLine.Builder builder = ShopOuterClass.OrderLine.newBuilder()
                .setShopProduct(createProtoVariant(orderLine.getProduct()))
                .setAmount(orderLine.getAmount());
        return builder.build();
    }

    private static ShopOuterClass.Warehouse createProtoVariant(final Warehouse warehouse)
    {
        final ShopOuterClass.Warehouse.Builder builder = ShopOuterClass.Warehouse.newBuilder()
                .addAllStockItems(warehouse.getStockItems().stream().map(Helper::createProtoVariant).collect(Collectors.toList()));
        return builder.build();
    }

    private static ShopOuterClass.StockItem createProtoVariant(final StockItem stockItem)
    {

        final ShopOuterClass.StockItem.Builder builder = ShopOuterClass.StockItem.newBuilder()
                .setShopProduct(createProtoVariant(stockItem.getShopProduct()))
                .setCount(stockItem.getCount());
        return builder.build();
    }

    private static ShopOuterClass.ShopProduct createProtoVariant(final ShopProduct shopProduct)
    {
        final ShopOuterClass.ShopProduct.Builder builder = ShopOuterClass.ShopProduct.newBuilder()
                .setName(shopProduct.getName())
                .setPrice(shopProduct.getPrice())
                .setSellingPrice(shopProduct.getSellingPrice())
                //Only name to break circular dependency
                .addAllShopNames(shopProduct.getShops().stream().map(Shop::getName).collect(Collectors.toList()));
        return builder.build();
    }

    public static List<Shop> createPojoVariant(final ShopOuterClass.Shops dataProto)
    {
        final List<Shop> result = new ArrayList<>();
        dataProto.getShopsList().stream().map(Helper::createPojoVariant).forEach(result::add);
        return result;
    }

    public static Shop createPojoVariant(final ShopOuterClass.Shop dataProto)
    {
        final Shop result = new Shop();
        result.setName(dataProto.getName());
        result.setStore(createPojoVariant(dataProto.getStore()));
        result.setOrders(dataProto.getOrdersList().stream().map(Helper::createPojoVariant).collect(Collectors.toList()));
        return result;
    }

    private static Order createPojoVariant(final ShopOuterClass.Order order)
    {
        final Order result = new Order();
        result.setCustomerName(order.getCustomerName());
        result.setOrderDate(toLocalDate(order.getOrderDate()));
        result.setOrderLines(order.getOrderLinesList().stream().map(Helper::createPojoVariant).collect(Collectors.toList()));
        return result;
    }

    private static OrderLine createPojoVariant(final ShopOuterClass.OrderLine orderLine)
    {
        final OrderLine result = new OrderLine();
        result.setShopProduct(createPojoVariant(orderLine.getShopProduct()));
        result.setAmount(orderLine.getAmount());
        return result;
    }

    private static ShopProduct createPojoVariant(final ShopOuterClass.ShopProduct shopProduct)
    {
        final ShopProduct result = new ShopProduct();
        result.setName(shopProduct.getName());
        result.setPrice(shopProduct.getPrice());
        result.setSellingPrice(shopProduct.getSellingPrice());
        result.setShops(shopProduct.getShopNamesList().stream().map(Helper::newShop).collect(Collectors.toList()));
        return result;
    }

    private static Shop newShop(final String name)
    {
        final Shop result = new Shop();
        result.setName(name);
        return result;
    }

    private static Warehouse createPojoVariant(final ShopOuterClass.Warehouse warehouse)
    {
        final Warehouse result = new Warehouse();
        result.setStockItems(warehouse.getStockItemsList().stream().map(Helper::createPojoVariant).collect(Collectors.toList()));
        return result;
    }

    private static StockItem createPojoVariant(final ShopOuterClass.StockItem stockItem)
    {
        final StockItem result = new StockItem();
        result.setShopProduct(createPojoVariant(stockItem.getShopProduct()));
        result.setCount(stockItem.getCount());
        return result;
    }

    public static void useShopReference(final List<Shop> allShops)
    {
        allShops.forEach(shop ->
                         {
                             shop.getOrders()
                                     .forEach(order ->
                                              {
                                                  order.getOrderLines()
                                                          .forEach(orderLine ->
                                                                   {
                                                                       List<Shop> actualShops = orderLine.getProduct()
                                                                               .getShops()
                                                                               .stream()
                                                                               .map(shopProduct -> findShop(allShops, shopProduct.getName()))
                                                                               .collect(Collectors.toList());
                                                                       orderLine.getProduct()
                                                                               .setShops(actualShops);
                                                                   });
                                              });

                             shop.getStore()
                                     .getStockItems()
                                     .forEach(stockItem ->
                                              {
                                                  List<Shop> actualShops = stockItem.getShopProduct()
                                                          .getShops()
                                                          .stream()
                                                          .map(shopProduct -> findShop(allShops, shopProduct.getName()))
                                                          .collect(Collectors.toList());
                                                  stockItem.getShopProduct()
                                                          .setShops(actualShops);
                                              });
                         });
    }

    private static Shop findShop(final List<Shop> allShops, final String name)
    {
        final Optional<Shop> shopResult = allShops.stream()
                .filter(shop -> shop.getName()
                        .equals(name))
                .findAny();
        return shopResult.orElse(null);

    }
}
