<template>
  <div class="anime-container">
    <div class="header">
      <button class="home-btn" @click="goHome">← 首页</button>
      <h1>动漫管理</h1>
      <button class="create-btn" @click="showCreateModal = true">+ 创建</button>
    </div>

    <div class="anime-grid" v-if="list.length > 0">
      <div 
        class="anime-card" 
        v-for="item in list" 
        :key="item.id"
        @click="goDetail(item.id)"
      >
        <div class="card-image">
          <img v-if="item.image" :src="item.image" />
          <span v-else class="no-image">暂无图片</span>
        </div>
        <div class="card-name">{{ item.name }}</div>
      </div>
    </div>
    <div v-else class="no-data">当前没有数据</div>

    <div class="pagination" v-if="total > 0">
      <button :disabled="pageNum <= 1" @click="changePage(pageNum - 1)">上一页</button>
      <span>{{ pageNum }} / {{ totalPages }}</span>
      <button :disabled="pageNum >= totalPages" @click="changePage(pageNum + 1)">下一页</button>
    </div>

    <!-- 创建弹窗 -->
    <div v-if="showCreateModal" class="modal">
      <div class="modal-content">
        <h2>创建动漫</h2>
        <form @submit.prevent="handleCreate">
          <div class="form-group">
            <label>图片</label>
            <div class="upload-section">
              <input 
                type="file" 
                @change="handleFileChange" 
                accept="image/*"
              />
              <div v-if="uploadPreview" class="upload-preview">
                <img :src="uploadPreview" />
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>名称</label>
            <input v-model="form.name" class="large-input" required />
          </div>
          <div class="form-group">
            <label>简介</label>
            <textarea v-model="form.description" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>评价</label>
            <textarea v-model="form.evaluation" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>评分</label>
            <input v-model="form.rating" class="large-input" placeholder="如：9.5分" />
          </div>
          <div class="modal-buttons">
            <button type="button" @click="closeModal">取消</button>
            <button type="submit" class="primary" :disabled="uploading">
              {{ uploading ? '上传中...' : '创建' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const list = ref([])
const pageNum = ref(1)
const pageSize = 25  // 5排 * 5个 = 25
const total = ref(0)
const showCreateModal = ref(false)
const uploading = ref(false)
const uploadPreview = ref('')
const selectedFile = ref(null)

const form = ref({
  name: '',
  image: '',
  description: '',
  evaluation: '',
  rating: ''
})

const totalPages = computed(() => Math.ceil(total.value / pageSize))

const fetchList = async () => {
  try {
    const res = await fetch(`http://localhost:2333/api/anime/list?pageNum=${pageNum.value}&pageSize=${pageSize}`)
    const result = await res.json()
    if (result.code === 200) {
      list.value = result.data.records
      total.value = result.data.total
    }
  } catch (e) {
    console.error(e)
  }
}

const changePage = (newPage) => {
  pageNum.value = newPage
  fetchList()
}

const goDetail = (id) => {
  router.push(`/anime/${id}`)
}

const goHome = () => {
  router.push('/')
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    selectedFile.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      uploadPreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const uploadImage = async () => {
  if (!selectedFile.value) return form.value.image
  
  const formData = new FormData()
  formData.append('file', selectedFile.value)
  
  try {
    const res = await fetch('http://localhost:2333/api/upload', {
      method: 'POST',
      body: formData
    })
    const result = await res.json()
    if (result.url) {
      return result.url
    }
  } catch (e) {
    console.error('上传失败', e)
  }
  return ''
}

const handleCreate = async () => {
  uploading.value = true
  try {
    const imageUrl = await uploadImage()
    form.value.image = imageUrl
    
    const res = await fetch('http://localhost:2333/api/anime', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    })
    const result = await res.json()
    if (result.code === 200) {
      alert('创建成功')
      closeModal()
      fetchList()
    } else {
      alert(result.message)
    }
  } catch (e) {
    alert('创建失败')
  } finally {
    uploading.value = false
  }
}

const closeModal = () => {
  showCreateModal.value = false
  form.value = { name: '', image: '', description: '', evaluation: '', rating: '' }
  uploadPreview.value = ''
  selectedFile.value = null
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除吗？')) return
  try {
    const res = await fetch(`http://localhost:2333/api/anime/${id}`, { method: 'DELETE' })
    const result = await res.json()
    if (result.code === 200) {
      fetchList()
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(fetchList)
</script>

<style scoped>
.anime-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.home-btn {
  padding: 8px 16px;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.home-btn:hover {
  background: #1976D2;
}

.header h1 {
  color: #333;
}

.create-btn {
  padding: 10px 20px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

/* 5列网格布局 */
.anime-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  width: 70%;
  margin: 0 auto;
}

.anime-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.anime-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0,0,0,0.15);
}

.card-image {
  width: 100%;
  aspect-ratio: 3/4;
  background: #f5f5f5;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-image .no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #999;
}

.card-name {
  padding: 12px;
  text-align: center;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.no-data {
  padding: 60px;
  text-align: center;
  color: #999;
  font-size: 16px;
  background: white;
  border-radius: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

.pagination button {
  padding: 8px 16px;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.pagination button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 90%;
  max-width: 500px;
}

.modal-content h2 {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: 500;
}

.form-group input[type="text"],
.form-group textarea {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.large-input {
  padding: 15px 20px !important;
  font-size: 18px !important;
  font-weight: 500 !important;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.upload-section input[type="file"] {
  padding: 5px;
}

.upload-preview img {
  width: 100px;
  height: 130px;
  object-fit: cover;
  border-radius: 5px;
  border: 1px solid #ddd;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.modal-buttons button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal-buttons button:not(.primary) {
  background: #ccc;
}

.modal-buttons .primary {
  background: #4CAF50;
  color: white;
}

.modal-buttons .primary:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
