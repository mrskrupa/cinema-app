package cinema.dao.impl;

import cinema.dao.OrderDao;
import cinema.exception.DataProcessingException;
import cinema.lib.Dao;
import cinema.model.Order;
import cinema.model.User;
import cinema.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class OrderDaoImpl implements OrderDao {
    @Override
    public Order add(Order order) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();
            return order;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add new order " + order, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Order> getByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> query = session.createQuery("select distinct o "
                    + "from Order o "
                    + "join fetch o.tickets t "
                    + "join fetch t.movieSession m "
                    + "join fetch m.movie "
                    + "join fetch m.cinemaHall "
                    + "where o.user = :user");
            query.setParameter("user", user);
            List<Order> resultList = query.getResultList();
            return resultList;
        } catch (Exception e) {
            throw new DataProcessingException("Can't get order by user " + user, e);
        }
    }
}