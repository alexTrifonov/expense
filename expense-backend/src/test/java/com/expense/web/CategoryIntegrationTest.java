package com.expense.web;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import com.expense.config.DataTestConfig;
import com.expense.config.ServiceTestConfig;
import com.expense.entities.Category;
import com.expense.entities.Expense;
import com.expense.service.CategoryService;
import com.expense.service.ExpenseService;

@SpringJUnitConfig(classes = {DataTestConfig.class, ServiceTestConfig.class})
@DisplayName("CategoryService integration test")
@ActiveProfiles("test")
@WebAppConfiguration
public class CategoryIntegrationTest {
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ExpenseService expenseService;
	
	@Test
	@DisplayName("Category saving")
	@Sql(value = "classpath:db/clean-up.sql",
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void saveCategoryTest() {
		Category category = new Category();
		category.setName("Auto");
		Category savedCategory = categoryService.save(category);
		int id = savedCategory.getId();
		Category findCategory = categoryService.findCategory(id);
		assertEquals(savedCategory.getName(), findCategory.getName());
		
	}
	
	
	@Test
	@DisplayName("Parent categories")
	@Sql(value = "classpath:db/clean-up.sql",
	executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testParentCategoriesGetting() {
		Category auto = new Category();
		auto.setName("Авто");
		auto = categoryService.save(auto);
		
		Category food = new Category();
		food.setName("Продукты");
		food = categoryService.save(food);
		
		Category petrol = new Category();
		petrol.setName("Бензин");
		petrol.setParent(auto);
		petrol = categoryService.save(petrol);
		
		Category meat = new Category();
		meat.setName("Мясо");
		meat.setParent(food);
		meat = categoryService.save(meat);
		
		List<Category> parentCategories = categoryService.getParentCategories();
		assertEquals(2, parentCategories.size());
		assertTrue(parentCategories.contains(auto));
		assertTrue(parentCategories.contains(food));
		
	}
	
	@Test
	@DisplayName("Child categories")
	@Sql(value = "classpath:db/clean-up.sql",
	executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testChildCategories() {
		Category auto = new Category();
		auto.setName("Авто");
		auto = categoryService.save(auto);
		
		Category food = new Category();
		food.setName("Продукты");
		food = categoryService.save(food);
		
		Category petrol = new Category();
		petrol.setName("Бензин");
		petrol.setParent(auto);
		petrol = categoryService.save(petrol);
		
		Category oil = new Category();
		oil.setName("Моторное масло");
		oil.setParent(auto);
		oil = categoryService.save(oil);
		
		Category meat = new Category();
		meat.setName("Мясо");
		meat.setParent(food);
		meat = categoryService.save(meat);
		
		List<Category> childCategories = categoryService.getChilds(auto);
		assertEquals(2, childCategories.size());
		assertTrue(childCategories.contains(petrol));
		assertTrue(childCategories.contains(oil));
	}
	
	@Test
	@DisplayName("Parent categories without expense")
	@Sql(value = "classpath:db/clean-up.sql",
	executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testParentNoExpense() {
		Category auto = new Category();
		auto.setName("Авто");
		auto = categoryService.save(auto);
		
		Category food = new Category();
		food.setName("Продукты");
		food = categoryService.save(food);
		
		Category petrol = new Category();
		petrol.setName("Бензин");
		petrol.setParent(auto);
		petrol = categoryService.save(petrol);
		
		Category oil = new Category();
		oil.setName("Моторное масло");
		oil.setParent(auto);
		oil = categoryService.save(oil);
		
		Category meat = new Category();
		meat.setName("Мясо");
		meat.setParent(food);
		meat = categoryService.save(meat);
		
		Category payments = new Category();
		payments.setName("Регулярные платежи");
		payments = categoryService.save(payments);
		
		Category physic = new Category();
		physic.setName("Медицина");
		physic = categoryService.save(physic);
		
		Expense expenseMeat = new Expense();
		expenseMeat.setCategory(meat);
		expenseMeat.setCount(1);
		expenseMeat.setLocalDate(LocalDate.now());
		expenseMeat.setUnitPrice(BigDecimal.valueOf(350.00));
		expenseMeat.setTotalPrice();
		expenseService.addExpense(expenseMeat);
		
		Expense expensePhysic = new Expense();
		expensePhysic.setCategory(physic);
		expensePhysic.setCount(1);
		expensePhysic.setLocalDate(LocalDate.now());
		expensePhysic.setUnitPrice(BigDecimal.valueOf(1500.00));
		expensePhysic.setTotalPrice();
		expenseService.addExpense(expensePhysic);
		
		List<Category> parentCategoryNoExpense = categoryService.getParentCategoriesNoExpense();
		assertEquals(2, parentCategoryNoExpense.size());
		assertTrue(parentCategoryNoExpense.contains(auto));
		assertTrue(parentCategoryNoExpense.contains(payments));
	}
	
	@Test
	@DisplayName("Child categories without expense")
	@Sql(value = "classpath:db/clean-up.sql",
	executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testChildNoExpense() {
		Category auto = new Category();
		auto.setName("Авто");
		auto = categoryService.save(auto);
		
		Category food = new Category();
		food.setName("Продукты");
		food = categoryService.save(food);
		
		Category petrol = new Category();
		petrol.setName("Бензин");
		petrol.setParent(auto);
		petrol = categoryService.save(petrol);
		
		Category oil = new Category();
		oil.setName("Моторное масло");
		oil.setParent(auto);
		oil = categoryService.save(oil);
		
		Category meat = new Category();
		meat.setName("Мясо");
		meat.setParent(food);
		meat = categoryService.save(meat);
		
		Category vegetable = new Category();
		vegetable.setName("Овощи");
		vegetable.setParent(food);
		vegetable = categoryService.save(vegetable);
		
		Category nuts = new Category();
		nuts.setName("Орехи");
		nuts.setParent(food);
		nuts = categoryService.save(nuts);
		
		Expense expenseMeat = new Expense();
		expenseMeat.setCategory(meat);
		expenseMeat.setCount(1);
		expenseMeat.setLocalDate(LocalDate.now());
		expenseMeat.setUnitPrice(BigDecimal.valueOf(350.00));
		expenseMeat.setTotalPrice();
		expenseService.addExpense(expenseMeat);
		
		List<Category> childNoExpense = categoryService.getChildCategoriesNoExpense(food.getId());
		assertEquals(2, childNoExpense.size());
		assertTrue(childNoExpense.contains(vegetable));
		assertTrue(childNoExpense.contains(nuts));
		
	}
	
	
}
