package com.expense.entities;

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

import com.expense.jsonview.View;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Класс сущности Категория расхода. У категории могут быть подкатегории, но не более чем на одном уровне вложенности.
 * @author Alexandr Trifonov
 *
 */
@Entity
@Table(name="category")
public class Category {
	/**
	 * Идентификатор категории
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonView(View.Public.class)
	private Integer id;
	
	/**
	 * Имя категории
	 */
	@Column(name = "name", nullable = false, unique = true)
	@JsonView(View.Public.class)
	private String name;
	
	/**
	 * У категории может быть один предок.
	 */
	@ManyToOne(fetch = FetchType.EAGER) //по умолчанию EAGER. FetchType.EAGER указано для ясности
	@JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_category_parent_id"))
	@JsonView(View.Internal.class)
	protected Category parent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) throws NullPointerException, IllegalStateException {
		if (parent == null) {
			throw new NullPointerException("Can't set null parent category");
		}
		//у категории может быть только один уровень потомков или не быть потомков вовсе
		if (this.parent != null) {
			throw new IllegalStateException("Parent is already assigned to a category");
		}
		if (parent.getParent() != null) {
			throw new IllegalStateException("Сategory cannot have more than one ancestor");
		}
		this.parent = parent;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
		Category other = (Category) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", parent=" + parent + "]";
	}
	
	
}
