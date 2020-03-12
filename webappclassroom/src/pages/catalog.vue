<template>
  <f7-page name="catalog">
    <f7-navbar title="上课"></f7-navbar>
    <f7-list>
      <!--
      <f7-list-item
        v-for="(product) in products"
        :key="product.id"
        :title="product.title"
        :link="`/product/${product.id}/`"
      ></f7-list-item>
      -->
      <f7-list-item> 数学课程 </f7-list-item>
      <f7-list-item> 英语课程 </f7-list-item>
      <f7-list-item> 语文课程</f7-list-item>

    </f7-list>
  </f7-page>
</template>
<script>
  export default {
    data: function () {
      return {
        products: this.$f7.data.products,
      };
    }
  };
</script>
