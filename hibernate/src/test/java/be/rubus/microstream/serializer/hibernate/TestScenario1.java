package be.rubus.microstream.serializer.hibernate;

import be.rubus.microstream.serializer.hibernate.model.ProductEntity;
import be.rubus.microstream.serializer.hibernate.util.HibernateUtil;
import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestScenario1
{

    @Test
    void testScenario1()
    {
        try (Session session = HibernateUtil.getSessionFactory()
                .openSession())
        {
            Query<ProductEntity> query = session.createQuery("FROM ProductEntity ", ProductEntity.class);
            List<ProductEntity> products = query.getResultList();
            Assertions.assertThat(products)
                    .hasSize(10_000);
        }
    }

}
