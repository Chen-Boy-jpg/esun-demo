<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useForm } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import { LoginRequestSchema } from '../types/auth';
import { AuthService } from '../services/auth.service';

const router = useRouter();
const serverError = ref(''); // 用於顯示後端回傳的錯誤（如：密碼錯誤）

// 1. 初始化表單與 Zod 驗證
const { errors, defineField, handleSubmit, isSubmitting } = useForm({
  validationSchema: toTypedSchema(LoginRequestSchema),
  initialValues: {
    phone: '',
    password: ''
  }
});

// 2. 定義欄位（對應 Zod 中的 phone 與 password）
const [phone, phoneProps] = defineField('phone');
const [password, passwordProps] = defineField('password');

// 3. 處理登入提交
const onSubmit = handleSubmit(async (values) => {
  try {
    serverError.value = '';
    // 呼叫 AuthService.login，它內部已經處理好 localStorage.setItem('token')
    await AuthService.login(values);
    
    // 登入成功跳轉
    router.push('/');
  } catch (err: any) {
    // 顯示後端噴出來的錯誤訊息（例如：該手機號碼尚未註冊）
    serverError.value = err.message || '登入失敗，請稍後再試';
  }
});
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="logo">
        <h2>歡迎回來</h2>
        <p>請登入以繼續使用社群功能</p>
      </div>

      <form @submit="onSubmit" class="login-form">
        <div class="form-item">
          <label>手機號碼</label>
          <input 
            v-model="phone" 
            v-bind="phoneProps" 
            placeholder="請輸入手機號碼"
            :class="{ 'input-error': errors.phone }"
          />
          <span class="error-msg">{{ errors.phone }}</span>
        </div>

        <div class="form-item">
          <label>密碼</label>
          <input 
            v-model="password" 
            v-bind="passwordProps" 
            type="password"
            placeholder="請輸入密碼"
            :class="{ 'input-error': errors.password }"
          />
          <span class="error-msg">{{ errors.password }}</span>
        </div>

        <div v-if="serverError" class="alert-error">
          {{ serverError }}
        </div>

        <button type="submit" class="submit-btn" :disabled="isSubmitting">
          {{ isSubmitting ? '驗證中...' : '登入' }}
        </button>

        <div class="auth-footer">
          還沒有帳號？ <router-link to="/register">立即註冊</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}

.logo {
  text-align: center;
  margin-bottom: 2rem;
}

.logo h2 { color: #2c3e50; margin-bottom: 0.5rem; }
.logo p { color: #7f8c8d; font-size: 0.9rem; }

.form-item {
  margin-bottom: 1.5rem;
  text-align: left;
}

label {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #34495e;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

input:focus {
  outline: none;
  border-color: #42b983;
}

.input-error { border-color: #e74c3c; }

.error-msg {
  color: #e74c3c;
  font-size: 0.8rem;
  margin-top: 5px;
  display: block;
}

.alert-error {
  background-color: #fdf2f2;
  color: #e74c3c;
  padding: 10px;
  border-radius: 6px;
  font-size: 0.85rem;
  margin-bottom: 1.5rem;
  border: 1px solid #facdcd;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover { background-color: #3aa876; }
.submit-btn:disabled { background-color: #95a5a6; cursor: not-allowed; }

.auth-footer {
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: #7f8c8d;
}

.auth-footer a {
  color: #42b983;
  text-decoration: none;
  font-weight: bold;
}
</style>