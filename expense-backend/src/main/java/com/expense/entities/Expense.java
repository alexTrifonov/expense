package com.expense.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.expense.serializer.LocalDateDeserializer;
import com.expense.serializer.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Класс сущности Расход.
 * @author Alexandr Trifonov
 *
 */
@Entity
@Table(name = "expense")
public class Expense {
	/**
	 * Идентификатор расхода
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	/**
	 * Категория расхода
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_category_id"), nullable = false)
	protected Category category;
	
	/**
	 * Количество единиц расхода.
	 */
	@Column(name = "count", nullable = false)
	protected int count = 1;
	
	//columnDefinition = "NUMERIC", //по умолчанию для BigDecimal в БД будет использован NUMERIC
	/**
	 * Цена единицы расхода.
	 */
	@Column(name = "unit_price", nullable = false)
	protected BigDecimal unitPrice;
	
	/**
	 * Полная цена расхода, учитывает количество единиц расхода и цену единицы расхода.
	 */
	@Column(name = "total_price", nullable = false)
	protected BigDecimal totalPrice;

	/**
	 * Дата расхода.
	 */
	@Column(name = "local_date", nullable = false)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	protected LocalDate localDate;
	
	/**
	 * Примечание к расходу.
	 */
	@Column(name = "note", nullable = true)
	protected String note;
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getCount() {
		return count;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice() {
		this.totalPrice =  this.unitPrice.multiply(new BigDecimal(count)); //this.unitPrice * this.count;
	}
	
	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Expense [id=" + id + ", category=" + category + ", count=" + count + ", totalPrice=" + totalPrice + ", unitPrice="
				+ unitPrice + ", localDate=" + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + count;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((localDate == null) ? 0 : localDate.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((totalPrice == null) ? 0 : totalPrice.hashCode());
		result = prime * result + ((unitPrice == null) ? 0 : unitPrice.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expense other = (Expense) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (count != other.count)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (localDate == null) {
			if (other.localDate != null)
				return false;
		} else if (!localDate.equals(other.localDate))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (totalPrice == null) {
			if (other.totalPrice != null)
				return false;
		} else if (totalPrice.compareTo(other.totalPrice) != 0)
			return false;
		if (unitPrice == null) {
			if (other.unitPrice != null)
				return false;
		} else if (unitPrice.compareTo(other.unitPrice) != 0)
			return false;
		return true;
	}

	
	
}
