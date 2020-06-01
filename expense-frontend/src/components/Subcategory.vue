<template>
  <div>
    <div class="container">
      <div class="page-header">
        <h3>Добавление подкатегорий</h3>
      </div>
      <alert-error
        :message="messageErrorCategory"
        :showAlert="showErrorCategory"
        id="alert-error-category"
      ></alert-error>
      <alert-error
        :message="messageErrorSubcategory"
        :showAlert="showErrorSubcategory"
        id="alert-error-subcategory"
      ></alert-error>
      <alert-error
        :message="messageErrorFreeSubcategory"
        :showAlert="showErrorFreeSubcategory"
        id="alert-error-free-subcategory"
      ></alert-error>
      <alert-error
        :message="messageErrorActionOnSubcategory"
        :showAlert="showErrorActionOnSubcategory"
        id="alert-error-action-subcategory"
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
      >{{ messageSuccessSubcategory }}</b-alert>
      <div class="row my-4">
        <div class="col lg-6">
          <label for="subcategory-list">Список существующих подкатегорий для выбранной категории</label>
          <ul class="list-group" id="subcategory-list">
            <li
              class="list-group-item py-2"
              v-for="subcategory in subcategories"
              v-bind:value="subcategory"
              v-bind:key="subcategory.id"
            >{{ subcategory.name}}</li>
          </ul>
        </div>
        <div class="col lg-6">
          <div class="form-group">
            <label for="select-category">Kатегория</label>
            <select class="form-control" v-model="selectedCategory" id="select-category">
              <option
                v-for="category in categories"
                v-bind:value="category"
                v-bind:key="category.id"
              >{{category.name}}</option>
            </select>
          </div>
          <form @submit.prevent="submitSubcategory">
            <div class="form-group">
              <label for="input-subcategory">Название новой подкатегории</label>
              <input
                type="text"
                class="form-control"
                id="input-subcategory"
                v-model="subcategoryName"
                placeholder="Введите название подкатегории"
              />
            </div>
            <button type="submit" class="btn btn-primary">Добавить подкатегорию</button>
          </form>
        </div>
      </div>
      <div class="page-header">
        <h3>Удаление подкатегорий</h3>
      </div>
      <div class="row my-4">
        <small
          id="tableHelp"
          class="form-text text-muted"
        >Для удаления доступны только подкатегории, по которым нет расходов</small>
        <hr />
        <br />
        <table class="table table-sm table-bordered" id="subcategory-table">
          <thead>
            <tr>
              <th scope="col">Подкатегория</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="subcategory in freeSubcategories" v-bind:key="subcategory.id">
              <td class="align-middle">{{ subcategory.name }}</td>
              <td>
                <button
                  type="button"
                  class="btn btn-danger btn-sm"
                  v-on:click="deleteSubcategory(subcategory.id)"
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
import AlertErrorList from "@/components/AlertErrorList";
import AlertError from "@/components/AlertError";
import {AXIOS} from './http-common'

