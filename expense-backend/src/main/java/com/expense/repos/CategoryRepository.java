package com.expense.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expense.entities.Category;
/**
 * Класс репозиторий объектов Категория.
 * @author Alexandr Trifonov
 *
 */
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	/**
	 * Получить все дочерние категории для данной родительской категории.
	 * @param parent Родительская категория
	 * @return Список дочерних категорий (подкатегорий)
	 */
	List<Category> findByParent(Category parent);
	
	/**
	 * Получить все дочерние категории для данной родительской категории.
	 * @param parent Родительская категория
	 * @return Список дочерних категорий, отсортированных по имени.
	 */
	List<Category> findByParentOrderByNameAsc(Category parent);
	
	/**
	 * Получить все родительские категории.
	 * @return Список родительских категорий
	 */
	@Query("select c from Category c where c.parent is null order by c.name")
	List<Category> findByParentNull();
	
	/**
	 * Получить все категории из указанного списка с идентификаторами.
	 * @param list Список идентификаторов.
	 * @return Список категорий, отсортированных по имени.
	 */
	List<Category> findByIdInOrderByNameAsc(List<Integer> list);
	
	
	
	/**
	 * Получить все подкатегории одной определенной категории, по которым нет расхода
	 * @param id Идентификатор родительской категории
	 * @return Список подкатегорий, по которым нет расходов.
	 */
	@Query("select c from Category c LEFT JOIN Expense e on c = e.category where e.category is null and c.parent.id = :id order by c.name")
	List<Category> findByParentAndWithoutExpense(@Param("id") Integer id);
	
	
	
	
	
}
