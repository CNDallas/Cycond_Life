package edu.se309.app.backend.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.se309.app.backend.entity.Account;

@Repository
public class AccountDAOImplementation implements AccountDAO {

	private EntityManager entityManager;
	
	@Autowired
	public AccountDAOImplementation(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Override
	public void deleteById(int accountId) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query query = 
				currentSession.createQuery("DELETE FROM Account WHERE id=:accountId")
				.setParameter("accountId", accountId);
		query.executeUpdate();
		
	}

	@Override	
	public List<Account> findAll() {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<Account> query = currentSession.createQuery("FROM Account", Account.class);
		
		List<Account> accounts = query.getResultList();
		
		return accounts;
	}

	@Override
	public Account findByEmail(String email) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<Account> query = 
				currentSession.createQuery("FROM Account a WHERE a.email = :email", Account.class)
				.setParameter("email", email);
		
		Account accountOutput = query.getSingleResult();
		return accountOutput;
	}

	@Override
	public Account findById(int accountId) {
		
		Session currentSession = entityManager.unwrap(Session.class);
		
		Account account = currentSession.get(Account.class, accountId);
		
		return account;
	}

	@Override
	public Account findByUsername(String username) {
		Session currentSession = entityManager.unwrap(Session.class);
		
		Query<Account> query = 
				currentSession.createQuery("FROM Account a WHERE a.username = :username", Account.class)
				.setParameter("username", username);
		
		Account accountOutput = query.getSingleResult();
		return accountOutput;
	}

	@Override
	public void save(Account newAccount) {
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.saveOrUpdate(newAccount);
		
	}

}