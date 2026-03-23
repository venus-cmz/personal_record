<template>
  <div class="home-container">
    <div class="header">
      <h1>欢迎来到系统</h1>
      <div class="user-info">
        <span>欢迎, {{ username }}</span>
        <button @click="handleLogout">退出登录</button>
      </div>
    </div>
    <div class="content">
      <div class="nav-grid">
        <div class="nav-item" @click="goAnime">
          <div class="nav-label">动漫</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username') || '用户'
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/')
}

const goAnime = () => {
  router.push('/anime')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.header {
  background: white;
  padding: 20px 30px;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.header h1 {
  color: #333;
  font-size: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  color: #555;
  font-size: 14px;
}

.user-info button {
  padding: 8px 20px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.user-info button:hover {
  background: #c0392b;
}

.content {
  margin-top: 30px;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}

.nav-item {
  background: white;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.nav-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.nav-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.nav-label {
  color: #333;
  font-size: 18px;
  font-weight: 500;
}
</style>
