<template>
  <div>
    <div class="container">
      <div class="page-header">
        <h3>Параметры для выборки расходов</h3>
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
      <b-alert
      :show="dismissCountDown"
      dismissible
      fade
      variant="success"
      @dismiss-count-down="countDownChanged"
    >Расход успешно удален</b-alert>
      <div class="row my-4">
        <div class="col-lg-6">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-select-category">Категория расхода</span>
            <select
              class="form-control"
              v-model="selectedCategory"
              id="category-select"
              aria-describedby="label-select-category"
            >
              <option
                v-for="category in categories"
                v-bind:value="category"
                v-bind:key="category.id"
              >{{category.name}}</option>
            </select>
          </div>
        </div>
      </div>
      <div class="row my-4">
        <div class="col-lg-6">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-select-subcategory">Подкатегория расхода</span>
            <select
              class="form-control"
              v-model="selectedSubcategory"
              id="subcategory-select"
              aria-describedby="label-select-subcategory"
            >
              <option
                v-for="subcategory in subcategories"
                v-bind:value="subcategory"
                v-bind:key="subcategory.id"
              >{{subcategory.name}}</option>
            </select>
          </div>
        </div>
      </div>

      <div class="row my-4">
        <div class="col-lg-5">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-unit_price">Полная стоимость расхода от</span>
            <input
              class="form-control"
              v-model.lazy="totalPriceFrom"
              v-money="money"
              aria-describedby="label-unit-price"
            />
          </div>
        </div>
        <div class="col-lg-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-unit_price">до:</span>
            <input
              class="form-control"
              v-model.lazy="totalPriceTo"
              v-money="money"
              aria-describedby="label-unit-price"
            />
          </div>
        </div>
      </div>
      <div class="row my-4">
        <div class="col-lg-6">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-note">Значение примечания к расходу</span>
            <input type="text" class="form-control" v-model="note" aria-describedby="label-note" />
          </div>
        </div>
      </div>
      <div class="row my-4">
        <div class="col-lg-4">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-datepicker-start">В период с</span>
            <datepicker              
              v-model="startDate"
              :monday-first="true"
              :language="ru"
              bootstrap-styling="true"
              id="datepickerStart"
              aria-describedby="label-datepicker-start"
            ></datepicker>
          </div>
        </div>
        <div class="col-lg-4">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-datepicker-finish">по</span>
            <datepicker              
              v-model="finishDate"
              :monday-first="true"
              :language="ru"
              bootstrap-styling="true"
              id="datepickerFinish"
              aria-describedby="label-datepicker-finish"
            ></datepicker>
          </div>
        </div>
      </div>

      <div class="row my-4">
        <div class="col-lg-4">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-order-by">Сортировать по</span>
            <div class="btn-group" data-toggle="buttons" aria-describedby="label-order-by">
              <label class="btn btn-secondary">
                <input
                  type="radio"
                  name="order-options"
                  id="dateRadio"
                  value="localDate"
                  v-model="orderBy"
                  autocomplete="off"
                  checked
                /> Дате
              </label>
              <label class="btn btn-secondary">
                <input
                  type="radio"
                  name="order-options"
                  id="dateRadio"
                  value="totalPrice"
                  v-model="orderBy"
                  autocomplete="off"
                /> Цене
              </label>
            </div>
          </div>
        </div>
      </div>

      <div class="row justify-content-center my-4">
        <button class="btn btn-primary" v-on:click="getExpense">Показать расходы</button>
      </div>

      <vue-good-table
        :columns="columns"
        :rows="rows"
        styleClass="vgt-table condensed bordered"
        :pagination-options="{
          enabled: true,
          mode: 'pages',
          perPageDropdown: [10, 15, 20]
        }"
      >
        <template slot="table-row" slot-scope="props">
          <span v-if="props.column.field == 'Action'">                    
            <router-link
              :to="'/expense-edit/' + props.row.id"
              class="badge badge-warning mr-2"              
            ><i class="fas fa-edit"></i></router-link>
            <button              
              class="badge badge-danger mr-2"
              v-on:click="deleteRow(props.row.id)"
              mr-3
            ><i class="fas fa-trash-alt"></i></button>
          </span>
          <span v-else>{{props.formattedRow[props.column.field]}}</span>
        </template>
      </vue-good-table>
    </div>
  </div>
</template>

<script>
import Datepicker from "vuejs-datepicker";
import { en, ru } from "vuejs-datepicker/dist/locale";
import "vue-good-table/dist/vue-good-table.css";
import { VueGoodTable } from "vue-good-table";
import { VMoney } from "v-money";
import AlertErrorList from "@/components/AlertErrorList";
import AlertError from "@/components/AlertError";
import {AXIOS} from './http-common'

let formatter = new Intl.NumberFormat("ru", {minimumFractionDigits: 2});

