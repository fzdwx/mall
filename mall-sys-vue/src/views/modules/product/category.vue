<template>
  <div>
    <el-tree
      :data="menus"
      :props="defaultProps"
      :expand-on-click-node="false"
      node-key="catId"
      show-checkbox="true"
      default-expanded-keys="expandKey"
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
      menus: [], //* 菜单信息
      expandKey: [], //* 默认展开菜单，
      defaultProps: {
        children: 'children',
        label: 'name',
      },
    }
  },
  methods: {
    //* 添加一个节点
    append(data) {},
    //* 删除一个节点
    remove(node, data) {
      let idList = [data.catId]
      this.$confirm(`是否删除【${data.name}】菜单, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        //* 确定删除
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(idList, false),
        })
          .then(({ data }) => {
            //* 调用获取数据按钮
            this.getmenu()
            //* 设置展开的菜单
            this.expandKey=[node.parent.data.catId]
            this.$message({
              type: 'success',
              message: '删除成功!',
            })
          })
           //* 取消删除
          .catch(() => {})
      })
    },
    //
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