export default {
  name: "Subcategory",
  components: {
    "alert-error-list": AlertErrorList,
    "alert-error": AlertError
  },
  data() {
    return {
      categories: [],
      subcategories: [],
      freeSubcategories: [],
      subcategoryName: "",
      selectedCategory: null,
      errors: [],
      showFormError: false,
      showErrorCategory: false,
      messageErrorCategory: "Ошибка получения категорий на стороне сервера",
      showErrorSubcategory: false,
      messageErrorSubcategory:
        "Ошибка получения подкатегорий на стороне сервера",
      showErrorFreeSubcategory: false,
      messageErrorFreeSubcategory:
        "Ошибка получения категорий без расходов на стороне сервера",
      dismissSecs: 1,
      dismissCountDown: 0,
      messageSuccessSubcategory: "",
      showErrorActionOnSubcategory: false,
      messageErrorActionOnSubcategory:
        "Ошибка выполнения действия (добавления, удаления) над подкатегорией",
      messageClientError: "",
      showClientError: false,
      messageUnknownError: "",
      showUnknownError: false
    };
  },
  mounted() {
    this.categoryList();
  },
  watch: {
    selectedCategory: function() {
      if (this.selectedCategory.id != null) {
        this.subcategoryList();
        this.freeSubcategoryList();
      }
    }
  },
  methods: {
    async categoryList() {
      await AXIOS
        .get("/category")
        .then(
          response => (
            (this.categories = response.data),
            (this.selectedCategory =
              this.categories.length != 0
                ? this.categories[0]
                : { id: null, name: null, parent: null }),
            (this.showErrorCategory = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {          
            if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorCategory =
                "Ошибка получения списка категорий на стороне базы данных.";
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
    subcategoryList() {
      AXIOS
        .get(
          "/category-child/" +
            this.selectedCategory.id
        )
        .then(
          response => (
            (this.subcategories = response.data),
            (this.showErrorSubcategory = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {         
            if (err.response.data.error === "USER_ERROR") {
              this.messageErrorSubcategory =
                "Ошибка запроса списка подкатегорий со стороны клиента. В запросе некорректно указан идентификатор категории.";
            } else if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorSubcategory =
                "Ошибка запроса списка подкатегорий. Внутренняя ошибка сервера.";
            }
            this.showErrorSubcategory = true;
          } else if (err.request) {           
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
    },
    freeSubcategoryList() {
      AXIOS
        .get(
          "/free-category-child/" +
            this.selectedCategory.id
        )
        .then(
          response => (
            (this.freeSubcategories = response.data),
            (this.showErrorFreeSubcategory = false),
            (this.showClientError = false),
            (this.showUnknownError = false)
          )
        )
        .catch(err => {
          if (err.response) {          
            if (err.response.data.error === "USER_ERROR") {
              this.messageErrorFreeSubcategory =
                "Ошибка запроса списка подкатегорий без расходов со стороны клиента. В запросе некорректно указан идентификатор категории.";
            } else if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorFreeSubcategory =
                "Ошибка запроса списка подкатегорий без расходов. Внутренняя ошибка сервера.";
            }
            this.showErrorFreeSubcategory = true;
          } else if (err.request) {           
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
    },
    async submitSubcategory() {
      this.errors = [];
      if (!this.subcategoryName) {
        this.errors.push("Укажите название подкатегории");
      } else if (!this.validName(this.subcategoryName)) {
        this.errors.push(
          "Название подкатегории должно быть длиной от 2 до 20 символов и начинаться с заглавной буквы. Может содержать только буквы, цифры, знак тире, пробел и запятую. Без пробелов в начале и конце слова"
        );
      }

      if (this.categories.length == 0) {
        this.errors.push(
          "Нет ни одной категории для добавления подкатегории. Необходимо добавить хотя бы одну категорию"
        );
      }

      if (!this.errors.length) {
        await AXIOS
          .post(
            "/category-child/" +
              this.selectedCategory.id,
            { name: this.subcategoryName }
          )
          .then(            
              (this.showFormError = false),
              (this.showErrorActionOnSubcategory = false),
              (this.messageSuccessSubcategory =
                "Подкатегория успешно добавлена"),
              (this.dismissCountDown = this.dismissSecs),
              (this.showClientError = false),
              (this.showUnknownError = false)            
          )
          .catch(err => {
            if (err.response) {              
              if (err.response.data.error === "USER_ERROR") {
                this.messageErrorActionOnSubcategory =
                  "Ошибка добавления подкатегории со стороны клиента. В запросе некорректно указан идентификатор категории";
              } else if (err.response.data.error === "SERVER_ERROR") {
                this.messageErrorActionOnSubcategory =
                  "Не удалось добавить подкатегорию. Внутренняя ошибка сервера";
              }
              this.showErrorActionOnSubcategory = true;
            } else if (err.request) {             
              this.messageClientError = "Ошибка сети";
              this.showClientError = true;
            } else {
              this.messageUnknownError = "Неизвестная ошибка";
              this.showUnknownError = true;
            }
          });
        this.subcategoryName = "";
        this.subcategoryList();
        this.freeSubcategoryList();
      } else {
        this.showFormError = true;
      }
    },
    async deleteSubcategory(subcategoryId) {
      await AXIOS
        .delete(
          "/category/" + subcategoryId
        )
        .then(          
            (this.showErrorActionOnSubcategory = false),
            (this.messageSuccessSubcategory = "Подкатегория успешно удалена"),
            (this.dismissCountDown = this.dismissSecs),
            (this.showClientError = false),
            (this.showUnknownError = false)          
        )
        .catch(err => {
          if (err.response) {           
            if (err.response.data.error === "USER_ERROR") {
              this.messageErrorActionOnSubcategory =
                "Ошибка удаления подкатегории со стороны клиента. В запросе некорректно указан идентификатор категории";
            } else if (err.response.data.error === "SERVER_ERROR") {
              this.messageErrorActionOnSubcategory =
                "Ошибка удаления категории на стороне сервера.";
            }
            this.showErrorActionOnSubcategory = true;
          } else if (err.request) {           
            this.messageClientError = "Ошибка сети";
            this.showClientError = true;
          } else {
            this.messageUnknownError = "Неизвестная ошибка";
            this.showUnknownError = true;
          }
        });
      this.freeSubcategoryList();
      this.subcategoryList();
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


<style scoped>
</style>