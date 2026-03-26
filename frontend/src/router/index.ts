import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import EditProfileView from "../views/EditProfileView.vue";
import EditPostView from "../views/EditPostView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: { requiresAuth: false }, // 公開頁面
    },
    {
      path: "/register",
      name: "register",
      component: () => import("../views/RegisterView.vue"),
      meta: { requiresAuth: false }, // 公開頁面
    },
    {
      path: "/",
      name: "home",
      component: () => import("../views/HomeView.vue"),
      meta: { requiresAuth: true }, // 需要登入
    },
    {
      path: "/post/:id",
      name: "post-detail",
      component: () => import("../views/PostDetailView.vue"),
      props: (route) => ({
        id: route.params.id,
        post: window.history.state?.postData,
      }),
      meta: { requiresAuth: true },
    },
    {
      path: "/create",
      name: "create-post",
      component: () => import("../views/CreatePostView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/profile/edit",
      name: "profile-edit",
      component: EditProfileView,
      meta: { requiresAuth: true },
    },
    {
      path: "/post/edit/:id",
      name: "edit-post",
      component: EditPostView,
      props: true,
      meta: { requiresAuth: true },
    },
    // 捕獲所有未定義的路由，導向首頁
    {
      path: "/:pathMatch(.*)*",
      redirect: "/",
    },
  ],
});

// --- 核心邏輯：全局路由守衛 ---
router.beforeEach((to, from, next) => {
  // 從 localStorage 取得 token
  const token = localStorage.getItem("token");

  // 1. 如果進入的是需要驗證的頁面 (requiresAuth) 且沒有 token
  if (to.meta.requiresAuth !== false && !token) {
    // 這裡我們假設預設所有頁面都需要登入，除非 meta.requiresAuth 明確設為 false
    if (to.name !== "login" && to.name !== "register") {
      return next({ name: "login" });
    }
    next();
  }
  // 2. 如果已經有 token，卻還想去 login 或 register 頁面
  else if (token && (to.name === "login" || to.name === "register")) {
    // 自動跳轉回首頁
    return next({ name: "home" });
  }
  // 3. 其他情況正常通行
  else {
    next();
  }
});

export default router;
