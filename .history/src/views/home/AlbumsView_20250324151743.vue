<template>
  <div class="table-container">
    <div class="table-grid table-header">
      <div>ID</div>
      <div>姓名</div>
      <div>年龄</div>
      <div>操作</div>
    </div>

    <div
      class="table-grid table-row"
      v-for="(user, index) in users"
      :key="index"
    >
      <div>{{ user.id }}</div>
      <div>{{ user.name }}</div>
      <div>{{ user.age }}</div>
      <div><button @click="deleteUser(index)">删除</button></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      users: [
        { id: 1, name: "张三", age: 25 },
        { id: 2, name: "李四", age: 30 },
        { id: 3, name: "王五", age: 28 },
      ],
    };
  },
  methods: {
    deleteUser(index) {
      this.users.splice(index, 1);
    },
  },
};
</script>

<style scoped>
.table-container {
  max-width: 600px;
  margin: auto;
}

.table-grid {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr;
  gap: 10px;
  padding: 10px;
  text-align: center;
}

.table-header {
  background: #7b61ff;
  color: white;
  font-weight: bold;
}

.table-row {
  background: white;
  border-bottom: 1px solid #ddd;
}

button {
  padding: 5px 10px;
  background: red;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
