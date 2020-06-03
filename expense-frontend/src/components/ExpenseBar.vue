<template>
  <div>
    <div class="container">
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
      <apexchart type="bar" height="500" :options="chartOptions" :series="series" class="chart"></apexchart>
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
            <span class="input-group-text mr-2" id="label-order-by">Расходы по</span>
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                name="order-options"
                id="categoryRadio"
                value="categoryBar"
                v-model="barType"
                checked
              />
              <label class="form-check-label" for="categoryRadio">Категориям</label>
            </div>
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                name="order-options"
                id="subcategoryRadio"
                value="subcategoryBar"
                v-model="barType"
              />
              <label class="form-check-label" for="subcategoryRadio">Подкатегориям</label>
            </div>
          </div>
        </div>
      </div>
      <div class="row my-4">
        <div class="col-lg-4">
          <div class="input-group-prepend">
            <span class="input-group-text" id="label-datepicker-start">За период с</span>
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
      <div class="row justify-content-center my-4">
        <button class="btn btn-primary" v-on:click="updateChart">Сформировать диаграмму</button>
      </div>
    </div>
  </div>
</template>

<script>
import VueApexCharts from "vue-apexcharts";
import Datepicker from "vuejs-datepicker";
import { en, ru } from "vuejs-datepicker/dist/locale";
import AlertErrorList from "@/components/AlertErrorList";
import AlertError from "@/components/AlertError";
import {AXIOS} from './http-common'

let formatter = new Intl.NumberFormat("ru", {minimumFractionDigits: 2});

export default {
  components: {
    "alert-error-list": AlertErrorList,
    "alert-error": AlertError,
    apexchart: VueApexCharts,
    datepicker: Datepicker
  },
  data() {
    return {
      categories: [],
      selectedCategory: {},
      barType: "categoryBar",
      startDate: new Date(),
      finishDate: new Date(),
      en: en,
      ru: ru,
      series: [
        {
          name: "Расходы",
          data: []
        }
      ],
      chartOptions: {
        chart: {
          height: 350,
          type: "bar"
        },
        plotOptions: {
          bar: {
            dataLabels: {
              position: "top" // top, center, bottom
            }
          }
        },
        dataLabels: {
          enabled: true,
          formatter: function(val) {
            return formatter.format(val) + " руб.";
          },
          offsetY: -20,
          style: {
            fontSize: "12px",
            colors: ["#304758"]
          }
        },

        xaxis: {
          categories: [],
          position: "top",
          axisBorder: {
            show: false
          },
          axisTicks: {
            show: false
          },
          crosshairs: {
            fill: {
              type: "gradient",
              gradient: {
                colorFrom: "#D8E3F0",
                colorTo: "#BED1E6",
                stops: [0, 100],
                opacityFrom: 0.4,
                opacityTo: 0.5
              }
            }
          },
          tooltip: {
            enabled: true
          }
        },
        yaxis: {
          axisBorder: {
            show: true
          },
          axisTicks: {
            show: true
          },
          labels: {
            show: true,
            formatter: function(val) {
              return formatter.format(val) + " руб.";
            }
          }
        },
        title: {
          text: "Расходы за указанный период",
          floating: true,
          offsetY: 480,
          align: "center",
          style: {
            color: "#444"
          }
        }
      },
      errors: [],
      showFormError: false,
      showServerErrorExpense: false,
      messageServerErrorExpense: "Ошибка получения расходов на стороне сервера",
      showServerErrorCategory: false,
      messageServerErrorCategory: "Ошибка получения категорий на стороне сервера",
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
          (this.selectedCategory = this.categories[0]),
          (this.showServerErrorCategory = false),
          (this.showClientError = false),
          (this.showUnknownError = false)
        )
      )
      .catch(err => {
        if (err.response) {                 
          if (err.response.data.error === "SERVER_ERROR") {
            this.messageServerErrorCategory =
              "Ошибка получения списка категорий на стороне базы данных";            
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
  methods: {
    async updateChart() {
      this.series = [
        {
          data: []
        }
      ];
      this.chartOptions = {
        xaxis: {
          categories: []
        }
      };
      this.errors = [];
      if (this.startDate > this.finishDate) {
        this.errors.push(
          "Значение начальной даты не может быть больше значения конечной даты"
        );
      }
      if (!this.errors.length) {
        let expenseByCategory;
        let categoryId = null;
        if (this.barType === "subcategoryBar") {
          categoryId = this.selectedCategory.id;
        }
        await AXIOS
          .get("/expense-bar-data", {
            params: {
              categoryId: categoryId,
              dateFrom: this.onlyDateISO(this.startDate),
              dateTo: this.onlyDateISO(this.finishDate)
            }
          })
          .then(
            response => (
              (expenseByCategory = response.data),
              (this.showFormError = false),
              (this.showServerErrorExpense = false),
              (this.showClientError = false),
              (this.showUnknownError = false)
            )
          )
          .catch(err => {
            if (err.response) {                       
              if (err.response.data.error === "USER_ERROR") {
                this.messageServerErrorExpense =
                  "Ошибка запроса данных по расходам со стороны клиента. В запросе некорректно указаны один или несколько параметров: даты, идентификатор категории";                
              } else if (err.response.data.error === "SERVER_ERROR") {
                this.messageServerErrorExpense = "Внутренняя ошибка сервера.";                
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
        let newData = [];
        let newCategories = [];
        for (let key in expenseByCategory) {
          newCategories.push(key);
          newData.push(expenseByCategory[key]);
        }
        this.series = [
          {
            data: newData
          }
        ];
        this.chartOptions = {
          xaxis: {
            categories: newCategories
          }
        };
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

<style>
.apexchart {
  background-color: mintcream;
}
.chart {
  background-color: lavender;
}
</style>