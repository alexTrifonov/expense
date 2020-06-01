package com.expense.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expense.entities.Category;
import com.expense.entities.Expense;
import com.expense.repos.ExpenseRepository;

/**
 * Класс из уровня обслуживания для доступа к объектам Расход.
 * @author Alexandr Trifonov
 *
 */
@Transactional
@Service
public class ExpenseService {
	/**
	 * Репозиторий Расходов.
	 */
	private ExpenseRepository expenseRepo;
	
	/**
	 * Объект уровня обслуживания для доступа к объектам Категория.
	 */
	private CategoryService categoryService;
	
	/**
	 * Константа для получения параметра запроса с периодом дат.
	 */
	private final static String DATES = "dates";
	/**
	 * Константа для получения параметра запроса с начальной датой.
	 */
	private final static String DATE_FROM = "dateFrom";
	/**
	 * Константа для получения параметра запроса с конечной датой.
	 */
	private final static String DATE_TO = "dateTo";
	/**
	 * Константа для получения параметра запроса с диапазоном цен.
	 */
	private final static String PRICES = "prices";
	/**
	 * Константа для получения параметра запроса с символьной последовательностью для поиска примечаний к расходам.
	 */
	private final static String NOTE = "note";
	/**
	 * Константа для получения параметра запроса с идентификатором категории.
	 */
	private final static String CATEGORY_ID = "categoryId";
	/**
	 * Константа для получения параметра запроса для формирования объекта сортировки.
	 */
	private final static String ORDER_BY = "orderBy";
	
	@Autowired
	public void setExpenseRepo(ExpenseRepository expenseRepo) {
		this.expenseRepo = expenseRepo;
	}
	
	
	@Autowired
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	/**
	 * Получение расхода по идентификатору.
	 * @param id Идентификатор расхода
	 * @return Расход с указанным идентификатором.
	 */
	@Transactional(readOnly = true)
	public Expense getExpense(long id) {
		return expenseRepo.findById(id).get();
	}
	
	/**
	 * Добавление расхода.
	 * @param expense Расход
	 * @return Добавленный расход.
	 */
	public Expense addExpense(Expense expense) {
		return expenseRepo.save(expense);
	}
	
	/**
	 * Обновление расхода.
	 * @param id Идентификатор расхода
	 * @param patchExpense Расход с новыми значениями полей
	 * @return Обновленный расход.
	 */
	public Expense updateExpense(long id, Expense patchExpense) {		
		Expense expense = getExpense(id);
		if (patchExpense.getCategory() != null) {
			expense.setCategory(patchExpense.getCategory());
		}
		if (patchExpense.getLocalDate() != null) {
			expense.setLocalDate(patchExpense.getLocalDate());
		} 		
		if (patchExpense.getUnitPrice().compareTo(BigDecimal.valueOf(0)) >= 0 && patchExpense.getCount() != 0) {
			expense.setCount(patchExpense.getCount());
			expense.setUnitPrice(patchExpense.getUnitPrice());
			expense.setTotalPrice();
		}
		if (patchExpense.getNote() != null) {
			expense.setNote(patchExpense.getNote());
		}	
		return expenseRepo.save(expense);
	}
	
	/**
	 * Удаление расхода по идентификатору.
	 * @param id Идентификатор расхода
	 */
	public void deleteExpense(long id) {
		expenseRepo.deleteById(id);
	}
	
