package com.armchairfun.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.armchairfun.common.ErrorIds;
import com.armchairfun.common.User;
import com.armchairfun.persistence.SessionFactoryUtil;

public class UserDao {

	public User getUser(String username, String password) throws UserNotFoundException {
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try {
			tx = session.beginTransaction();
			List<User> users = session.createCriteria(User.class)
			.add( Restrictions.eq("username", username))
			.add( Restrictions.eq("password", password))
					.list();
			tx.commit();
			if (users.size() > 0) {
				return users.get(0);
			}
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
					System.out.println("Error rolling back transaction");
				}
				throw new UserNotFoundException(ErrorIds.NO_USER_FOUND, "User not found");
			}

		}
		throw new UserNotFoundException(ErrorIds.NO_USER_FOUND, "User not found");
	}
}
