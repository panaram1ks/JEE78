package com.library.app.category.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.library.app.category.model.Category;

public class CategoryRepository {

	EntityManager em;

	public Category add(Category category) {
		em.persist(category);
		return category;
	}

	public Category findById(final Long id) {
		if (id == null) {
			return null;
		}
		return em.find(Category.class, id);
	}

	public void update(Category category) {
		em.merge(category);

	}

	@SuppressWarnings("unchecked")
	public List<Category> findAll(String ordereField) {
		return em.createQuery("Select e From Category e Order by e." + ordereField).getResultList();
	}

	public boolean alreadyExists(Category category) {
		final StringBuilder jpql = new StringBuilder();
		jpql.append("Select 1 From Category e where e.name = :name");
		if (category.getId() != null) {
			jpql.append(" And e.id != :id");
		}

		final Query query = em.createQuery(jpql.toString());
		query.setParameter("name", category.getName());
		if (category.getId() != null) {
			query.setParameter("id", category.getId());
		}

		return query.setMaxResults(1).getResultList().size() > 0;
	}

	public Object existingById(Long id) {
		return em.createQuery("Select 1 From Category e where e.id = :id").setParameter("id", id).setMaxResults(1)
				.getResultList().size() > 0;
	}

}
