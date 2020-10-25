<template>
  <div>
    <el-tree :data="data" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
  </div>
</template>

<script>
export default {
  data() {
    return {
      data: [
        
      ],
      defaultProps: {
        children: 'children',
        label: 'label',
      },
    }
  },
  methods: {
     getmenu(){
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'GET',
      }).then(res =>console.log(res.data))
    },
    handleNodeClick(data) {
      console.log(data)
    },
  },
  created() {
    this.getmenu();
  }
}
</script>
<style>
</style>