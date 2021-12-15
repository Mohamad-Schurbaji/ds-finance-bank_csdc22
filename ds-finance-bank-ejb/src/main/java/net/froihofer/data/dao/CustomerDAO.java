package net.froihofer.data.dao;

import net.froihofer.common.exception.BankException;
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

    public Customer addCustomer(String firstName, String lastName, String address) throws BankPersistenceException {
        if (firstName == null || lastName == null || address == null)
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_INSERT, "Customer's info can not be null");
        if (firstName.isBlank() || lastName.isBlank() || address.isBlank())
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_INSERT, "Customer's info can not be empty");
        return addCustomer(new Customer(firstName, lastName, address));
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
        String query = "select c FROM Customer as c where LOWER(c.lastName) like :lastName";
        TypedQuery<Customer> typedQuery = entityManager.createQuery(query, Customer.class);
        typedQuery.setParameter("lastName", "%" + lastName + "%");
        try {
            return typedQuery.getResultList();
        } catch (NoResultException noResultException) {
            throw new BankPersistenceException(PersistenceFaultCode.FAILED_TO_FIND,
                    String.format("No entries were found for %s", lastName), noResultException);
        }
    }
}
