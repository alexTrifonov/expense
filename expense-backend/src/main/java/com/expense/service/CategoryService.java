package com.expense.service;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expense.entities.Category;
import com.expense.repos.CategoryRepository;

/**
 * Класс из уровня обслуживания для доступа к объектам Категория.
 * @author Alexandr Trifonov
 *
 */
@Transactional
@Service
public class CategoryService {
	/**
	 * Репозиторий Категорий.
	 */
	private CategoryRepository categoryRepo;
	/**
	 * Объект JdbcTemplate для запросов к базе данных вне JPA.
	 */
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setCategoryRepo(CategoryRepository categoryRepo) {
		this.categoryRepo = categoryRepo;
	}
	
	@Autowired
	public void setJdbcTemplate(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
	}
	
	/**
	 * Получение родительских категорий.
	 * @return Список родительских категорий.
	 */
	@Transactional(readOnly = true)
	public List<Category> getParentCategories() {
		return this.categoryRepo.findByParentNull();
	}
	
	/**
	 * Сохранение категории.
	 * @param category Категория
	 * @return Сохраненная категория.
	 */
	public Category save(Category category) {
		return this.categoryRepo.save(category);
	}
	
	/**
	 * Получение категории по идентификатору.
	 * @param id Идентификатор категории
	 * @return Найденная по идентификатору категория.
	 */
	@Transactional(readOnly = true)
	public Category findCategory(int id) {
		return this.categoryRepo.findById(id).get();
	}
	
	/**
	 * Удаление категории по идентификатору.
	 * @param id Идентификатор категории
	 */
	public void deleteCategory(int id) {
		this.categoryRepo.deleteById(id);
	}
	
	/**
	 * Удаление списка указанных категорий
	 * @param categories Список категорий для удаления
	 */
	public void deleteCategoryList(List<Category> categories) {
		this.categoryRepo.deleteAll(categories);
	}
	
	/**
	 * Получение списка всех подкатегорий для указанной категории
	 * @param parent Родительская категория.
	 * @return Список подкатегорий для указанной категории.
	 */
	public List<Category> getChilds(Category parent) {		
		return categoryRepo.findByParentOrderByNameAsc(parent);
	}
	
	/**
	 * Получение списка всех родительских категорий, по которым нет расходов ни по самим категориям ни по подкатегориям (при наличии подкатегорий).
	 * @return Список родительских категорий без расходов.
	 */
	@Transactional(readOnly = true)
	public List<Category> getParentCategoriesNoExpense() {
		List<Category> categoryList = new ArrayList<>();
		List<Integer> idList = jdbcTemplate.queryForList("select category.id from category where category.parent_id is null "
				+ "and category.id not in (with recursive included_category(id, parent_id)"
				+ " as (select id, parent_id from category where category.id in (select category.id from category right outer join expense on expense.category_id = category.id)"
				+ " union all select c.id, c.parent_id from included_category ct, category c where c.id = ct.parent_id) select id from included_category where parent_id is null)", Integer.class);
		Iterable<Category> categoryIterable = categoryRepo.findByIdInOrderByNameAsc(idList);
		categoryIterable.forEach(categoryList::add);
		return categoryList;
	}
	
	/**
	 * Получение списка подкатегорий для указанной категории, по которым нет расходов.
	 * @param parentId Идентификаторо родительско категории.
	 * @return Список подкатегорий без расходов.
	 */
	@Transactional(readOnly = true)
	public List<Category> getChildCategoriesNoExpense(int parentId) {
		return categoryRepo.findByParentAndWithoutExpense(parentId);
	}
}
