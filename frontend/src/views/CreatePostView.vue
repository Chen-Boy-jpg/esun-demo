<script setup lang="ts">
import { ref, computed, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useMutation, useQueryClient } from "@tanstack/vue-query";
import { useForm } from "vee-validate";
import { toTypedSchema } from "@vee-validate/zod";
import * as zod from "zod";
import { PostService } from "../services/post.service";
import { AuthService } from "../services/auth.service";

const router = useRouter();
const queryClient = useQueryClient();

//  狀態管理：使用者資訊、圖片預覽與檔案
const currentUserName = computed(() => AuthService.getUserName() || "使用者");
const imagePreview = ref<string | null>(null);
const selectedFile = ref<File | null>(null);

//  表單驗證 Schema
const schema = toTypedSchema(
  zod.object({
    content: zod
      .string()
      .min(1, "內容不能為空")
      .max(500, "內容太長了 (上限 500 字)"),
  }),
);

const { errors, defineField, handleSubmit } = useForm({
  validationSchema: schema,
  initialValues: { content: "" },
});

const [content, contentProps] = defineField("content");

//  處理圖片選取與預覽
const onFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const file = target.files[0];

    // 限制檔案大小 (5MB)
    if (file.size > 5 * 1024 * 1024) {
      alert("圖片大小不能超過 5MB");
      return;
    }

    selectedFile.value = file;
    // 清除舊的預覽 URL 避免記憶體洩漏
    if (imagePreview.value) URL.createObjectURL(new Blob()).slice(0, 0); // 這裡只是示意
    imagePreview.value = URL.createObjectURL(file);
  }
};

// 移除圖片
const removeImage = () => {
  selectedFile.value = null;
  if (imagePreview.value) URL.revokeObjectURL(imagePreview.value);
  imagePreview.value = null;
};

//  發布貼文的 Mutation
const { mutate: createPost, isPending } = useMutation({
  mutationFn: (payload: { content: string; image?: File }) =>
    PostService.createPost(payload),
  onSuccess: () => {
    setTimeout(() => {
      window.location.href = "/";
    }, 500);
  },
  onError: (error: any) => {
    alert(error.response?.data || error.message || "發布失敗，請稍後再試");
  },
});

const onSubmit = handleSubmit((values) => {
  createPost({
    content: values.content,
    image: selectedFile.value || undefined,
  });
});

//組件銷毀時釋放記憶體中的圖片連結
onUnmounted(() => {
  if (imagePreview.value) URL.revokeObjectURL(imagePreview.value);
});
</script>

<template>
  <div class="pc-app-bg">
    <div class="pc-layout-container">
      <aside class="side-panel left-side">
        <button class="glass-btn" @click="router.back()">
          <span class="icon">←</span>
          <span>返回廣場</span>
        </button>
      </aside>

      <main class="main-editor-area">
        <div class="editor-card">
          <header class="editor-header">
            <div class="user-meta">
              <div class="avatar-box">
                {{ currentUserName.charAt(0).toUpperCase() }}
              </div>
              <div class="user-info">
                <span class="username">@{{ currentUserName }}</span>
                <span class="status">正在撰寫動態...</span>
              </div>
            </div>
          </header>

          <div class="editor-body">
            <form @submit.prevent="onSubmit">
              <textarea
                v-model="content"
                v-bind="contentProps"
                placeholder="分享你的想法..."
                class="rich-textarea"
                rows="10"
                autofocus
                :disabled="isPending"
              ></textarea>

              <transition name="fade">
                <div v-if="imagePreview" class="image-preview-wrapper">
                  <img :src="imagePreview" alt="Preview" class="preview-img" />
                  <button type="button" @click="removeImage" class="remove-btn">
                    ✕
                  </button>
                </div>
              </transition>

              <div v-if="errors.content" class="error-text">
                {{ errors.content }}
              </div>
            </form>
          </div>

          <footer class="editor-footer">
            <div class="footer-left">
              <label
                for="img-upload"
                class="upload-label"
                :class="{ disabled: isPending }"
              >
                <span class="icon">🖼️</span>
                <span>{{ selectedFile ? "已選擇圖片" : "加入圖片" }}</span>
              </label>
              <input
                id="img-upload"
                type="file"
                accept="image/*"
                @change="onFileChange"
                hidden
                :disabled="isPending"
              />
            </div>

            <div class="footer-right">
              <span
                class="counter"
                :class="{ warning: (content?.length || 0) > 450 }"
              >
                {{ content?.length || 0 }} / 500
              </span>
              <div class="action-btns">
                <button
                  class="btn-cancel"
                  @click="router.back()"
                  :disabled="isPending"
                >
                  取消
                </button>
                <button
                  class="btn-publish"
                  @click="onSubmit"
                  :disabled="isPending || !content"
                >
                  {{ isPending ? "發布中..." : "發布貼文" }}
                </button>
              </div>
            </div>
          </footer>
        </div>
      </main>

      <aside class="side-panel right-side">
        <div class="tips-card">
          <h4>💡 貼心小提醒</h4>
          <ul>
            <li>圖片檔案建議不超過 5MB。</li>
            <li>文字內容不可為空，上限 500 字。</li>
            <li>發布後將會顯示在社群廣場。</li>
          </ul>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
