<template>
  <div>
    <b-alert
      :show="showAlert"
      variant="warning"
      dismissible
      fade
      @dismissed="showAlert=false"
    >
      <b>Пожалуйста исправьте указанные ошибки:</b>
      <ul>
        <li v-for="error in errors" v-bind:key="error">{{ error }}</li>
      </ul>
    </b-alert>
    
  </div>
</template>

<script>
export default {
  name: "Alert",
  props: {
      errors: Array,
      showAlert: Boolean
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
