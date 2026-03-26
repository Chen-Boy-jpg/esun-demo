<template>
  <div class="edit-profile-container">
    <div class="edit-card">
      <h2>編輯個人資料</h2>
      
      <div class="avatar-upload">
        <div class="avatar-preview" @click="triggerClick">
          <img 
            :src="previewUrl || (user.coverImage ? `http://localhost:8080${user.coverImage}` : '/default-avatar.png')" 
            class="circle-img" 
          />
          <div class="upload-overlay">
            <span>更換照片</span>
          </div>
        </div>
        <input 
          type="file" 
          ref="fileInput" 
          @change="onFileChange" 
          accept="image/*" 
          hidden 
        />
      </div>

      <div class="form-group">
        <label>暱稱</label>
        <input v-model="user.userName" type="text" placeholder="輸入你的暱稱" />
      </div>

      <div class="form-group">
        <label>個人簡介</label>
        <textarea v-model="user.biography" rows="4" placeholder="介紹一下你自己..."></textarea>
      </div>

      <div class="action-bar">
        <button @click="router.back()" class="btn-cancel">取消</button>
        <button @click="handleUpdateProfile" :disabled="isUpdating" class="btn-save">
          {{ isUpdating ? '儲存中...' : '儲存變更' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// 1. 引入剛剛建立的 UserService
import { UserService } from '../services/user.service';

const router = useRouter();
const user = ref({ userName: '', biography: '', coverImage: '' });
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string | null>(null);
const isUpdating = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const triggerClick = () => {
  fileInput.value?.click();
};

// 2. 初始化：使用 UserService 取得資料
onMounted(async () => {
  try {
    const data = await UserService.getCurrentUser();
    // 將 API 回傳的資料填入響應式變數
    user.value = {
      userName: data.userName,
      biography: data.biography || '',
      coverImage: data.coverImage || ''
    };
  } catch (err) {
    console.error("初始化資料失敗", err);
    // 這裡可以考慮加入 router.push('/login') 如果是 401 錯誤的話
  }
});

const onFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];
  if (file) {
    selectedFile.value = file;
    previewUrl.value = URL.createObjectURL(file);
  }
};

// 3. 提交更新：使用 UserService 更新資料
const handleUpdateProfile = async () => {
  if (!user.value.userName.trim()) {
    alert('暱稱不能為空');
    return;
  }

  isUpdating.value = true;
  try {
    // 直接呼叫 service，不用再手動處理 FormData 和 headers
    await UserService.updateProfile(
      user.value.userName,
      user.value.biography,
      selectedFile.value
    );
    
    alert('個人資料更新成功！');
    router.push('/'); 
  } catch (err) {
    console.error("更新失敗:", err);
    alert('更新失敗，請檢查網路連線或稍後再試');
  } finally {
    isUpdating.value = false;
  }
};
</script>

<style scoped>
/* 樣式保持不變 */
.edit-profile-container { padding: 40px 20px; display: flex; justify-content: center; }
.edit-card { background: white; padding: 30px; border-radius: 20px; width: 100%; max-width: 500px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); }
.avatar-upload { display: flex; justify-content: center; margin-bottom: 30px; }
.avatar-preview { position: relative; width: 120px; height: 120px; border-radius: 50%; overflow: hidden; border: 4px solid #10b981; cursor: pointer; transition: transform 0.2s; }
.avatar-preview:hover { transform: scale(1.02); }
.circle-img { width: 100%; height: 100%; object-fit: cover; }
.upload-overlay { position: absolute; bottom: 0; width: 100%; background: rgba(0,0,0,0.5); color: white; font-size: 12px; text-align: center; padding: 4px 0; opacity: 0; transition: 0.3s; pointer-events: none; }
.avatar-preview:hover .upload-overlay { opacity: 1; }
.form-group { margin-bottom: 20px; text-align: left; }
.form-group label { display: block; font-weight: 700; margin-bottom: 8px; color: #475569; }
.form-group input, .form-group textarea { width: 100%; padding: 12px; border: 1px solid #e2e8f0; border-radius: 10px; font-size: 1rem; transition: border-color 0.2s; }
.form-group input:focus, .form-group textarea:focus { border-color: #10b981; outline: none; }
.action-bar { display: flex; gap: 12px; margin-top: 30px; }
.btn-save { flex: 1; background: #10b981; color: white; border: none; padding: 12px; border-radius: 10px; font-weight: 700; cursor: pointer; transition: background 0.2s; }
.btn-save:hover:not(:disabled) { background: #059669; }
.btn-save:disabled { background: #94a3b8; cursor: not-allowed; }
.btn-cancel { flex: 1; background: #f1f5f9; color: #64748b; border: none; padding: 12px; border-radius: 10px; font-weight: 700; cursor: pointer; }
</style>