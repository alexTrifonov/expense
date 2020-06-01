<template>
  <div>
    <div class="container">
      <div class="page-header">
        <h3>Редактирование расхода</h3>
      </div>
      <alert-error
        :message="messageServerErrorExpense"
        :showAlert="showServerErrorExpense"
        id="alert-error-expense"
      ></alert-error>
      <alert-error
        :message="messageServerErrorCategory"
        :showAlert="showServerErrorCategory"
        id="alert-error-category"
      ></alert-error>
      <alert-error
        :message="messageServerErrorSubcategory"
        :showAlert="showServerErrorSubcategory"
        id="alert-error-subcategory"
      ></alert-error>
      <alert-error
        :message="messageClientError"
        :showAlert="showClientError"
        id="alert-client-error"
      ></alert-error>
      <alert-error
        :message="messageUnknownError"
        :showAlert="showUnknownError"
        id="alert-unknown-error"
      ></alert-error>
      <alert-error-list :errors="errors" :showAlert="showFormError"></alert-error-list>
      <form class="form-horizontal" @submit.prevent="updateExpense">
        <div class="row my-4">
          <div class="col-lg-6">
            <label for="category-select">Категория расхода:</label>
            <select class="form-control" v-model="selectedCategory" id="category-select">
              <option
                v-for="category in categories"
                v-bind:value="category"
                v-bind:key="category.id"
              >{{category.name}}</option>
            </select>
          </div>
          <div class="col-lg-6">
            <label for="subcategory-select">Подкатегория расхода:</label>
            <select class="form-control" v-model="selectedSubcategory" id="subcategory-select">
              <option
                v-for="subcategory in subcategories"
                v-bind:value="subcategory"
                v-bind:key="subcategory.id"
              >{{subcategory.name}}</option>
            </select>
          </div>
        </div>

        <div class="row my-4">
          <div class="col-lg-4">
            <div class="input-group-prepend">
              <span class="input-group-text" id="label-unitPrice">Цена за единицу</span>
              <input
                class="form-control"
                v-model.lazy="unitPrice"
                v-money="money"
                aria-describedby="label-unit-price"
              />
            </div>
          </div>

          <div class="col-lg-4">
            <div class="input-group-prepend">
              <span class="input-group-text" id="label-count">Количество</span>
              <input
                type="number"
                min="1"
                step="1"
                class="form-control"
                v-model="count"
                aria-describedby="label-count"
              />
            </div>
          </div>

          <div class="col-lg-4">
            <div class="input-group-prepend">
              <span class="input-group-text" id="label-note">Примечание</span>
              <input type="text" class="form-control" v-model="note" aria-describedby="label-note" />
            </div>
          </div>
        </div>
        <div class="row my-4">
          <div class="col-lg-6">
            <label for="datepicker">Дата:</label>
            <datepicker
              @selected="dateClick"
              v-model="selectedDate"
              :inline="true"
              :monday-first="true"
              :language="ru"
              id="datepicker"
            ></datepicker>
          </div>
        </div>
        <button type="submit" class="btn btn-primary">Обновить расход</button>
      </form>
    </div>
  </div>
</template>

<script>
import Datepicker from "vuejs-datepicker";
import { en, ru } from "vuejs-datepicker/dist/locale";
import { VMoney } from "v-money";
import AlertErrorList from "@/components/AlertErrorList";
import AlertError from "@/components/AlertError";
import {AXIOS} from './http-common'

