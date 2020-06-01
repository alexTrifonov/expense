package com.expense.web;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@DisplayName("ExpenseService integration test")
@ActiveProfiles("test")
@WebAppConfiguration
public class ExpenseIntegrationTest {
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ExpenseService expenseService;
	
	@Test
	@DisplayName("Expense saving, getting, updating")
	@Sql(value = "classpath:db/clean-up.sql",
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testUpdate() {
		Category food = new Category();
		food.setName("Продукты");
		food = categoryService.save(food);
		
		Category meat = new Category();
		meat.setName("Мясо");
		meat.setParent(food);
		meat = categoryService.save(meat);
		
		Expense expenseMeat = new Expense();
		expenseMeat.setCategory(meat);
		expenseMeat.setCount(1);
		expenseMeat.setLocalDate(LocalDate.now());
		expenseMeat.setUnitPrice(BigDecimal.valueOf(350.00));
		expenseMeat.setTotalPrice();
		expenseMeat = expenseService.addExpense(expenseMeat);
		
		long id = expenseMeat.getId();
		expenseMeat.setCategory(food);
		expenseMeat.setCount(2);
		expenseMeat.setUnitPrice(BigDecimal.valueOf(450.00));
		LocalDate localDate = LocalDate.of(2020, 1, 1);
		expenseMeat.setLocalDate(LocalDate.of(2020, 1, 1));
		
		expenseService.updateExpense(id, expenseMeat);
		Expense updatedExpense = expenseService.getExpense(id);
		assertEquals(food, updatedExpense.getCategory());
		assertEquals(0, BigDecimal.valueOf(900.00).compareTo(updatedExpense.getTotalPrice()));
		assertEquals(localDate, updatedExpense.getLocalDate());
		
	}
	
	@Test
	@DisplayName("Certain Expense")
	@Sql(value = "classpath:db/clean-up.sql",
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testCertainExpense() {
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
		expenseMeat.setLocalDate(LocalDate.of(2020, 5, 5));
		expenseMeat.setUnitPrice(BigDecimal.valueOf(350.00));
		expenseMeat.setTotalPrice();
		expenseMeat = expenseService.addExpense(expenseMeat);
		
		Expense expenseVegetable = new Expense();
		expenseVegetable.setCategory(vegetable);
		expenseVegetable.setCount(1);
		expenseVegetable.setLocalDate(LocalDate.of(2020, 5, 6));
		expenseVegetable.setUnitPrice(BigDecimal.valueOf(150.00));
		expenseVegetable.setTotalPrice();
		expenseVegetable = expenseService.addExpense(expenseVegetable);
		
		Expense expensePetrol = new Expense();
		expensePetrol.setCategory(petrol);
		expensePetrol.setCount(1);
		expensePetrol.setLocalDate(LocalDate.of(2020, 5, 7));
		expensePetrol.setUnitPrice(BigDecimal.valueOf(1500.00));
		expensePetrol.setTotalPrice();
		expensePetrol = expenseService.addExpense(expensePetrol);
		
		Expense expenseNut = new Expense();
		expenseNut.setCategory(nuts);
		expenseNut.setCount(1);
		expenseNut.setLocalDate(LocalDate.of(2020, 3, 8));
		expenseNut.setUnitPrice(BigDecimal.valueOf(300.00));
		expenseNut.setTotalPrice();
		expenseNut = expenseService.addExpense(expenseNut);
		
		Map<String, String> params = new HashMap<>();
		params.put("dates", "2020-05-01,2020-05-31");
		params.put("orderBy", "localDate");
		params.put("categoryId", food.getId().toString());
		
		List<Expense> certainExpense = expenseService.getCertainExpense(params);
		assertEquals(2, certainExpense.size());
		assertTrue(certainExpense.contains(expenseMeat));
		assertTrue(certainExpense.contains(expenseVegetable));
	}
	
	@Test
	@DisplayName("Data for bar")
	@Sql(value = "classpath:db/clean-up.sql",
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testDataForBar() {
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
		expenseMeat.setLocalDate(LocalDate.of(2020, 5, 5));
		expenseMeat.setUnitPrice(BigDecimal.valueOf(350.00));
		expenseMeat.setTotalPrice();
		expenseMeat = expenseService.addExpense(expenseMeat);
		
		Expense expenseVegetable = new Expense();
		expenseVegetable.setCategory(vegetable);
		expenseVegetable.setCount(1);
		expenseVegetable.setLocalDate(LocalDate.of(2020, 5, 6));
		expenseVegetable.setUnitPrice(BigDecimal.valueOf(150.00));
		expenseVegetable.setTotalPrice();
		expenseVegetable = expenseService.addExpense(expenseVegetable);
		
		Expense expensePetrol = new Expense();
		expensePetrol.setCategory(petrol);
		expensePetrol.setCount(1);
		expensePetrol.setLocalDate(LocalDate.of(2020, 5, 7));
		expensePetrol.setUnitPrice(BigDecimal.valueOf(1500.00));
		expensePetrol.setTotalPrice();
		expensePetrol = expenseService.addExpense(expensePetrol);
		
		Expense expenseNut = new Expense();
		expenseNut.setCategory(nuts);
		expenseNut.setCount(1);
		expenseNut.setLocalDate(LocalDate.of(2020, 3, 8));
		expenseNut.setUnitPrice(BigDecimal.valueOf(300.00));
		expenseNut.setTotalPrice();
		expenseNut = expenseService.addExpense(expenseNut);
		
		Map<String, String> params = new HashMap<>();
		params.put("dateFrom", "2020-05-01");
		params.put("dateTo", "2020-05-31");
		
		Map<String, Double> dataForBar = expenseService.getDataForBar(params);
		assertEquals(2, dataForBar.size());
		assertEquals(0, Double.compare(500, dataForBar.get("Продукты")));
		assertEquals(0, Double.compare(1500, dataForBar.get("Авто")));
	}
}
