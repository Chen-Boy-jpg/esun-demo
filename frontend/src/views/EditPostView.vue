<template>
  <div class="edit-post-container">
    <div class="edit-card">
      <header class="card-header">
        <div class="header-left">
          <button @click="router.back()" class="btn-back" title="返回">
            <span class="icon">←</span>
          </button>
          <h3>編輯貼文</h3>
        </div>
        <span class="post-id-tag">Post #{{ postId }}</span>
      </header>

      <div class="edit-form">
        <section class="form-group">
          <label class="section-label">分享你的想法</label>
          <textarea
            v-model="content"
            class="content-textarea"
            placeholder="說點什麼吧..."
          ></textarea>
        </section>

        <section class="form-group">
          <div class="section-header">
            <label class="section-label">貼文圖片</label>
            <label class="upload-btn-label">
              <span
                >📷
                {{
                  existingImageUrl || newImagePreview ? "更換圖片" : "上傳圖片"
                }}</span
              >
              <input
                type="file"
                @change="onFileChange"
                accept="image/*"
                hidden
              />
            </label>
          </div>

          <div
            v-if="newImagePreview || existingImageUrl"
            class="image-preview-container"
          >
            <p class="preview-status">
              {{
                newImagePreview
                  ? "✨ 新選取的圖片 (尚未儲存)"
                  : "🖼️ 目前使用的圖片"
              }}
            </p>
            <div class="img-wrapper">
              <img
                :src="
                  newImagePreview || `http://localhost:8080${existingImageUrl}`
                "
                class="main-preview-img"
              />
              <button
                v-if="newImagePreview"
                @click="cancelNewImage"
                class="btn-delete-preview"
                title="恢復原始圖片"
              >
                ✕
              </button>
            </div>
          </div>
        </section>
      </div>

      <footer class="card-footer">
        <button
          @click="handleUpdatePost"
          :disabled="isSubmitting"
          class="btn-save"
        >
          {{ isSubmitting ? "正在更新..." : "儲存修改" }}
        </button>
        <button @click="router.back()" class="btn-cancel">取消</button>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import { PostService } from "../services/post.service";

const route = useRoute();
const router = useRouter();
const postId = route.params.id as string;

// --- 狀態管理 ---
const content = ref(history.state.postData?.content || "");
const existingImageUrl = ref(history.state.postData?.imageUrl || "");
const selectedFile = ref<File | null>(null);
const newImagePreview = ref<string | null>(null);
const isSubmitting = ref(false);

// --- 邏輯處理 ---

const onFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const file = target.files[0];
    selectedFile.value = file;
    const reader = new FileReader();
    reader.onload = (event) => {
      newImagePreview.value = event.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const cancelNewImage = () => {
  selectedFile.value = null;
  newImagePreview.value = null;
};

/**
 * 提交並跳轉
 */
const handleUpdatePost = async () => {
  if (!content.value.trim()) {
    alert("貼文內容不能為空喔！");
    return;
  }

  isSubmitting.value = true;
  try {
    // 1呼叫 Service 執行更新
    await PostService.updateExistingPost(
      postId,
      content.value,
      selectedFile.value,
    );

    // 成功提示
    alert("貼文更新成功！");

    router.push("/");
  } catch (err: any) {
    console.error("Update failed:", err);
    const errorMsg = err.response?.data || "更新失敗，請確認修改權限";
    alert(errorMsg);
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
/* 頁面佈局與卡片設計 */
.edit-post-container {
  padding: 100px 20px 60px;
  min-height: 100vh;
  background-color: #f8fafc;
  display: flex;
  justify-content: center;
}
.edit-card {
  width: 100%;
  max-width: 640px;
  background: white;
  border-radius: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.04);
  padding: 32px;
  display: flex;
  flex-direction: column;
}

/* Header */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.btn-back {
  background: #f1f5f9;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s;
}
.btn-back:hover {
  background: #e2e8f0;
  color: #1e293b;
}
h3 {
  margin: 0;
  font-size: 1.4rem;
  color: #1e293b;
  font-weight: 800;
}
.post-id-tag {
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 700;
  background: #f8fafc;
  padding: 4px 10px;
  border-radius: 8px;
}

/* 表單 */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  text-align: left;
}
.section-label {
  font-weight: 700;
  color: #475569;
  font-size: 0.95rem;
}

/* 輸入框 */
.content-textarea {
  width: 100%;
  min-height: 180px;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  font-size: 1.1rem;
  line-height: 1.6;
  color: #334155;
  resize: vertical;
  outline: none;
  transition: 0.2s;
  background: #fcfcfc;
}
.content-textarea:focus {
  border-color: #10b981;
  background: white;
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.08);
}

/* 圖片區 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.upload-btn-label {
  background: #10b981;
  color: white;
  padding: 8px 16px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  transition: 0.2s;
}
.upload-btn-label:hover {
  background: #059669;
}

/* 預覽容器 */
.image-preview-container {
  background: #f8fafc;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
}
.preview-status {
  font-size: 0.8rem;
  color: #94a3b8;
  margin-bottom: 12px;
}
.img-wrapper {
  position: relative;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  background: #eee;
  display: flex;
  justify-content: center;
}
.main-preview-img {
  width: 100%;
  max-height: 400px;
  object-fit: contain;
  display: block;
}
.btn-delete-preview {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(239, 68, 68, 0.9);
  color: white;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  font-weight: bold;
}

/* 底部按鈕 */
.card-footer {
  margin-top: 32px;
  display: flex;
  gap: 12px;
}
.btn-save {
  flex: 2;
  background: #1e293b;
  color: white;
  border: none;
  padding: 16px;
  border-radius: 14px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: 0.2s;
}
.btn-save:hover {
  background: #0f172a;
  transform: translateY(-2px);
}
.btn-save:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}
.btn-cancel {
  flex: 1;
  background: #f1f5f9;
  color: #64748b;
  border: none;
  padding: 16px;
  border-radius: 14px;
  font-weight: 600;
  cursor: pointer;
}

@media (max-width: 480px) {
  .edit-card {
    padding: 20px;
    border-radius: 0;
  }
  .edit-post-container {
    padding: 60px 0 0;
  }
}
</style>
