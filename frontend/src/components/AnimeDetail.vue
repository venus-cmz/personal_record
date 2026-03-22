<template>
  <div class="detail-container">
    <div class="header">
      <button @click="goBack">← 返回</button>
      <h1>动漫详情</h1>
    </div>

    <div class="detail-content" v-if="anime">
      <div class="image-section">
        <div class="image-wrapper">
          <img v-if="anime.image" :src="anime.image" class="detail-image" />
          <div v-else class="no-image">暂无图片</div>
        </div>
        <label class="upload-label">
          <input type="file" @change="handleFileChange" accept="image/*" />
          <span>更换图片</span>
        </label>
        <div v-if="uploadPreview" class="upload-preview">
          <img :src="uploadPreview" />
        </div>
      </div>
      
      <div class="info-section">
        <div class="info-item">
          <label>名称</label>
          <input v-model="anime.name" />
        </div>
        
        <div class="info-item">
          <label>图片URL</label>
          <input v-model="anime.image" />
        </div>
        
        <div class="info-item">
          <label>简介</label>
          <textarea v-model="anime.description" rows="4"></textarea>
        </div>

        <div class="info-item">
          <label>评价</label>
          <textarea v-model="anime.evaluation" rows="4"></textarea>
        </div>

        <div class="info-item">
          <label>评分</label>
          <input v-model="anime.rating" />
        </div>
        
        <button class="save-btn" @click="handleSave" :disabled="uploading">
          {{ uploading ? '保存中...' : '保存' }}
        </button>
      </div>
    </div>

    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const anime = ref(null)
const uploading = ref(false)
const selectedFile = ref(null)
const uploadPreview = ref('')
const id = route.params.id

const fetchDetail = async () => {
  try {
    const res = await fetch(`http://localhost:2333/api/anime/${id}`)
    const result = await res.json()
    if (result.code === 200) {
      anime.value = result.data
    }
  } catch (e) {
    console.error(e)
  }
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
  if (!selectedFile.value) return anime.value.image
  
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
  return anime.value.image
}

const handleSave = async () => {
  uploading.value = true
  try {
    // 如果选了新图片，先上传
    if (selectedFile.value) {
      const imageUrl = await uploadImage()
      anime.value.image = imageUrl
    }
    
    const res = await fetch(`http://localhost:2333/api/anime/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(anime.value)
    })
    const result = await res.json()
    if (result.code === 200) {
      alert('保存成功')
      selectedFile.value = null
      uploadPreview.value = ''
    } else {
      alert(result.message)
    }
  } catch (e) {
    alert('保存失败')
  } finally {
    uploading.value = false
  }
}

const goBack = () => {
  router.push('/anime')
}

onMounted(fetchDetail)
</script>

<style scoped>
.detail-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.header button {
  padding: 8px 16px;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.header h1 {
  color: #333;
}

.detail-content {
  display: flex;
  gap: 30px;
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.image-section {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.image-wrapper {
  width: 200px;
  height: 280px;
  border-radius: 10px;
  overflow: hidden;
}

.detail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.upload-label {
  display: inline-block;
  padding: 8px 16px;
  background: #2196F3;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.upload-label input {
  display: none;
}

.upload-preview img {
  width: 100px;
  height: 130px;
  object-fit: cover;
  border-radius: 5px;
  border: 2px solid #4CAF50;
}

.info-section {
  flex: 1;
}

.info-item {
  margin-bottom: 20px;
}

.info-item label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.info-item input,
.info-item textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
  font-size: 14px;
}

.info-item input:focus,
.info-item textarea:focus {
  outline: none;
  border-color: #2196F3;
}

.save-btn {
  padding: 12px 30px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 10px;
}

.save-btn:hover {
  background: #45a049;
}

.save-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
