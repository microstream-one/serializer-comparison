package be.rubus.microstream.serializer.data;

import be.rubus.microstream.serializer.data.shop.Order;
import be.rubus.microstream.serializer.data.shop.OrderLine;
import be.rubus.microstream.serializer.data.shop.Shop;
import be.rubus.microstream.serializer.data.shop.ShopProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestCircular
{

    @Test
    void testCircular()
    {
        final List<Shop> allShops = GenerateData.testShopData(true);

        // Take the first shop, but actually this is true for each shop (by the way how the Obj
        final Shop shop = allShops.get(0);
        // All orders for the shop
        final List<Order> orders = shop.getOrders();
        // All order lines for the shop
        final List<OrderLine> orderLines = orders.stream()
                .map(Order::getOrderLines)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // All shops where a product sold from this shop is available.
        final Set<Shop> shops = orderLines.stream()
                .map(OrderLine::getProduct)
                .map(ShopProduct::getShops)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());


        Assertions.assertThat(shops.contains(shop))
                .isTrue();

    }

}
