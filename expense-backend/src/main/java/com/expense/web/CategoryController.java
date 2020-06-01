package com.expense.web;

import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.entities.Category;
import com.expense.jsonview.View;
import com.expense.service.CategoryService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST контроллер для обработки запросов связанных с действиями над Категориями.
 * @author Alexandr Trifonov
 *
 */
@RestController()
@RequestMapping("/api")
public class CategoryController {
	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(CategoryController.class);
	
	/**
	 * ObjectMapper для создания json ответов при перехвате исключений.
	 */
	private final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Константа для обозначения кода ошибки со стороны клиента.
	 */
	private final static String USER_ERROR = "USER_ERROR";
	
	/**
	 * Константа для обозначения кода ошибки на стророне сервера.
	 */
	private final static String SERVER_ERROR = "SERVER_ERROR";
	
	/**
	 * Сервис для доступа к объектам Категория
	 */
	private CategoryService categoryService;
	
	@Autowired
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Обработка запросов для получения всех родительских категорий.
	 * @return Список родительских категорий.
	 */
	@JsonView(View.Public.class)
	@GetMapping(path = "/category", produces = "application/json")
	public ResponseEntity<?> getAllParentCategory() {
		ResponseEntity<?> responseEntity;
		HttpHeaders httpHeaders = new HttpHeaders();		
		try {			
			responseEntity = new ResponseEntity<List<Category>>(categoryService.getParentCategories(), httpHeaders, HttpStatus.OK);
		} catch(Exception e) {
			logger.error("getAllParentCategory. error = {}, error class = {}", e.getMessage(), e.getClass(), e);			
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов для добавления родительских категорий.
	 * @param category Категория для добавления.
	 * @return ResponseEntity со статусом HttpStatus.CREATED.
	 */
	@PostMapping(path = "/category", consumes = "application/json")
	public ResponseEntity<?> addCategory(@RequestBody Category category) {
		ResponseEntity<?> responseEntity;
		try {			
			categoryService.save(category);
			responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("addCategory. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов на удаление категории. При удалении родительской категории удаляются все её подкатегории.
	 * @param idStr Идентификатор категории.
	 * @return ResponseEntity со статусом HttpStatus.NO_CONTENT
	 */
	@DeleteMapping(path = "/category/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable("id") String idStr) {
		ResponseEntity<?> responseEntity;
		try {			
			Integer id = Integer.parseInt(idStr);
			Category parentCategory = categoryService.findCategory(id);
			if (parentCategory.getParent() == null) {
				List<Category> subcategories = categoryService.getChilds(parentCategory);
				categoryService.deleteCategoryList(subcategories);
			}
			categoryService.deleteCategory(id);
			responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch(NumberFormatException e) {
			logger.error("deleteCategory. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			logger.error("deleteCategory. error  = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов на добавление подкатегории для указанной категории.
	 * @param parentIdStr Идентификатор родительской категории.
	 * @param child Подкатегория
	 * @return ResponseEntity со статусом HttpStatus.CREATED
	 */
	@PostMapping(path = "/category-child/{id}", consumes = "application/json")
	public ResponseEntity<?> addCategoryChild(@PathVariable("id") String parentIdStr, @RequestBody Category child) {
		ResponseEntity<?> responseEntity;
		try {
			Integer parentId = Integer.parseInt(parentIdStr);
			Category parent = categoryService.findCategory(parentId);
			child.setParent(parent);
			categoryService.save(child);
			responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
		} catch(NumberFormatException e) {
			logger.error("addCategoryChild. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("addCategoryChild. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов на получение всех подкатегорий для данной категории.
	 * @param idStr Идентификатор категории.
	 * @return Список подкатегорий.
	 */
	@JsonView(View.Internal.class)
	@GetMapping(path = "/category-child/{id}", produces = "application/json")
	public ResponseEntity<?> getCategoryChild(@PathVariable("id") String idStr) {
		ResponseEntity<?> responseEntity;
		try {			
			Integer id = Integer.parseInt(idStr);
			Category parent = categoryService.findCategory(id);
			responseEntity = new ResponseEntity<List<Category>>(categoryService.getChilds(parent), HttpStatus.OK);
		} catch(NumberFormatException e) {
			logger.error("getCategoryChild. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("getCategoryChild. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов на получение всех категорий без расходов (в том числе при наличии подкатегорий без расходов по подкатегориям). 
	 * @return Список категорий без расходов.
	 */
	@JsonView(View.Public.class)
	@GetMapping(path = "/free-category", produces = "application/json")
	public ResponseEntity<?> getFreeCategory() {
		ResponseEntity<?> responseEntity;
		try {			
			responseEntity = new ResponseEntity<Iterable<Category>>(categoryService.getParentCategoriesNoExpense(), HttpStatus.OK);
		} catch(Exception e) {
			logger.error("getFreeCategory. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов на получение всех подкатегорий без расходов для данной категории. 
	 * @param parentIdStr Идентификатор родительской категории.
	 * @return Список подкатегорий без расходов.
	 */
	@JsonView(View.Internal.class)
	@GetMapping(path = "/free-category-child/{id}", produces = "application/json")
	public ResponseEntity<?> getFreeCategoryChild(@PathVariable("id") String parentIdStr) {
		ResponseEntity<?> responseEntity;
		try {			
			Integer parentId = Integer.parseInt(parentIdStr);			
			responseEntity = new ResponseEntity<List<Category>>(categoryService.getChildCategoriesNoExpense(parentId), HttpStatus.OK);
		} catch(NumberFormatException e) {
			logger.error("getFreeCategoryChild. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("getFreeCategoryChild. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/*
	@JsonView(View.Internal.class)
	@GetMapping(path = "/category/{id}", produces = "application/json")
	public ResponseEntity<?> getCategory(@PathVariable("id") String idStr) {
		ResponseEntity<?> responseEntity;
		try {
			Integer id = Integer.parseInt(idStr);
			responseEntity = new ResponseEntity<Category>(categoryRepo.findById(id).get(), HttpStatus.OK);
		} catch(NumberFormatException e) {
			logger.error("getCategory. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (NoSuchElementException noSuchExc) {
			logger.error("getCategory. error = {}, error class = {}", noSuchExc.getMessage(), noSuchExc.getClass(), noSuchExc);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", BD_ERROR);
			errorBody.put("error_message", noSuchExc.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("getCategory. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	*/
	
	
}
