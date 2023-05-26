package be.rubus.microstream.serializer.hibernate;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.hibernate.model.ProductEntity;
import be.rubus.microstream.serializer.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class LoadData
{

    public static void main(String[] args)
    {
        List<Product> products = GenerateData.products(10_000);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            for (Product product : products)
            {
                session.persist(new ProductEntity(product));
            }

            transaction.commit();

        }
        HibernateUtil.shutdown();  // Closes the Sessionfactory

    }
}
