<template>
  <div>
    <!-- 树形 -->
    <el-tree
      :data="menus"
      :props="defaultProps"
      :expand-on-click-node="false"
      node-key="catId"
      show-checkbox
      :default-expanded-keys="expandKey"
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <!-- 显示标题 -->
        <span>{{ node.label }}</span>
        <!-- 删除和添加按钮 -->
        <span>
          <el-button
            v-if="node.level <= 2"
            type="text"
            size="mini"
            @click="() => openDialog(data)"
          >添加</el-button>
          <el-button
            v-if="node.childNodes.length==0"
            type="text"
            size="mini"
            @click="() => remove(node, data)"
          >删除</el-button>
        </span>
      </span>
    </el-tree>
    <!--对话框 -->
    <el-dialog title="添加分类" :visible.sync="dialogFormVisible">
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addCategory(category)">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      category: { name: '', patentCid: 0, catLevel: 0, showStatus: 1, sort: 0 },
      dialogFormVisible: false, //* dialog 默认关闭
      menus: [], //* 菜单信息
      expandKey: [], //* 默认展开菜单，
      defaultProps: {
        //* tree 组件默认属性
        children: 'children',
        label: 'name',
      },
    }
  },
  methods: {
    //* 添加分类
    addCategory(data) {
      this.$http({
        url: this.$http.adornUrl('/product/category/save'),
        method: 'POST',
        data: this.$http.adornData(this.category, false),
      }).then(() => {
        //* 保存成功，关闭对话框
        this.dialogFormVisible = false
        //* 获取数据
        this.getmenu()
        this.expandKey = [this.category.catId,this.category.parentCid]
        this.$message({
          type: 'success',
          message: '添加成功!',
        })
      })
    },
    //* 打开对话框
    openDialog(data) {
      this.dialogFormVisible = true //* 打开对话框
      //* 初始化分类对象
      this.category.parentCid = data.catId
      this.category.catLevel = data.catLevel * 1 + 1
    },
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
            this.expandKey = [node.parent.data.catId,node.parent.data.patentCid]
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