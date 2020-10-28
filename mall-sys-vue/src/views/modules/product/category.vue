<!--产品分类维护-->
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
      @node-drop="handleDrop"
      draggable
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <!-- 显示标题 -->
        <span>{{ node.label }}</span>
        <!-- 删除和添加按钮 -->
        <span>
          <el-button v-if="node.level <= 2" type="text" size="mini" @click="() => add(data)">添加</el-button>
          <el-button
            v-if="node.childNodes.length==0"
            type="text"
            size="mini"
            @click="() => remove(node, data)"
          >删除</el-button>
          <el-button type="text" size="mini" @click="() => edit( data)">修改</el-button>
        </span>
      </span>
    </el-tree>

    <!--对话框 -->
    <el-dialog title="编辑分类" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类图标">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCategory(category)">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import http from "../../../utils/httpRequest";

export default {
  created() {
    this.getmenu()
  },
  data() {
    return {
      maxLevel: 1, //* 拖拽节点时，计算的最大节点等级
      updateNodes: [],
      //* 分类对象
      category: {
        name: '',
        patentCid: 0,
        catLevel: 0,
        showStatus: 1,
        sort: 0,
        catId: 0,
        icon: '', //* 图标
        productUnit: '', //* 计量单位
      },
      dialogType: '', //* 对话框类型 edit，add
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
    // 拖拽成功
    handleDrop(draggingNode, dropNode, dropType) {
      console.log('成功')
      // 1.当前节点的最新的父节点id
      let curNodeParentId = 0;
      let siblings = null; // 兄弟节点
      if (dropType === "before" || dropType === "after") { // 同层级拖动
        curNodeParentId = dropNode.parent.data.catId === undefined ? 0 : dropNode.parent.data.catId;
        siblings = dropNode.parent.childNodes
      } else { // inner
        curNodeParentId = dropNode.data.catId;
        siblings = dropNode.childNodes;
      }
      // 2.当前拖拽节点的最新顺序
      for (let i = 0; i < siblings.length; i++) {
        if (siblings[i].data.catId === draggingNode.data.catId) {
          let currCatLevel = draggingNode.level;
          // 如果遍历的是正在拖拽的节点
          if (siblings[i].level !== draggingNode.level) {
            // 修改当前节点的层级
            currCatLevel = siblings[i].level;
            // 修改子节点的层级
            this.updateChNodeLevel(siblings[i])
            // if (dropType === "before" || dropType === "after") {
            //   currCatLevel = dropNode.level;
            // } else {// inner
            //   currCatLevel = dropNode.level + 1;
            // }
          }
          this.updateNodes.push({catId: siblings[i].data.catId, sort: i, patentCid: curNodeParentId});
        } else {
          this.updateNodes.push({catId: siblings[i].data.catId, sort: i})
        }
      }
      // 3.当前拖拽节点的最新层级
      this.$http({
        url: this.$http.adornUrl('/product/category/update/drag'),
        method: 'post',
        data: this.$http.adornData(this.updateNodes, false)
      }).then(({data}) => {
        this.$message({
          message: "菜单顺序修改成功",
          type:"success",
        });
      });
    },
    // 修改子节点的等级
    updateChNodeLevel(chNode) {
      for (let i = 0; i < chNode.childNodes.length; i++) {
        let currNode = chNode.childNodes[i].data;
        this.updateNodes.push({catId: currNode.catId, catLevel: chNode.childNodes[i].level})
        // 递归
        this.countNodeLevel(chNode.childNodes[i])
      }
    },
    // 是否允许拖拽
    allowDrop(draggingNode, dropNode, type) {
      //1.能被拖动的节点和其所在父节点的总层数不能大于3
      this.maxLevel = 0; // 重新复制
      let level = this.countNodeLevel(draggingNode.data)
      let deep = level - draggingNode.data.catLevel + 1
      // draggingNode.data.catLevel 深度
      if (type === "inner") {
        return (deep + dropNode.level) <= 3;
      } else {
        return (deep + dropNode.parent.level) <= 3;
      }
    },
    // 计算节点的等级
    countNodeLevel(node) {
      // 如果有子节点
      if (node.children != null && node.children.length > 0) {
        for (let i = 0; i < node.children.length; i++) {
          if (node.children[1].catLevel > this.maxLevel) {
            this.maxLevel = node.children[1].catLevel
          }
          this.countNodeLevel(node.children[i])
        }
      }
      return this.maxLevel;
    },
    //* 提交分类
    submitCategory(data) {
      //* 添加
      if (this.dialogType === 'add') {
        this.$http({
          url: this.$http.adornUrl('/product/category/save'),
          method: 'POST',
          data: this.$http.adornData(this.category, false),
        }).then(() => {
          //* 保存成功，关闭对话框
          this.dialogFormVisible = false
          //* 获取数据
          this.getmenu()
          this.expandKey = [this.category.catId, this.category.parentCid]
          this.$message({
            type: 'success',
            message: '编辑成功!',
          })
        })
      } //* 编辑
      else {
        //* 解构对象
        let {catId, name, icon, productUnit} = this.category
        let editData = {catId, name, icon, productUnit}
        this.$http({
          url: this.$http.adornUrl('/product/category/update'),
          method: 'POST',
          data: this.$http.adornData(editData, false),
        }).then(() => {
          //* 保存成功，关闭对话框
          this.dialogFormVisible = false
          //* 刷新数据
          this.getmenu()
          //* 设置打开标签
          this.expandKey = [this.category.catId, this.category.parentCid]
          this.$message({
            type: 'success',
            message: '编辑成功!',
          })
        })
      }
    },
    //* 修改分类
    edit(data) {
      this.dialogType = 'edit'
      this.dialogFormVisible = true
      //* 发送请求获取最新数据
      this.$http({
        url: this.$http.adornUrl('/product/category/info/' + data.catId),
        method: 'get',
      }).then(({data}) => {
        console.log('最新的数据', data.data)
        //* 回显数据
        this.category.name = data.data.name
        this.category.catId = data.data.catId
        this.category.icon = data.data.icon
        this.category.productUnit = data.data.productUnit
      })
    },
    //* 打开对话框
    add(data) {
      this.dialogType = 'add'
      this.dialogFormVisible = true //* 打开对话框
      //* 初始化分类对象
      this.category.parentCid = data.catId
      this.category.catLevel = data.catLevel * 1 + 1
      this.category.name = ''
      this.category.catId = 0
      this.category.showStatus = 1
      this.category.sort = 0
      this.category.icon = ''
      this.category.productUnit = ''
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
          .then(({data}) => {
            //* 调用获取数据按钮
            this.getmenu()
            //* 设置展开的菜单
            this.expandKey = [
              node.parent.data.catId,
              node.parent.data.patentCid,
            ]
            this.$message({
              type: 'success',
              message: '删除成功!',
            })
          })
          //* 取消删除
          .catch(() => {
          })
      })
    },
  }
}
</script>
<style>
</style>
