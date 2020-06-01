<template>
  <div>
    <div class="container">
      <div class="page-header">
        <h3>Добавление категорий</h3>
      </div>
      <alert-error
        :message="messageErrorCategory"
        :showAlert="showErrorCategory"
        id="alert-error-category"
      ></alert-error>
      <alert-error
        :message="messageErrorFreeCategory"
        :showAlert="showErrorFreeCategory"
        id="alert-error-free-category"
      ></alert-error>
      <alert-error
        :message="messageErrorActionOnCategory"
        :showAlert="showErrorActionOnCategory"
        id="alert-error-action-category"
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
      <b-alert
        :show="dismissCountDown"
        dismissible
        fade
        variant="success"
        @dismiss-count-down="countDownChanged"
      >{{messageSuccessCategory}}</b-alert>
      <div class="row my-4">
        <div class="col lg-6">
          <label for="category-list">Список существующих категорий расхода</label>
          <ul class="list-group" id="category-list">
            <li
              class="list-group-item py-2"
              v-for="category in categories"
              v-bind:value="category"
              v-bind:key="category.id"
            >{{ category.name}}</li>
          </ul>
        </div>
        <div class="col lg-6">
          <form @submit.prevent="submitCategory">
            <div class="form-group">
              <label for="category-input">Название новой категории расхода:</label>
              <input
                type="text"
                class="form-control"
                id="category-input"
                v-model="category_name"
                placeholder="Введите название категории"
              />
            </div>
            <button>Добавить категорию</button>
          </form>
        </div>
      </div>
      <div class="page-header">
        <h3>Удаление категорий</h3>
      </div>
      <div class="row my-4">
        <small
          id="tableHelp"
          class="form-text text-muted"
        >Для удаления доступны только категории, по которым нет расходов ни по самим категориям, ни по их подкатегориям</small>
        <hr />
        <br />
        <table class="table table-sm table-bordered" aria-describedby="tableHelp">
          <thead>
            <tr>
              <th scope="col">Категория</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="category in freeCategories" v-bind:key="category.id">
              <td class="align-middle">{{ category.name }}</td>
              <td>
                <button
                  type="button"
                  class="btn btn-danger btn-sm"
                  v-on:click="deleteCategory(category.id)"
                >Удалить</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import {AXIOS} from './http-common'
import AlertErrorList from "@/components/AlertErrorList";
import AlertError from "@/components/AlertError";

export default {
  name: "Category",
  components: {
    "alert-error-list": AlertErrorList,
    "alert-error": AlertError
  },
  data() {
    return {
      categories: [],
      freeCategories: [],
      category_name: "",
      parent_category_id: "",
      errors: [],
      showFormError: false,
      showErrorCategory: false,
      messageErrorCategory: "Ошибка получения категорий на стороне сервера",
      showErrorFreeCategory: false,
      messageErrorFreeCategory:
        "Ошибка получения категорий без расходов на стороне сервера",
      dismissSecs: 1,
      dismissCountDown: 0,
      messageSuccessCategory: "",
      showErrorActionOnCategory: false,
      messageErrorActionOnCategory:
        "Ошибка выполнения действия (добавления, удаления) над категорией",
      messageClientError: "",
      showClientError: false,
      messageUnknownError: "",
      showUnknownError: false
    };
  },
  mounted() {
    this.categoryList();
    this.freeCategoryList();
  },
  methods: {
    categoryList() {
      AXIOS
        .get("/category")
        .then(
          response => (
            (this.categories = response.data),
            (this.showErrorCategory = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {           
            if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorCategory =
                "Ошибка получения списка категорий на стороне сервера.";
            }
            this.showErrorCategory = true;
          } else if (err.request) {           
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
    },
    freeCategoryList() {
      AXIOS
        .get("/free-category")
        .then(
          response => (
            (this.freeCategories = response.data),
            (this.showErrorFreeCategory = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {          
            if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorFreeCategory =
                "Ошибка получения списка категорий без расходов на стороне сервера.";
            }
            this.showErrorFreeCategory = true;
          } else if (err.request) {           
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
    },
    async submitCategory() {
      this.errors = [];
      if (!this.category_name) {
        this.errors.push("Укажите название категории");
      } else if (!this.validName(this.category_name)) {
        this.errors.push(
          "Название категории должно быть длиной от 2 до 20 символов и начинаться с заглавной буквы. Может содержать только буквы, цифры, знак тире, пробел и запятую."
        );
      }

      if (!this.errors.length) {
        await AXIOS
          .post("/category", {
            name: this.category_name
          })
          .then(            
              (this.showFormError = false),
              (this.showErrorActionOnCategory = false),
              (this.messageSuccessCategory = "Категория успешно добавлена"),
              (this.dismissCountDown = this.dismissSecs),
              (this.showClientError = false),
              (this.showUnknownError = false)
          )
          .catch(err => {
            if (err.response) {            
              if (err.response.data.error === "SERVER_ERROR") {
                this.messageErrorActionOnCategory =
                  "Не удалось добавить категорию. Внутренняя ошибка сервера";
              }
              this.showErrorActionOnCategory = true;
            } else if (err.request) {            
              this.messageClientError = "Ошибка сети";
              this.showClientError = true;
            } else {
              this.messageUnknownError = "Неизвестная ошибка";
              this.showUnknownError = true;
            }
          });
      }
      this.categoryList();
      this.freeCategoryList();
      this.category_name = "";
    },
    async deleteCategory(categoryId) {
      await AXIOS
        .delete("/category/" + categoryId)
        .then(      
            (this.showErrorActionOnCategory = false),
            (this.messageSuccessCategory = "Категория успешно удалена"),
            (this.dismissCountDown = this.dismissSecs),
            (this.showClientError = false),
            (this.showUnknownError = false)          
        )
        .catch(err => {
          if (err.response) {          
            if (err.response.data.error === "USER_ERROR") {
              this.messageErrorActionOnCategory =
                "Ошибка удаления категории со стороны клиента. В запросе некорректно указан идентификатор категории";
            } else if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorActionOnCategory =
                "Ошибка удаления категории на стороне базы данных.";
            }
            this.showErrorActionOnCategory = true;
          } else if (err.request) {         
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
      this.freeCategoryList();
      this.categoryList();
    },
    validName: function(name) {
      let re = /^\p{Lu}[\p{L}\d\p{Zs},-]{1,20}$/u;
      name = name.trim();
      return re.test(name);
    },
    countDownChanged(dismissCountDown) {
      this.dismissCountDown = dismissCountDown;
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
