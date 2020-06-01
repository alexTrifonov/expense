<template>
  <div id="app">
    <sidebar-menu :menu="menu" :theme="theme">
    </sidebar-menu>   
    <router-view/>
  </div>
</template>

<script>

export default {
  name: "App",
  data() {
    return {
      menu: [
        {
          header: true,
          title: "",
          hiddenOnCollapse: true
        },
        {
          href: "/",
          title: "Добавить расход",
          icon: "fab fa-shopify"
        },
        {
          href: "/category-action",
          title: "Управление категориями",
          icon: "fa fa-shopping-basket"
        },
        {
          href: "/subcategory-action",
          title: "Управление подкатегориями",
          icon: "fa fa-shopping-bag"
        },
        {
          href: "/expense-table",
          title: "Таблица расходов",
          icon: "fa fa-table"
        },
        {
          href: "/bar",
          title: "Диаграмма расходов",
          icon: "fas fa-chart-bar"
        },
      ],
      theme: 'white-theme'
    };
  },
  components: {
    
  }
};

</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
