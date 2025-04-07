<template>
    <div class="modal-overlay">
      <div class="modal-content">
        <h2>添加新相册</h2>
        <input
          type="text"
          v-model="albumTitle"
          placeholder="请输入相册名称"
          class="input"
        />
        <div class="modal-actions">
          <button class="btn" @click="confirm">确定</button>
          <button class="btn cancel" @click="cancel">取消</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "AddAlbumModal",
    data() {
      return {
        albumTitle: "",
      };
    },
    methods: {
      confirm() {
        // 将输入的相册名称传递给父组件
        this.$emit("confirm", this.albumTitle);
      },
      cancel() {
        this.$emit("cancel");
      },
    },
  };
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 300px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .input {
    width: 100%;
    padding: 8px;
    margin-top: 12px;
    box-sizing: border-box;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  
  .modal-actions {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .btn {
    padding: 6px 12px;
    border: none;
    border-radius: 4px;
    background-color: #3498db;
    color: #fff;
    cursor: pointer;
    margin-left: 10px;
  }
  
  .btn.cancel {
    background-color: #e74c3c;
  }
  </style>