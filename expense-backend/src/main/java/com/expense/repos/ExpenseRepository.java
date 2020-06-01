package com.expense.repos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.expense.entities.Category;
import com.expense.entities.Expense;

/**
 * Класс репозиторий объектов Расход.
 * @author Alexandr Trifonov
 *
 */
public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Long> {
	/**
	 * Получить все расходы в указанном периоде дат.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @return Список расходов в указанном периоде дат.
	 */
	List<Expense> findAllByLocalDateBetween(LocalDate startDate, LocalDate finishDate);
	
	/**
	 * Получить все расходы в указанном периоде дат, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов и в указанном периоде дат.
	 */
	List<Expense> findAllByLocalDateBetween(LocalDate startDate, LocalDate finishDate, Sort sort);
	
	/**
	 * Получить все расходы в указанном периоде дат по определенной категории, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param category Категория расхода.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, по определенной категории, в указанном периоде дат.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategory(LocalDate startDate, LocalDate finishDate, Category category, Sort sort);
	
	/**
	 * Получить все расходы: в указанном периоде дат, по определенной категории, в определенном диапазоне цен, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param category Категория расхода.
	 * @param totalPriceFrom Начальная цена диапазона.
	 * @param totalPriceTo Конечная цена диапазона.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, по определенной категории в указанном периоде дат и в указанном диапазоне цен.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryAndTotalPriceBetween(LocalDate startDate, LocalDate finishDate, Category category, double totalPriceFrom, double totalPriceTo, Sort sort);
	
	/**
	 * Получить все расходы: в указанном периоде дат, в определенном диапазоне цен, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param totalPriceFrom Начальная цена диапазона.
	 * @param totalPriceTo Конечная цена диапазона.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, в указанном периоде дат и в указанном диапазоне цен.
	 */
	List<Expense> findAllByLocalDateBetweenAndTotalPriceBetween(LocalDate startDate, LocalDate finishDate, double totalPriceFrom, double totalPriceTo, Sort sort);
	
	/**
	 * Получить все расходы: в указанном периоде дат, примечание содержит указанную символьную последовательность, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param note Примечание.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, в указанном периоде дат с примечанием, содержащим указанную символьную последовательность.
	 */
	List<Expense> findAllByLocalDateBetweenAndNoteIgnoreCaseContaining(LocalDate startDate, LocalDate finishDate, String note, Sort sort);
	
	/**
	 * Получить все расходы: в указанном периоде дат, по определенной категории, примечание содержит указанную символьную последовательность, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param category Категория расхода.
	 * @param note Примечание.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, по указанной категории, в указанном периоде дат, с примечанием, содержащим указанную символьную последовательность.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryAndNoteIgnoreCaseContaining(LocalDate startDate, LocalDate finishDate, Category category, String note, Sort sort);
	
	/**
	 * Получить все расходы: в указанном периоде дат, примечание содержит указанную символьную последовательность, в определенном диапазоне цен, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param note Примечание.
	 * @param totalPriceFrom Начальная цена диапазона.
	 * @param totalPriceTo Конечная цена диапазона.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, в указанном периоде дат и в указанном диапазоне цен, с примечанием, содержащим указанную символьную последовательность.
	 */
	List<Expense> findAllByLocalDateBetweenAndNoteIgnoreCaseContainingAndTotalPriceBetween(LocalDate startDate, LocalDate finishDate, String note, double totalPriceFrom, double totalPriceTo, Sort sort);
	
	/**
	 * Получить все расходы: в указанном периоде дат, по определенной категории, примечание содержит указанную символьную последовательность, в определенном диапазоне цен, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param category Категория расхода.
	 * @param note Примечание.
	 * @param totalPriceFrom Начальная цена диапазона.
	 * @param totalPriceTo Конечная цена диапазона.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, в указанном периоде дат и в указанном диапазоне цен, по определенной категории, с примечанием, содержащим указанную символьную последовательность.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryAndNoteIgnoreCaseContainingAndTotalPriceBetween(LocalDate startDate, LocalDate finishDate, Category category, String note, double totalPriceFrom, double totalPriceTo, Sort sort);
	
	
	
	/**
	 * Получить все расходы в указанном периоде дат по определенным подкатегориям.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param subcategories Список подкатегорий.
	 * @return Список расходов по определенным подкатегориям, в указанном периоде дат.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryIn(LocalDate startDate, LocalDate finishDate, List<Category> subcategories);
	
	/**
	 * Получить все расходы в указанном периоде дат по определенным подкатегориям, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param subcategories Список подкатегорий.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, по определенным подкатегориям, в указанном периоде дат.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryIn(LocalDate startDate, LocalDate finishDate, List<Category> subcategories, Sort sort);
	
	/**
	 * Получить все расходы в указанном периоде дат, по определенным подкатегориям, в указанном диапазоне цен, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param subcategories Список подкатегорий.
	 * @param totalPriceFrom Начальная цена диапазона.
	 * @param totalPriceTo Конечная цена диапазона.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, по определенным подкатегориям, в указанном периоде дат, в указанном диапазоне цен.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryInAndTotalPriceBetween(LocalDate startDate, LocalDate finishDate, List<Category> subcategories, double totalPriceFrom, double totalPriceTo, Sort sort);
	
	/**
	 * Получить все расходы в указанном периоде дат, по определенным подкатегориям, с примечанием, содержащим указанную символьную последовательность, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param subcategories Список подкатегорий.
	 * @param note Примечание.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, по определенным подкатегориям, с примечанием, содержащим указанную символьную последовательность, в указанном диапазоне цен.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryInAndNoteIgnoreCaseContaining(LocalDate startDate, LocalDate finishDate, List<Category> subcategories, String note, Sort sort);
	
	/**
	 * Получить все расходы в указанном периоде дат, по определенным подкатегориям, с примечанием, содержащим указанную символьную последовательность, в указанном периоде дат, отсортированные по указанному параметру.
	 * @param startDate Начальная дата периода.
	 * @param finishDate Конечная дата периода.
	 * @param subcategories Список подкатегорий.
	 * @param note Примечание.
	 * @param totalPriceFrom Начальная цена диапазона.
	 * @param totalPriceTo Конечная цена диапазона.
	 * @param sort Объект сортировки.
	 * @return Список отсортированных расходов, по определенным подкатегориям, в указанном периоде дат, с примечанием, содержащим указанную символьную последовательность, в указанном диапазоне цен.
	 */
	List<Expense> findAllByLocalDateBetweenAndCategoryInAndNoteIgnoreCaseContainingAndTotalPriceBetween(LocalDate startDate, LocalDate finishDate, List<Category> subcategories, String note, double totalPriceFrom, double totalPriceTo, Sort sort);
	
	
}
