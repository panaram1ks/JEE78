package com.library.app.category.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.category.model.Category;
import com.library.app.commontests.utils.DBCommand;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.junit.Assert.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

public class CategoryRepositoryUTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private CategoryRepository categoryRepository;
	private DBCommandTransactionalExecutor dBCommandTransactionalExecutor;

	@Before
	public void initTestCase() {
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();

		categoryRepository = new CategoryRepository();
		categoryRepository.em = em;

		dBCommandTransactionalExecutor = new DBCommandTransactionalExecutor(em);
	}

	@After
	public void closeEntityManager() {
		em.close();
		emf.close();
	}

	@Test
	public void addCategoryAndFindIt() {
		Long categoryAddedId = dBCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});
		assertThat(categoryAddedId, is(notNullValue()));

		Category category = categoryRepository.findById(categoryAddedId);
		assertThat(category, is(notNullValue()));
		assertThat(category.getName(), is(equalTo(java().getName())));
	}

	@Test
	public void findCategoryByIdNotFound() {
		final Category category = categoryRepository.findById(999L);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void findCategoryByIdWithnullId() {
		final Category category = categoryRepository.findById(null);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void updateCategory() {
		final Long categoryAddedId = dBCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});
		Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));

		categoryAfterAdd.setName(cleanCode().getName());
		dBCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.update(categoryAfterAdd);
			return null;
		});

		Category categoryAfterUpdate = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));
	}

	@Test
	public void findAllCategories() {
		dBCommandTransactionalExecutor.executeCommand(() -> {
			allCategories().forEach(categoryRepository::add);
			return null;
		});

		final List<Category> categories = categoryRepository.findAll("name");
		assertThat(categories.size(), is(equalTo(4)));
		// assertThat(categories.get(0), is(equalTo(java().getName())));
		// assertThat(categories.get(1), is(equalTo(cleanCode().getName())));
		// assertThat(categories.get(2), is(equalTo(architecture().getName())));
		// assertThat(categories.get(3), is(equalTo(networks().getName())));
	}

	@Test
	public void alreadyExistsForAdd() {
		dBCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(java()).getId();
			return null;
		});

		assertThat(categoryRepository.alreadyExists(java()), is(equalTo(true)));
		assertThat(categoryRepository.alreadyExists(cleanCode()), is(equalTo(false)));
	}

	@Test
	public void alreadyExistsCategoryWithId() {
		Category java = dBCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(cleanCode());
			return categoryRepository.add(java());
		});

		assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));
		java.setName(cleanCode().getName());
		assertThat(categoryRepository.alreadyExists(java), is(equalTo(true)));
		java.setName(networks().getName());
		assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));
	}

	@Test
	public void existsById() {
		Long categoryAddedId = dBCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});

		assertThat(categoryRepository.existingById(categoryAddedId), is(equalTo(true)));
		assertThat(categoryRepository.existingById(999l), is(equalTo(false)));
	}

}
