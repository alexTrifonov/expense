package com.expense.jsonview;


/**
 * Класс, используемый при настройке фильтрации сериализуемых полей. 
 * Используется в аннотации @JsonView Jackson.
 * @author Alexandr Trifonov
 *
 */
public class View {
	public static class Public {
		
	}
	public static class Internal extends Public {
		
	}
}
