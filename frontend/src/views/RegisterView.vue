<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useForm } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import { RegisterRequestSchema } from '../types/auth';
import { AuthService } from '../services/auth.service';

const router = useRouter();

// 1. 初始化表單並綁定 Zod Schema
const { values, errors, defineField, handleSubmit, isSubmitting } = useForm({
  validationSchema: toTypedSchema(RegisterRequestSchema),
  // 設定初始值，確保符合 RegisterRequest 型別
  initialValues: {
    userName: '',
    phoneNumber: '',
    email: '',
    password: '',
    confirmPassword: ''
  }
});

// 2. 定義欄位與驗證屬性
// defineField 會回傳 modelValue 與相關事件（如 blur）
const [userName, userNameProps] = defineField('userName');
const [phoneNumber, phoneProps] = defineField('phoneNumber');
const [email, emailProps] = defineField('email');
const [password, passwordProps] = defineField('password');
const [confirmPassword, confirmProps] = defineField('confirmPassword');

// 3. 處理註冊提交
const onSubmit = handleSubmit(async (formValues) => {
  try {
    // 呼叫 Service 進行 API 請求
    const response = await AuthService.register(formValues);
    
    alert(response.message || '註冊成功！將跳轉至登入頁面');
    
    // 成功後導向登入頁
    router.push('/login');
  } catch (err: any) {
    // 這裡會抓到 api.ts 攔截器拋出的 Error(errorMessage)
    alert(`註冊失敗：${err.message}`);
  }
});
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <h2>建立新帳號</h2>
      <p class="subtitle">加入我們的社群，分享你的生活</p>

      <form @submit="onSubmit" class="register-form">
        <div class="form-item">
          <label>使用者名稱</label>
          <input 
            v-model="userName" 
            v-bind="userNameProps" 
            placeholder="例如：小明"
            :class="{ 'input-error': errors.userName }"
          />
          <span class="error-msg">{{ errors.userName }}</span>
        </div>

        <div class="form-item">
          <label>手機號碼</label>
          <input 
            v-model="phoneNumber" 
            v-bind="phoneProps" 
            placeholder="0912345678"
            :class="{ 'input-error': errors.phoneNumber }"
          />
          <span class="error-msg">{{ errors.phoneNumber }}</span>
        </div>

        <div class="form-item">
          <label>電子信箱</label>
          <input 
            v-model="email" 
            v-bind="emailProps" 
            type="email"
            placeholder="example@mail.com"
            :class="{ 'input-error': errors.email }"
          />
          <span class="error-msg">{{ errors.email }}</span>
        </div>

        <div class="form-item">
          <label>密碼</label>
          <input 
            v-model="password" 
            v-bind="passwordProps" 
            type="password"
            placeholder="至少 6 個字元"
            :class="{ 'input-error': errors.password }"
          />
          <span class="error-msg">{{ errors.password }}</span>
        </div>

        <div class="form-item">
          <label>確認密碼</label>
          <input 
            v-model="confirmPassword" 
            v-bind="confirmProps" 
            type="password"
            placeholder="請再次輸入密碼"
            :class="{ 'input-error': errors.confirmPassword }"
          />
          <span class="error-msg">{{ errors.confirmPassword }}</span>
        </div>

        <button type="submit" class="submit-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '註冊中...' : '立即註冊' }}
        </button>

        <div class="auth-link">
          已經有帳號了？ <router-link to="/login">點此登入</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 450px;
  padding: 2.5rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.05);
  border: 1px solid #f0f0f0;
}

h2 { margin-bottom: 0.5rem; color: #333; }
.subtitle { color: #888; margin-bottom: 2rem; font-size: 0.9rem; }

.form-item {
  margin-bottom: 1.2rem;
  text-align: left;
}

label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #555;
}

input {
  width: 100%;
  padding: 12px;
  border: 1.5px solid #eee;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.input-error {
  border-color: #ff4d4f;
}

.error-msg {
  color: #ff4d4f;
  font-size: 0.75rem;
  margin-top: 4px;
  height: 1rem;
  display: block;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 1rem;
  transition: background 0.3s;
}

.submit-btn:hover { background-color: #3aa876; }
.submit-btn:disabled { background-color: #ccc; cursor: not-allowed; }

.auth-link {
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: #666;
}

.auth-link a {
  color: #42b983;
  text-decoration: none;
  font-weight: bold;
}
</style>