<template>
  <div class="app-layout">
    <main class="home-container">
      <header class="content-header">
        <h1>最新動態</h1>
        <p v-if="!isAppLoading" class="stats-info">
          目前有 {{ postsWithCalculatedCount.length }} 則討論
        </p>
      </header>

      <div v-if="isAppLoading" class="status-box">
        <div class="spinner"></div>
        <p>正在同步最新資訊...</p>
      </div>

      <div v-else-if="postsError" class="status-box error">
        <p>連線不穩定，請檢查後端服務</p>
        <button @click="() => refetchPosts()" class="btn-retry">重試</button>
      </div>

      <div v-else-if="postsWithCalculatedCount.length > 0" class="post-feed">
        <div
          v-for="post in postsWithCalculatedCount"
          :key="post.postId"
          class="post-card"
        >
          <div class="card-top">
            <div class="author-info">
              <div class="avatar-circle">
                <img
                  v-if="post.authorCoverImage"
                  :src="getImageUrl(post.authorCoverImage)"
                  class="avatar-img"
                />
                <span v-else>{{
                  post.authorName?.charAt(0).toUpperCase()
                }}</span>
              </div>
              <div class="meta-text">
                <span class="author-name">@{{ post.authorName }}</span>
                <span class="post-time">{{ formatDate(post.createdAt) }}</span>
              </div>
            </div>

            <div
              v-if="currentUser && currentUser.userName === post.authorName"
              class="author-actions"
            >
              <button @click.stop="goToEditPost(post)" class="btn-action edit">
                ✏️ 編輯
              </button>
              <button
                @click.stop="handleDelete(post.postId)"
                class="btn-action delete"
              >
                🗑️ 刪除
              </button>
            </div>
          </div>

          <div
            v-if="post.imageUrl && post.imageUrl.trim() !== ''"
            class="post-image-container"
            @click="goToDetail(post)"
          >
            <img
              :src="getImageUrl(post.imageUrl)"
              alt="Post Image"
              class="main-img"
              loading="lazy"
            />
          </div>

          <div class="post-body" @click="goToDetail(post)">
            <p class="content-text">{{ post.content }}</p>
          </div>

          <div class="card-bottom" @click="goToDetail(post)">
            <div class="interaction">
              <span class="comment-stat"
                >💬 {{ post.commentCount }} 則留言</span
              >
            </div>
            <span class="read-more">閱讀全文 →</span>
          </div>
        </div>
      </div>

      <div v-else class="status-box empty">
        <p>🍃 這裡空空如也，來發第一篇文吧！</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useQuery, useMutation, useQueryClient } from "@tanstack/vue-query";
import { PostService } from "../services/post.service";
import { CommentService } from "../services/comment.service";
import { UserService } from "../services/user.service";

interface EnhancedPost {
  postId: number;
  content: string;
  authorName: string;
  createdAt: string;
  imageUrl?: string | null;
  authorCoverImage?: string | null;
  commentCount: number;
}

const router = useRouter();
const queryClient = useQueryClient();
const currentUser = ref<any>(null);

//  初始化獲取當前使用者
onMounted(async () => {
  try {
    currentUser.value = await UserService.getCurrentUser();

    queryClient.invalidateQueries({ queryKey: ["posts"] });
  } catch (err) {
    console.error("無法同步使用者資料", err);
  }
});

//  獲取貼文列表資料
const {
  data: posts,
  isLoading: postsLoading,
  isError: postsError,
  refetch: refetchPosts,
} = useQuery({
  queryKey: ["posts"],
  queryFn: PostService.getAllPosts,
  initialData: [],
  staleTime: 5000,
});

//  獲取所有留言 (用於計算數量)
const { data: allComments, isLoading: commentsLoading } = useQuery({
  queryKey: ["all-comments"],
  queryFn: CommentService.getAllComments,
  initialData: [],
  staleTime: 1000 * 30,
});

//  刪除貼文的 Mutation
const { mutate: deletePostMutation } = useMutation({
  mutationFn: (id: number) => PostService.deletePost(id),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ["posts"] });
    queryClient.invalidateQueries({ queryKey: ["all-comments"] });
  },
  onError: (error: any) => {
    alert(error.response?.data || "刪除貼文失敗");
  },
});

