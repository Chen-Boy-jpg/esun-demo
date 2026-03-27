<template>
  <div id="app-layout">
    <nav v-if="showNavbar" class="navbar">
      <div class="nav-content">
        <div class="nav-left" @click="router.push('/')">
          <span class="logo-icon">🌟</span>
          <span class="logo-text">社群廣場</span>
        </div>

        <div class="nav-right">
          <template v-if="isLoggedIn">
            <button @click="router.push('/create')" class="btn-create">
              + 發佈
            </button>

            <div
              class="user-profile-link"
              @click="router.push('/profile/edit')"
              title="編輯個人資料"
            >
              <div class="nav-avatar">
                <img
                  v-if="currentUser?.coverImage"
                  :src="`http://localhost:8080${currentUser.coverImage}`"
                  alt="Avatar"
                />
                <span v-else>{{
                  currentUser?.userName?.charAt(0).toUpperCase()
                }}</span>
              </div>
            </div>

            <button @click="handleLogout" class="btn-logout">登出</button>
          </template>

          <button v-else @click="router.push('/login')" class="btn-login">
            登入
          </button>
        </div>
      </div>
    </nav>

    <router-view />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { AuthService } from "./services/auth.service";
import { UserService } from "./services/user.service";

const router = useRouter();
const route = useRoute();
const currentUser = ref<any>(null);

// 判斷是否顯示 Navbar
const showNavbar = computed(() => {
  const hideOnPages = ["login", "register"];
  return !hideOnPages.includes(route.name as string);
});

// 判斷登入狀態
const isLoggedIn = computed(() => AuthService.isAuthenticated());

// 獲取使用者資料
const fetchUser = async () => {
  if (isLoggedIn.value) {
    try {
      currentUser.value = await UserService.getCurrentUser();
    } catch (err) {
      console.error("無法取得使用者資訊", err);

      handleLogout();
    }
  } else {
    currentUser.value = null;
  }
};

/**
 * 處理登出
 */
const handleLogout = () => {
  AuthService.logout();

  currentUser.value = null;

  router.push("/login");
};

// 監聽路由變化
watch(
  () => route.path,
  () => {
    fetchUser();
  },
);

onMounted(fetchUser);
</script>

<style scoped>
/* 原有樣式保持... */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e2e8f0;
  z-index: 1000;
  display: flex;
  justify-content: center;
}
.nav-content {
  width: 100%;
  max-width: 1200px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.nav-left {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}
.logo-text {
  font-weight: 800;
  font-size: 1.25rem;
  color: #10b981;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 頭像與連結 */
.user-profile-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.nav-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #10b981;
  color: white;
  overflow: hidden;
  display: grid;
  place-items: center;
  font-weight: bold;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: 0.2s;
}
.nav-avatar:hover {
  transform: scale(1.05);
}
.nav-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 按鈕樣式 */
.btn-create {
  background: #10b981;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.btn-create:hover {
  background: #059669;
}

.btn-login {
  background: transparent;
  color: #10b981;
  border: 1px solid #10b981;
  padding: 6px 16px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

/* 🗑️ 登出按鈕樣式 */
.btn-logout {
  background: #f1f5f9;
  color: #64748b;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.btn-logout:hover {
  background: #fee2e2;
  color: #ef4444;
}
</style>