	/**
	 * Получение списка расходов, удовлетворяющих указанным параметрам.
	 * @param params Map с названиями параметров и их значениями
	 * @return Список расходов, удовлетворяющих указанным параметрам.
	 */
	@Transactional(readOnly = true)
	public List<Expense> getCertainExpense(Map<String, String> params) {
		String categoryParam = params.get(CATEGORY_ID);
		String pricesParam = params.get(PRICES);
		String noteParam = params.get(NOTE);		
		String datesParam = params.get(DATES);
		String orderByParam = params.get(ORDER_BY);		
		
		Sort sort = Sort.by(orderByParam);
		List<Expense> expenseList = new LinkedList<>();
		String[] dates = datesParam.split(",");
		LocalDate startDate = LocalDate.parse(dates[0]);
		LocalDate finishDate = LocalDate.parse(dates[1]);
		
		Category category = new Category();
		List<Category> subcategories = new LinkedList<>();
		if (categoryParam != null) {
			category = categoryService.findCategory(Integer.parseInt(categoryParam));			
			if (category.getParent() == null) {
				subcategories = categoryService.getChilds(category);
				subcategories.add(category); //категорию тоже нужно добавить в список категорий, т.к. есть  расходы без подкатегории и расход сохраняется с category_id = id категории
			}
		}
		
		Double totalPriceFrom = 0.0;
		Double totalPriceTo = 0.0;
		if (pricesParam != null) {
			String[] prices = pricesParam.split(",");
			totalPriceFrom = Double.parseDouble(prices[0]);
			totalPriceTo = Double.parseDouble(prices[1]);
		}
	
		if (categoryParam == null && pricesParam == null &&  noteParam == null) {
			expenseList = expenseRepo.findAllByLocalDateBetween(startDate, finishDate, sort);
		} else if (categoryParam != null && pricesParam == null &&  noteParam == null) {
			if (category.getParent() == null) {				
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategoryIn(startDate, finishDate, subcategories, sort);
			} else {
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategory(startDate, finishDate, category, sort);
			}
		} else if (categoryParam != null && pricesParam != null && noteParam == null) {			
			if (category.getParent() == null) {
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategoryInAndTotalPriceBetween(startDate, finishDate, subcategories, totalPriceFrom, totalPriceTo, sort);
			} else {
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategoryAndTotalPriceBetween(startDate, finishDate, category, totalPriceFrom, totalPriceTo, sort);
			}
		} else if (categoryParam != null && pricesParam == null && noteParam != null) {
			if (category.getParent() == null) {
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategoryInAndNoteIgnoreCaseContaining(startDate, finishDate, subcategories, noteParam, sort);
			} else {
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategoryAndNoteIgnoreCaseContaining(startDate, finishDate, category, noteParam, sort);
			}
		} else if (categoryParam == null && pricesParam != null && noteParam == null) {			
			expenseList = expenseRepo.findAllByLocalDateBetweenAndTotalPriceBetween(startDate, finishDate, totalPriceFrom, totalPriceTo, sort);
		} else if (categoryParam == null && pricesParam != null && noteParam != null) {			
			expenseList = expenseRepo.findAllByLocalDateBetweenAndNoteIgnoreCaseContainingAndTotalPriceBetween(startDate, finishDate, noteParam, totalPriceFrom, totalPriceTo, sort);
		} else if (categoryParam == null && pricesParam == null && noteParam != null) {
			expenseList = expenseRepo.findAllByLocalDateBetweenAndNoteIgnoreCaseContaining(startDate, finishDate, noteParam, sort);
		} else if (categoryParam != null && pricesParam != null && noteParam != null) {			
			if (category.getParent() == null) {
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategoryInAndNoteIgnoreCaseContainingAndTotalPriceBetween(startDate, finishDate, subcategories, noteParam, totalPriceFrom, totalPriceTo, sort);
			} else {
				expenseList = expenseRepo.findAllByLocalDateBetweenAndCategoryAndNoteIgnoreCaseContainingAndTotalPriceBetween(startDate, finishDate, category, noteParam, totalPriceFrom, totalPriceTo, sort);
			}
		}
		return expenseList;
	}
	
	/**
	 * Получение Map с категориями и суммой расходов по ним за указанный период.
	 * @param params Map с названиями параметров и их значениями
	 * @return Map с категориями и суммой расходов по ним за указанный период.
	 */
	@Transactional(readOnly = true)
	public Map<String, Double> getDataForBar(Map<String, String> params) {
		String categoryId = params.get(CATEGORY_ID);				
		LocalDate startDate = LocalDate.parse(params.get(DATE_FROM));
		LocalDate finishDate = LocalDate.parse(params.get(DATE_TO));
		Map<String, Double> expenseByCategory = new TreeMap<>();
		if (categoryId != null) {
			Category category = new Category();
			List<Category> subcategories = new LinkedList<>();
			
			category = categoryService.findCategory(Integer.parseInt(categoryId));
			subcategories = categoryService.getChilds(category);
			subcategories.add(category); //для случая, когда в расходе не указана подкатегория и расход сохраняется с category_id = id категории
			List<Expense> expenses = expenseRepo.findAllByLocalDateBetweenAndCategoryIn(startDate, finishDate, subcategories);
			subcategories.forEach(sub -> {
				String name = sub.getName();
				BigDecimal sumBigDec = expenses.stream().filter(e -> e.getCategory().getName().equals(name)).map(Expense::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
				double sum = sumBigDec.doubleValue();
				expenseByCategory.put(name, sum);
			});
			double sumNoSubcategory = expenseByCategory.get(category.getName());
			expenseByCategory.put("Без подкатегории", sumNoSubcategory);
			expenseByCategory.remove(category.getName());
		} else {
			List<Category> categories = categoryService.getParentCategories();
			List<Expense> expenses = expenseRepo.findAllByLocalDateBetween(startDate, finishDate);
			categories.forEach(category -> {
				List<Category> subcategories = categoryService.getChilds(category);
				subcategories.add(category);
				String name = category.getName();
				BigDecimal sumBigDec = expenses.stream().filter(e -> subcategories.contains(e.getCategory())).map(Expense::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
				double sum = sumBigDec.doubleValue();
				expenseByCategory.put(name, sum);
			});				
		}
		return expenseByCategory;
	}
	
	public List<Expense> getAllExpense() {
		List<Expense> expensies = new ArrayList<>();
		Iterable<Expense> expenseIterable = expenseRepo.findAll();
		expenseIterable.forEach(expensies::add);
		return expensies;
	}
}