export default {
  name: "ExpenseTable",
  components: {
    "alert-error-list": AlertErrorList,    
    "alert-error": AlertError,
    Datepicker,
    VueGoodTable
  },
  data() {
    return {
      categories: [],
      subcategories: [],
      subcategory_name: "",
      selectedCategory: { id: null, name: "<Без категории>" },
      selectedSubcategory: { id: null, name: "<Без подкатегории>" },
      startDate: new Date(),
      finishDate: new Date(),
      en: en,
      ru: ru,
      totalPriceFrom: 0.0,
      totalPriceTo: 0.0,
      note: null,
      orderBy: "localDate",
      columns: [
        {
          label: "Дата",
          field: "localDate"
        },
        {
          label: "Категория",
          field: "category"
        },
        {
          label: "Количество",
          field: "count",
          type: "number"
        },
        {
          label: "Цена",
          field: "totalPrice",
          type: "number",
          formatFn: function(val) {
            return formatter.format(val);
          }
        },
        {
          label: "Примечание",
          field: "note"
        },
        {
          label: "Action",
          field: "Action",
          tdClass: 'text-center'
        }
      ],
      rows: [],
      money: {
        decimal: ".",
        thousands: " ",
        precision: 2
      },
      errors: [],
      showFormError: false,
      showServerErrorExpense: false,
      messageServerErrorExpense: "Ошибка с действиями над расходами на стороне сервера",
      showServerErrorCategory: false,
      messageServerErrorCategory: "Ошибка получения категорий на стороне сервера",
      showServerErrorSubcategory: false,
      messageServerErrorSubcategory: "Ошибка получения подкатегорий на стороне сервера",
      dismissSecs: 1,
      dismissCountDown: 0,
      messageClientError: "",
      showClientError: false,
      messageUnknownError: "",
      showUnknownError: false
    };
  },
  mounted() {
    AXIOS
      .get("/category")
      .then(
        response => (
          (this.categories = response.data),
          this.categories.unshift({ id: null, name: "<Без категории>" }),
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
  },
  watch: {
    selectedCategory: function(newSelectedCategory) {
      if (newSelectedCategory.id == null) {
        this.subcategories = [{ id: null, name: "<Без подкатегории>" }];
        this.selectedSubcategory = { id: null, name: "<Без подкатегории>" };
      } else {
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
              (this.selectedSubcategory = {
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
                this.messageServerErrorSubcategory =
                  "Ошибка запроса списка подкатегорий. Внутренняя ошибка сервера.";
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
    }
  },
  directives: {
    money: VMoney
  },
  methods: {
    getExpense() {
      this.rows = [];
      this.errors = [];
      let prices = null;

      if (this.totalPriceFrom < 0 || this.totalPriceTo < 0) {
        this.errors.push(
          "Значения границ диапазона цен не могут быть отрицательными."
        );
      } else if (this.totalPriceFrom > this.totalPriceTo) {
        this.errors.push(
          "Значение начальной границы диапазона цен не может быть больше значения конечной границы диапазона цен."
        );
      } else if (this.totalPriceFrom == 0 && this.totalPriceTo == 0) {
        prices = null;
      } else {
        prices = this.totalPriceFrom + "," + this.totalPriceTo;
      }

      if (this.startDate > this.finishDate) {
        this.errors.push(
          "Значение начальной даты не может быть больше значения конечной даты"
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
        let dates =
          this.onlyDateISO(this.startDate) +
          "," +
          this.onlyDateISO(this.finishDate);
        let selectedCategoryId =
          this.selectedSubcategory.id != null
            ? this.selectedSubcategory.id
            : this.selectedCategory.id;
        AXIOS
          .get("/expense-certain", {
            params: {
              dates: dates,
              prices: prices,
              categoryId: selectedCategoryId,
              note: this.note,
              orderBy: this.orderBy
            }
          })
          .then(
            response => (
              (this.rows = this.fillRows(response.data)),
              (this.note = null),
              (this.totalPriceFrom = null),
              (this.totalPriceTo = null),
              (this.showServerErrorExpense = false),
              (this.showClientError = false),
              (this.showUnknownError = false),
              (this.showFormError = false)
            )
          )
          .catch(err => {
            if (err.response) {                       
              if (err.response.data.error === "USER_ERROR") {
                this.messageServerErrorExpense =
                  "Ошибка запроса расходов со стороны клиента. В запросе некорректно указаны один или несколько параметров: цены, даты, идентификатор категории";                
              } else if (err.response.data.error === "SERVER_ERROR") {
                this.messageServerErrorExpense = "Ошибка запроса расходов со стороны клиента. Внутренняя ошибка сервера.";
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
    },
    fillRows: function(data) {
      let rows = [];
      
      data.forEach(function(item) {        
        let row = {
          id: item.id,
          localDate: item.localDate,
          category: item.category.name,
          count: item.count,
          totalPrice: item.totalPrice,
          note: item.note
        };
        rows.push(row);
      });
      return rows;
    },
    deleteRow: async function(rowId) {     
      await AXIOS
        .delete("/expense/" + rowId)
        .then(         
            (this.dismissCountDown = this.dismissSecs),
            (this.showServerErrorExpense = false),
            (this.showClientError = false),
            (this.showUnknownError = false)          
        )
        .catch(err => {
          if (err.response) {                 
            if (err.response.data.error === "USER_ERROR") {
              this.messageServerErrorExpense =
                "Ошибка удаления расхода со стороны клиента. В запросе некорректно указан идентификатор расхода";              
            } else if (err.response.data.error === "SERVER_ERROR") {
              this.messageServerErrorExpense =
                "Ошибка удаления расхода на стороне сервера.";
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
      this.rows.splice(
        this.rows.findIndex(row => row.id == rowId),
        1
      );
    },
    countDownChanged(dismissCountDown) {
        this.dismissCountDown = dismissCountDown
      }
  }
};
</script>


<style scoped>
</style>