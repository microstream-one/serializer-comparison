package be.rubus.microstream.serializer.hibernate;

import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.hibernate.model.ProductEntity;
import be.rubus.microstream.serializer.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class HibernatePerformanceRun1
{
    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(HibernatePerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> allProducts;
    private Session reusedSession;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        reusedSession = HibernateUtil.getSessionFactory()
                .openSession();
        Logger.getLogger("").setLevel(Level.WARNING);  // But doesn't work. Can't set the log options within the JMH executions.
    }

    @TearDown
    public void shutdown() throws Exception
    {
        if (reusedSession != null)
        {
            reusedSession.close();
            HibernateUtil.shutdown();
        }
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<ProductEntity> deserializeWithInitialization()
    {

        final List<ProductEntity> products;
        // This is not the entire initialization because Hibernate is already initialised through init.
        // Only factory initialisation is counted here.
        try (SessionFactory sessionFactory = HibernateUtil.getNewSessionFactory())
        {
            try (Session session = sessionFactory
                    .openSession())
            {
                Query<ProductEntity> query = session.createQuery("FROM ProductEntity ", ProductEntity.class);
                products = query.getResultList();
            }  // Session close
        } // SessionFactory close

        return products;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<ProductEntity> deserializeReuseSession()
    {

        final List<ProductEntity> products;
        Query<ProductEntity> query = reusedSession.createQuery("FROM ProductEntity ", ProductEntity.class);
        products = query.getResultList();

        return products;
    }
}
