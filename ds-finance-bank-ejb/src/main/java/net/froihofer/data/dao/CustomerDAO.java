package net.froihofer.data.dao;

import net.froihofer.data.BankPersistenceException;
import net.froihofer.data.PersistenceFaultCode;
import net.froihofer.data.entity.Customer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CustomerDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public Customer addCustomer(Customer customer) throws BankPersistenceException {
        if (customer == null)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_INSERT, "Customer can not be null");
        try {
            entityManager.persist(customer);
            entityManager.flush();
            return customer;
        } catch (Exception e) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_INSERT, "Customer already exist!", e);
        }
    }

    public Customer getCustomerById(Long customerId) {
        return entityManager.find(Customer.class, customerId);
    }

    public List<Customer> getCustomerByLastName(String lastName) throws BankPersistenceException {
        if (lastName == null)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_FIND, "Name can not be null");
        if (lastName.isBlank())
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_FIND, "Name can not be empty");
        lastName = lastName.toLowerCase();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = criteriaBuilder.createQuery(Customer.class);
        Root<Customer> from = criteria.from(Customer.class);
        criteria.select(from).where(criteriaBuilder.like(from.get("last_name"), "%" + lastName + "%"));
        TypedQuery<Customer> typedQuery = entityManager.createQuery(criteria);
        try {
            return typedQuery.getResultList();
        } catch (NoResultException noResultException) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_FIND,
                    String.format("No entries were found for %s", lastName), noResultException);
        }
    }
}
