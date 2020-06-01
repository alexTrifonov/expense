package com.expense.web;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense.entities.Expense;
import com.expense.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST контроллер для обработки запросов связанных с действиями над Расходами.
 * @author Alexandr Trifonov
 *
 */
@RestController
@RequestMapping("/api")
public class ExpenseController {
	/**
	 * Logger.
	 */
	private Logger logger = LoggerFactory.getLogger(ExpenseController.class);
	
	/**
	 * ObjectMapper для создания json ответов при перехвате исключений.
	 */
	private ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Константа для обозначения кода ошибки на строное базы данных.
	 */
	private final static String BD_ERROR = "BD_ERROR";
	
	/**
	 * Константа для обозначения кода ошибки со стороны клиента.
	 */
	private final static String USER_ERROR = "USER_ERROR";
	
	/**
	 * Константа для обозначения кода ошибки на стророне сервера.
	 */
	private final static String SERVER_ERROR = "SERVER_ERROR";
	
	/**
	 * Сервис для доступа к объектам Расход
	 */
	private ExpenseService expenseService;
	
	@Autowired
	public void setExpenseService(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}
	
	/**
	 * Обработка запросов для получения расхода по идентификатору.
	 * @param idStr Идентификатор расхода.
	 * @return Расход с указанным идентификатором.
	 */
	@GetMapping(path = "/expense/{id}", produces = "application/json")
	public ResponseEntity<?> getExpense(@PathVariable("id") String idStr) {
		ResponseEntity<?> responseEntity;
		try {
			Long id = Long.parseLong(idStr);
			responseEntity = new ResponseEntity<Expense>(expenseService.getExpense(id), HttpStatus.OK);
		} catch(NumberFormatException e) {
			logger.error("getExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (NoSuchElementException e) {
			logger.error("getExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", BD_ERROR);
			errorBody.put("error_message",e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("getExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов для добавления расхода.
	 * @param expense Расход
	 * @return ResponseEntity со статусом HttpStatus.CREATED.
	 */
	@PostMapping(path = "/expense", consumes = "application/json")
	public ResponseEntity<?> addExpense(@RequestBody Expense expense) {
		ResponseEntity<?> responseEntity;
		try {
			expense.setTotalPrice();
			expenseService.addExpense(expense);
			responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
		} catch(Exception e) {
			logger.error("addExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	
	/**
	 * Обработка запросов для обновления расхода с указанным идентификатором.
	 * @param idStr Идентификатор расхода
	 * @param patchExpense Расход с новыми значениями полей.
	 * @return ResponseEntity со статусом HttpStatus.OK.
	 */
	@PutMapping(path = "/expense/{id}", consumes = "application/json")
	public ResponseEntity<?> updateExpense(@PathVariable("id") String idStr, @RequestBody Expense patchExpense) {
		ResponseEntity<?> responseEntity;
		try {			
			Long id = Long.parseLong(idStr);
			expenseService.updateExpense(id, patchExpense);
			responseEntity = new ResponseEntity<>(HttpStatus.OK);			
		} catch(NumberFormatException e) {
			logger.error("updateExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			logger.error("updateExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов на удаление расхода с указанным идентификатором.	
	 * @param idStr Идентификатор расхода
	 * @return ResponseEntity со статусом HttpStatus.NO_CONTENT.
	 */
	@DeleteMapping(path = "/expense/{id}")
	public ResponseEntity<?> deleteExpense(@PathVariable("id") String idStr) {
		ResponseEntity<?> responseEntity;
		try {
			Long id = Long.parseLong(idStr);			
			expenseService.deleteExpense(id);
			responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);			
		} catch(NumberFormatException e) {
			logger.error("deleteExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			logger.error("deleteExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	
	/**
	 * Обработка запросов для получения списка расходов, удовлетворяющих указанным параметрам.
	 * @param params Map с названиями параметров и их значениями
	 * @return Список расходов, удовлетворяющих указанным параметрам.
	 */
	@GetMapping(path = "/expense-certain", produces = "application/json")
	public ResponseEntity<?> getCertainExpense(@RequestParam Map<String, String> params) {
		ResponseEntity<?> responseEntity;
		try {					
			responseEntity = new ResponseEntity<List<Expense>>(expenseService.getCertainExpense(params), HttpStatus.OK);			
		} catch(NumberFormatException | DateTimeParseException e) {
			logger.error("getCertainExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("getCertainExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", "SERVER_ERROR");
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Обработка запросов для получения Map с категориями и суммой расходов по ним за указанный период.
	 * @param params Map с названиями параметров и их значениями
	 * @return Map с категориями и суммой расходов по ним за указанный период.
	 */
	@GetMapping(path = "/expense-bar-data", produces = "application/json")
	public ResponseEntity<?> getExpenseData(@RequestParam Map<String, String> params) {
		ResponseEntity<?> responseEntity;
		try {
			responseEntity = new ResponseEntity<Map<String, Double>>(expenseService.getDataForBar(params), HttpStatus.OK);
		} catch(NumberFormatException | DateTimeParseException e) {
			logger.error("getExpenseData. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("getExpenseData. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	
	/*
	@GetMapping(path = "/expense", produces = "application/json")
	public ResponseEntity<?> getAllExpense() {
		ResponseEntity<?> responseEntity;
		try {			
			responseEntity = new ResponseEntity<List<Expense>>(new ArrayList<Expense>((Collection<? extends Expense>) expenseRepo.findAll()), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("getAllExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	*/
}
