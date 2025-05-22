<template>
  <el-dialog v-model="visible" :close-on-click-modal="false" fullscreen>
    <template #header>
      <div
        style="
          display: flex;
          justify-content: space-between;
          align-items: center;
        "
      >
        <span>编辑照片</span>
        <div>
          <el-button type="primary" :loading="saving" @click="handleSave">
            保存修改
          </el-button>
        </div>
      </div>
    </template>

    <div class="editor-container">
      <ImageEditorComponent
        ref="editor"
        :file-url="fileUrl"
        :show-save-button="false"
      />
    </div>
  </el-dialog>
</template>

<script>
import ImageEditorComponent from "../views/ImageEditor.vue";

export default {
  components: {
    ImageEditorComponent,
  },
  props: {
    fileUrl: {
      type: String,
      required: true,
    },
    value: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      saving: false,
    };
  },
  computed: {
    visible: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit("input", val);
      },
    },
  },
  methods: {
    async handleSave() {
      this.saving = true;
      try {
        const { blob } = await this.$refs.editor.getEditorImageData();
        console.log(blob);
        this.$emit("save", blob);
      } finally {
        this.saving = false;
      }
    },
    handleCancel() {
      this.visible = false;
    },
  },
};
</script>

<style scoped>
.editor-container {
  height: 80vh;
  position: relative;
}
</style>
