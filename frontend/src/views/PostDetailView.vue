<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query';
import { useForm } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import * as zod from 'zod';
import { PostService } from '../services/post.service';
import { CommentService } from '../services/comment.service';
import { AuthService } from '../services/auth.service';
import type { PostResponse } from '../types/post';

const route = useRoute();
const router = useRouter();
const queryClient = useQueryClient();

// 路由參數 ID
const routeId = Number(route.params.id);

// 1. 接收 Props (由 HomeView 透過 router.push 傳入)
const props = defineProps<{
  id: string | number;
  post?: PostResponse; 
}>();

// 2. 取得貼文詳情 (如果 Props 沒傳，例如重新整理，則發起請求)
const { data: apiPost, isLoading: postLoading } = useQuery({
  queryKey: ['post', routeId],
  queryFn: () => PostService.getPostById(routeId),
  enabled: !props.post, 
  staleTime: 1000 * 60 * 5,
});

// 計算最終顯示的資料源
const displayPost = computed(() => props.post || apiPost.value);

// 3. 取得留言列表 (前端 Filter 模式)
const { data: comments = [], isLoading: commentsLoading } = useQuery({
  queryKey: ['comments', routeId],
  queryFn: () => CommentService.getAllComments(),
  select: (allComments) => {
    return allComments.filter((c) => Number(c.postId) === routeId);
  }
});

// 4. 留言表單驗證
const schema = toTypedSchema(
  zod.object({
    content: zod.string().min(1, '內容不能為空').max(200, '字數超出上限')
  })
);
const { errors, defineField, handleSubmit, resetForm } = useForm({ validationSchema: schema });
const [content, contentProps] = defineField('content');

// 5. 新增留言 Mutation
const { mutate: submitComment, isPending: isSubmitting } = useMutation({
  mutationFn: (data: { content: string }) => CommentService.addComment(routeId, data),
  onSuccess: () => {
    // 重新整理當前頁面留言與首頁留言數
    queryClient.invalidateQueries({ queryKey: ['comments', routeId] });
    queryClient.invalidateQueries({ queryKey: ['all-comments'] });
    resetForm();
  },
  onError: (err: any) => alert(err.message || '留言發送失敗')
});

const onCommentSubmit = handleSubmit((values) => {
  if (!AuthService.isAuthenticated()) {
    alert('請先登入！');
    router.push('/login');
    return;
  }
  submitComment(values);
});

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString('zh-TW', {
    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  });
};
</script>

<template>
  <div class="detail-container">
    <nav class="nav-header">
      <button @click="router.push('/')" class="btn-back">
        <span class="icon">←</span> 返回廣場
      </button>
    </nav>

    <main v-if="displayPost" class="content-wrapper">
      <article class="main-card">
        <header class="card-header">
          <div class="category-tag">動態消息</div>
          <h1 class="post-title">{{ displayPost.content.substring(0, 15) }}...</h1>
          
          <div class="author-row">
            <div class="avatar">{{ displayPost.authorName.charAt(0).toUpperCase() }}</div>
            <div class="author-info">
              <span class="name">@{{ displayPost.authorName }}</span>
              <span class="date">{{ formatDate(displayPost.createdAt) }}</span>
            </div>
          </div>
        </header>

        <section class="card-body">
          <p class="main-text">{{ displayPost.content }}</p>
        </section>

        <footer class="card-footer">
          <div class="stats">
            <span>💬 {{ comments?.length || 0 }} 則評論</span>
          </div>
        </footer>
      </article>

      <section class="comment-area">
        <h3 class="area-title">發表評論</h3>
        
        <form @submit="onCommentSubmit" class="editor-box">
          <textarea 
            v-model="content" 
            v-bind="contentProps"
            placeholder="分享你的想法..." 
            rows="3"
            :disabled="isSubmitting"
          ></textarea>
          <div class="editor-footer">
            <span class="error-msg">{{ errors.content }}</span>
            <button type="submit" :disabled="isSubmitting || !content" class="btn-post">
              {{ isSubmitting ? '傳送中...' : '發送留言' }}
            </button>
          </div>
        </form>

        <div class="comments-list">
          <div v-if="commentsLoading" class="status-msg">正在加載評論...</div>
          
          <template v-else-if="comments?.length ||0">
            <div v-for="item in comments" :key="item.id" class="comment-item">
              <div class="item-head">
                <span class="item-user">{{ item.authorName }}</span>
                <span class="item-date">{{ formatDate(item.createdAt) }}</span>
              </div>
              <p class="item-body">{{ item.content }}</p>
            </div>
          </template>

          <div v-else class="empty-msg">
            <span class="icon">🍃</span>
            <p>目前還沒有留言，快來搶頭香！</p>
          </div>
        </div>
      </section>
    </main>

    <div v-else class="full-loading">
      <div class="loader"></div>
      <p>正在載入內容...</p>
    </div>
  </div>