export default {
  name: "ExpenseEdit",
  components: {
    "alert-error-list": AlertErrorList,
    "alert-error": AlertError,
    Datepicker
  },
  data() {
    return {
      expense: {},
      categories: [],
      subcategories: [],
      selectedCategory: { id: null, name: "no-category" },
      selectedSubcategory: { id: null, name: "<Без подкатегории>" },
      selectedDate: new Date(),
      en: en,
      ru: ru,
      unitPrice: 0.0,
      money: {
        decimal: ".",
        thousands: " ",
        precision: 2
      },
      count: 1,
      note: null,
      errors: [],
      showFormError: false,
      showServerErrorExpense: false,
      messageServerErrorExpense: "Ошибка получения расходов на стороне сервера",
      showServerErrorCategory: false,
      messageServerErrorCategory: "Ошибка получения категорий на стороне сервера",
      showServerErrorSubcategory: false,
      messageServerErrorSubcategory: "Ошибка получения подкатегорий на стороне сервера",
      messageClientError: "",
      showClientError: false,
      messageUnknownError: "",
      showUnknownError: false
    };
  },
  async mounted() {
    await AXIOS
      .get("/category")
      .then(
        response => (
          (this.categories = response.data),
          (this.showServerErrorCategory = false),
          (this.showClientError = false),
          (this.showUnknownError = false)
        )
      )
      .catch(err => {
        if (err.response) {               
          if (err.response.data.error === "SERVER_ERROR") {
            this.messageServerErrorCategory =
              "Ошибка получения списка категорий на стороне сервера.";            
          }
          this.showServerErrorCategory = true;
        } else if (err.request) {        
          this.messageClientError = "Ошибка сети";
          this.showClientError = true;
        } else {
          this.messageUnknownError = "Неизвестная ошибка";
          this.showUnknownError = true;
        }
      });

    await AXIOS
      .get(
        "/expense/" + this.$route.params.id
      )
      .then(
        response => (
          (this.expense = response.data),
          (this.showServerErrorExpense = false),
          (this.showClientError = false),
          (this.showUnknownError = false)
        )
      )
      .catch(err => {
        if (err.response) {            
          if (err.response.data.error === "BD_ERROR") {
            this.messageServerErrorExpense = "Не удалось получить расход. Запрошен несуществующий расход";            
          } else if (err.response.data.error === "USER_ERROR") {
            this.messageServerErrorExpense =
              "Ошибка запроса расхода со стороны клиента. В запросе некорректно указан идентификатор расхода";
          } else if (err.response.data.error === "SERVER_ERROR") {
            this.messageServerErrorExpense = "Не удалось получить расход. Внутренняя ошибка сервера.";
          }
          this.showServerErrorExpense = true;
        } else if (err.request) {         
          this.messageClientError = "Ошибка сети";
          this.showClientError = true;
        } else {
          this.messageUnknownError = "Неизвестная ошибка";
          this.showUnknownError = true;
        }
      });

    let expenseCategory =
      this.expense.category.parent == null
        ? this.expense.category
        : this.expense.category.parent;

    this.selectedCategory = this.categories.find(
      category => category.id === expenseCategory.id
    );
    this.selectedSubcategory =
      this.expense.category.parent != null
        ? this.expense.category
        : { id: null, name: "<Без подкатегории>" };
    this.unitPrice = this.expense.unitPrice.toFixed(2);
    this.count = this.expense.count;
    this.note = this.expense.note;
    this.selectedDate = new Date(this.expense.localDate);
  },
  watch: {
    selectedCategory: function() {
      AXIOS
        .get(
          "/category-child/" +
            this.selectedCategory.id
        )
        .then(
          response => (
            (this.subcategories = response.data),
            this.subcategories.unshift({
              id: null,
              name: "<Без подкатегории>"
            }),
            (this.showServerErrorSubcategory = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {                  
            if (err.response.data.error === "USER_ERROR") {
              this.messageServerErrorSubcategory =
                "Ошибка запроса списка подкатегорий со стороны клиента. В запросе некорректно указан идентификатор категории.";
            } else if (err.response.data.error === "SERVER_ERROR") {
              this.messageServerErrorSubcategory = "Ошибка запроса списка подкатегорий. Внутренняя ошибка сервера.";
            }
            this.showServerErrorSubcategory = true;
          } else if (err.request) {          
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
    }
  },
  directives: {
    money: VMoney
  },
  methods: {
    async updateExpense() {
      this.errors = [];
      if (this.unitPrice <= 0) {
        this.errors.push(
          "Сумма расхода не может быть нулевой либо отрицательной"
        );
      }

      if (this.note != null) {
        this.note = this.note.trim();
        if (this.note.length > 30) {
          this.errors.push(
            "Значение примечания может быть пустым либо строкой с длиной не более 30 символов"
          );
        } else if (this.note.length == 0) {
          this.note = null;
        }
      }

      if (!this.errors.length) {
        await AXIOS
          .put(
            "/expense/" + this.expense.id,
            {
              category: {
                id:
                  this.selectedSubcategory.id != null
                    ? this.selectedSubcategory.id
                    : this.selectedCategory.id
              },
              count: this.count,
              unitPrice: this.unitPrice.replace(/\s/g, ''),
              localDate: this.onlyDateISO(this.selectedDate),
              note: this.note
            }
          )
          .then(            
              this.showFormError = false,
              this.showServerErrorExpense = false,
              this.showClientError = false,
              this.showUnknownError = false,
              this.$router.push({ path: "/" })                          
          )
          .catch(err => {
            if (err.response) {                        
              if (err.response.data.error === "USER_ERROR") {
                this.messageServerErrorExpense =
                  "Ошибка обновления расхода со стороны клиента. В запросе некорректно указан идентификатор расхода";                
              } else if (err.response.data.error === "SERVER_ERROR") {
                this.messageServerErrorExpense = "Не удалось обновить расход. Внутренняя ошибка сервера.";
              }
              this.showServerErrorExpense = true;
            } else if (err.request) {             
              this.messageClientError = "Ошибка сети";
              this.showClientError = true;
            } else {
              this.messageUnknownError = "Неизвестная ошибка";
              this.showUnknownError = true;
            }
          });
      } else {
        this.showFormError = true;
      }
    },
    onlyDateISO: function(date) {
      let year = date.getFullYear();
      let month = date.getMonth() + 1;
      let day = date.getDate();

      if (day < 10) {
        day = "0" + day;
      }
      if (month < 10) {
        month = "0" + month;
      }
      return year + "-" + month + "-" + day;
    }
  }
};
</script>


<style scoped>
</style>