/* 全域版面設定 */
.pc-app-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 60px 20px;
  display: flex;
  justify-content: center;
}

.pc-layout-container {
  max-width: 1200px;
  width: 100%;
  display: grid;
  grid-template-columns: 200px 1fr 280px;
  gap: 40px;
}

/* 側邊欄按鈕 - 毛玻璃效果 */
.glass-btn {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 12px 24px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: 0.3s;
}

.glass-btn:hover {
  background: white;
  transform: translateX(-5px);
  color: #10b981;
}

/* 編輯器卡片 */
.editor-card {
  background: white;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #f1f5f9;
}

.editor-header {
  padding: 30px 40px 10px;
}

.user-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar-box {
  width: 48px;
  height: 48px;
  background: #10b981;
  color: white;
  border-radius: 14px;
  display: grid;
  place-items: center;
  font-weight: 800;
  font-size: 1.2rem;
}

.username {
  display: block;
  font-weight: 700;
  color: #1e293b;
  text-align: left;
}
.status {
  font-size: 0.8rem;
  color: #94a3b8;
}

.editor-body {
  padding: 20px 40px;
}

.rich-textarea {
  width: 100%;
  border: none;
  outline: none;
  font-size: 1.2rem;
  line-height: 1.6;
  color: #334155;
  resize: vertical;
  background: transparent;
  min-height: 200px;
}

/* 圖片預覽 */
.image-preview-wrapper {
  margin-top: 20px;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f1f5f9;
  width: fit-content;
}

.preview-img {
  max-width: 100%;
  max-height: 400px;
  display: block;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
}

.editor-footer {
  padding: 24px 40px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-label {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: 0.2s;
}

.upload-label:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.counter {
  font-size: 0.85rem;
  color: #94a3b8;
  font-weight: 600;
}
.counter.warning {
  color: #f59e0b;
}

.action-btns {
  display: flex;
  gap: 12px;
}

.btn-cancel {
  background: none;
  border: none;
  color: #94a3b8;
  font-weight: 600;
  cursor: pointer;
}

.btn-publish {
  background: #10b981;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 10px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s;
}

.btn-publish:hover:not(:disabled) {
  background: #059669;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
}

.btn-publish:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

/* 提示卡片 */
.tips-card {
  background: white;
  padding: 24px;
  border-radius: 20px;
  text-align: left;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);
}

.tips-card h4 {
  margin-top: 0;
  color: #1e293b;
}
.tips-card ul {
  padding-left: 20px;
  color: #64748b;
  font-size: 0.9rem;
}
.tips-card li {
  margin-bottom: 10px;
  line-height: 1.4;
}

/* 響應式優化 */
@media (max-width: 1024px) {
  .pc-layout-container {
    grid-template-columns: 1fr;
  }
  .side-panel {
    display: none;
  }
}

.error-text {
  color: #ef4444;
  font-size: 0.85rem;
  margin-top: 10px;
  text-align: left;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