// 執行刪除前的確認
const handleDelete = (id: number) => {
  if (confirm("確定要刪除這則貼文嗎？刪除後將無法還原。")) {
    deletePostMutation(id);
  }
};

//  整合與計算貼文顯示資料
const postsWithCalculatedCount = computed<EnhancedPost[]>(() => {
  const currentPosts = (posts.value as any[]) || [];
  const currentComments = (allComments.value as any[]) || [];

  return currentPosts.map((post) => {
    const count = currentComments.filter(
      (c) => Number(c.postId) === Number(post.postId),
    ).length;
    return {
      ...post,
      commentCount: count,
      authorCoverImage: post.authorCoverImage || post.coverImage || null,
    };
  });
});

/**
 * 圖片處理與快取控制
 */
const getImageUrl = (url: string | null | undefined) => {
  if (!url) return "";
  const baseUrl = "http://localhost:8080";
  const cleanPath = url.startsWith("/") ? url : `/${url}`;

  return `${baseUrl}${cleanPath}?v=${Date.now()}`;
};

const goToDetail = (post: EnhancedPost) => {
  router.push({
    name: "post-detail",
    params: { id: post.postId.toString() },

    state: { postData: JSON.parse(JSON.stringify(post)) },
  });
};

const goToEditPost = (post: EnhancedPost) => {
  router.push({ name: "edit-post", params: { id: post.postId.toString() } });
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "未知時間";
  return new Date(dateStr).toLocaleDateString("zh-TW", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const isAppLoading = computed(
  () => postsLoading.value || commentsLoading.value,
);
</script>

<style scoped>
.app-layout {
  background-color: #f8fafc;
  min-height: 100vh;
}
.home-container {
  max-width: 680px;
  margin: 0 auto;
  padding: 100px 20px 40px;
}
.content-header {
  margin-bottom: 24px;
  text-align: left;
}
.stats-info {
  color: #94a3b8;
  font-size: 0.9rem;
  margin-top: 4px;
}

.post-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  margin-bottom: 24px;
  transition: 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.post-card:hover {
  border-color: #10b981;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.03);
}

.card-top {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar-circle {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #10b981;
  color: white;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.meta-text {
  display: flex;
  flex-direction: column;
  text-align: left;
}
.author-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 0.95rem;
}
.post-time {
  font-size: 0.75rem;
  color: #94a3b8;
}

/* 刪除與編輯按鈕樣式 */
.author-actions {
  display: flex;
  gap: 8px;
}
.btn-action {
  border: none;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.btn-action.edit {
  background: #f1f5f9;
  color: #64748b;
}
.btn-action.edit:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.btn-action.delete {
  background: #fff1f2;
  color: #f43f5e;
}
.btn-action.delete:hover {
  background: #ffe4e6;
  color: #e11d48;
}

.post-image-container {
  width: 100%;
  background: #f8fafc;
  cursor: pointer;
  border-top: 1px solid #f1f5f9;
  border-bottom: 1px solid #f1f5f9;
}
.main-img {
  width: 100%;
  max-height: 500px;
  object-fit: cover;
  display: block;
}

.post-body {
  padding: 16px 20px;
  text-align: left;
  cursor: pointer;
}
.content-text {
  color: #334155;
  font-size: 1.05rem;
  line-height: 1.6;
  white-space: pre-wrap;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 5;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-bottom {
  padding: 14px 20px;
  border-top: 1px solid #f8fafc;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #94a3b8;
  font-size: 0.9rem;
  cursor: pointer;
}
.comment-stat {
  font-weight: 600;
  color: #64748b;
}
.read-more {
  color: #10b981;
  font-weight: 700;
}

.status-box {
  padding: 80px 20px;
  text-align: center;
  color: #94a3b8;
}
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #10b981;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}
@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}
.btn-retry {
  margin-top: 12px;
  background: white;
  border: 1px solid #10b981;
  color: #10b981;
  padding: 6px 20px;
  border-radius: 8px;
  cursor: pointer;
}
</style>
