<template>
  <div>
    <el-tree
      :data="menus"
      :props="defaultProps"
      :expand-on-click-node="false" 
      show-checkbox="true"
      node-key="catId"
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <!-- 显示标题 -->
        <span>{{ node.label }}</span>
        <!-- 删除和添加按钮 -->
        <span>
          <el-button v-if="node.level <= 2" type="text" size="mini" @click="() => append(data)">添加</el-button>
          <el-button
            v-if="node.childNodes.length==0"
            type="text"
            size="mini"
            @click="() => remove(node, data)"
          >删除</el-button>
        </span>
      </span>
    </el-tree>
  </div>
</template>

<script>
export default {
  data() {
    return {
      menus: [],
      defaultProps: {
        children: 'children',
        label: 'name',
      },
    }
  },
  methods: {
    // 添加一个节点
    append(data) {
     
    },
    // 删除一个节点
    remove(node, data) {
    
    },
    // 获取分类信息
    getmenu() {
      // 发送请求
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'GET',
        // 赋值
      }).then((res) => (this.menus = res.data.data))
    },
  },
  created() {
    this.getmenu()
  },
}
</script>
<style>
</style>