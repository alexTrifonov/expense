package com.expense.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.ResponseEntity;

import com.expense.entities.Category;
import com.expense.service.CategoryService;


public class CategoryControllerTest {
	private final List<Category> categories = new ArrayList<Category>();
	
	@Before
	public void initCategories() {
		Category category = new Category();
		category.setName("Авто");
		categories.add(category);
	}
	
	@Test
	public void testList() throws Exception {		
		CategoryService categoryService = mock(CategoryService.class);
		when(categoryService.getParentCategories()).thenReturn(categories);
		
		CategoryController categoryController = new CategoryController();
		categoryController.setCategoryService(categoryService);		
		
		ResponseEntity<?> respEntity = categoryController.getAllParentCategory();		
		
		List<Category> list = (List<Category>) respEntity.getBody();
		
		
		Category category = list.get(0);
		
		assertEquals("Авто", category.getName());
	}
}