</template>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: 100vh;
  color: #1e293b;
}

/* 返回按鈕 */
.nav-header { margin-bottom: 24px; }
.btn-back {
  background: none; border: none; color: #64748b;
  font-weight: 600; cursor: pointer; display: flex;
  align-items: center; gap: 8px; transition: 0.2s;
}
.btn-back:hover { color: #10b981; transform: translateX(-4px); }

/* 主卡片樣式 */
.main-card {
  background: #fff;
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  margin-bottom: 32px;
}

.category-tag {
  display: inline-block; background: #f0fdf4; color: #10b981;
  font-size: 12px; font-weight: 700; padding: 4px 12px;
  border-radius: 8px; margin-bottom: 16px;
}

.post-title { font-size: 2rem; font-weight: 800; margin-bottom: 24px; line-height: 1.2; }

.author-row { display: flex; align-items: center; gap: 12px; }
.avatar {
  width: 40px; height: 40px; background: #10b981; color: white;
  border-radius: 12px; display: grid; place-items: center; font-weight: 800;
}

.author-info { display: flex; flex-direction: column; }
.author-info .name { font-weight: 700; font-size: 15px; }
.author-info .date { font-size: 13px; color: #94a3b8; }

.main-text {
  font-size: 1.125rem; line-height: 1.8; color: #334155;
  margin: 32px 0; white-space: pre-wrap;
}

.card-footer { border-top: 1px solid #f1f5f9; padding-top: 20px; color: #94a3b8; font-size: 14px; }

/* 留言區 */
.comment-area { text-align: left; }
.area-title { font-size: 1.25rem; font-weight: 800; margin-bottom: 20px; }

.editor-box {
  background: #f8fafc; border: 1px solid #e2e8f0;
  border-radius: 16px; padding: 16px; transition: 0.3s;
}
.editor-box:focus-within { border-color: #10b981; background: #fff; box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.1); }

textarea {
  width: 100%; background: transparent; border: none; outline: none;
  font-size: 1rem; resize: none; font-family: inherit;
}

.editor-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.error-msg { color: #ef4444; font-size: 13px; }

.btn-post {
  background: #10b981; color: white; border: none;
  padding: 8px 20px; border-radius: 10px; font-weight: 700;
  cursor: pointer; transition: 0.2s;
}
.btn-post:hover:not(:disabled) { background: #059669; }
.btn-post:disabled { opacity: 0.5; cursor: not-allowed; }

/* 留言列表 */
.comment-item {
  padding: 20px; background: #fff; border-radius: 16px;
  margin-top: 16px; border: 1px solid #f1f5f9;
}
.item-head { display: flex; justify-content: space-between; margin-bottom: 8px; }
.item-user { font-weight: 700; font-size: 14px; }
.item-date { font-size: 12px; color: #cbd5e1; }
.item-body { margin: 0; color: #475569; line-height: 1.6; }

.empty-msg { text-align: center; padding: 40px; color: #94a3b8; }
.empty-msg .icon { font-size: 32px; display: block; margin-bottom: 8px; }

/* Loading 動畫 */
.loader {
  width: 24px; height: 24px; border: 3px solid #f1f5f9;
  border-top-color: #10b981; border-radius: 50%;
  animation: spin 0.8s linear infinite; margin: 0 auto 10px;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>