<template>
  <div class="detail-container" :style="backgroundStyle">
    <!-- 左箭头 -->
    <button type="button" v-if="prevAnime" class="nav-arrow left-arrow" @click="goToAnime(prevAnime.id)">
      ‹
    </button>

    <!-- 右箭头 -->
    <button type="button" v-if="nextAnime" class="nav-arrow right-arrow" @click="goToAnime(nextAnime.id)">
      ›
    </button>

    <div class="content-wrapper" :key="currentId">
      <div class="header">
        <button type="button" @click="goBack">← 返回</button>
        <h1>动漫详情</h1>
        <button type="button" v-if="!isEditing" @click="startEdit" class="edit-btn">编辑</button>
      </div>

      <div class="detail-content" v-if="anime">
        <div class="image-section">
          <div class="image-wrapper">
            <img v-if="anime.image" :src="anime.image" class="detail-image"/>
            <div v-else class="no-image">暂无图片</div>
          </div>
          <label v-if="isEditing" class="upload-label">
            <input type="file" @change="handleFileChange" accept="image/*"/>
            <span>更换封面</span>
          </label>
          <div v-if="uploadPreview" class="upload-preview">
            <img :src="uploadPreview"/>
          </div>

          <!-- 背景图上传 -->
          <label v-if="isEditing" class="upload-label">
            <input type="file" @change="handleBgChange" accept="image/*"/>
            <span>更换背景</span>
          </label>
          <div v-if="bgPreview" class="upload-preview">
            <img :src="bgPreview"/>
          </div>
        </div>

        <div class="info-section">
          <!-- 展示模式 -->
          <template v-if="!isEditing">
            <div class="info-item">
              <label>名称</label>
              <div class="display-value">{{ anime.name }}</div>
            </div>

            <div class="info-item" v-if="anime.description">
              <label>简介</label>
              <div class="display-value">{{ anime.description }}</div>
            </div>

            <div class="info-item" v-if="anime.evaluation">
              <label>评价</label>
              <div class="display-value">{{ anime.evaluation }}</div>
            </div>

            <div class="info-item" v-if="anime.rating">
              <label>评分</label>
              <div class="display-value">{{ anime.rating }}</div>
            </div>
          </template>

          <!-- 编辑模式 -->
          <template v-else>
            <div class="info-item">
              <label>名称</label>
              <input v-model="editForm.name"/>
            </div>

            <div class="info-item">
              <label>简介</label>
              <textarea v-model="editForm.description" rows="4"></textarea>
            </div>

            <div class="info-item">
              <label>评价</label>
              <textarea v-model="editForm.evaluation" rows="4"></textarea>
            </div>

            <div class="info-item">
              <label>评分</label>
              <input v-model="editForm.rating"/>
            </div>

            <div class="button-group">
              <button type="button" class="save-btn" @click="handleSave" :disabled="uploading">
                {{ uploading ? '保存中...' : '保存' }}
              </button>
              <button type="button" class="cancel-btn" @click="cancelEdit">取消</button>
              <button type="button" class="delete-btn" @click="handleDelete">删除</button>
            </div>
          </template>
        </div>
      </div>

      <div v-else class="loading">加载中...</div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted, watch} from 'vue'
import {useRouter, useRoute} from 'vue-router'

const router = useRouter()
const route = useRoute()

const anime = ref(null)
const prevAnime = ref(null)
const nextAnime = ref(null)
const isEditing = ref(false)

// 当前动漫ID（响应式）
const currentId = computed(() => route.params.id)

// 监听路由变化
watch(() => route.params.id, (newId, oldId) => {
  if (newId && oldId && parseInt(newId) !== parseInt(oldId)) {
    fetchDetail()
  }
})

const editForm = reactive({
  name: '',
  description: '',
  evaluation: '',
  rating: ''
})
const uploading = ref(false)
const selectedFile = ref(null)
const uploadPreview = ref('')
const bgFile = ref(null)
const bgPreview = ref('')

// 背景图样式
const backgroundStyle = computed(() => {
  const bg = anime.value?.bgImg
  if (bg) {
    return {
      backgroundImage: `linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.7)), url(${bg})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
      minHeight: '100vh'
    }
  }
  return {}
})

const fetchDetail = async () => {
  try {
    const res = await fetch(`http://localhost:2333/api/anime/${currentId.value}`)
    const result = await res.json()
    if (result.code === 200) {
      anime.value = result.data
      // 获取相邻动漫
      fetchNeighborAnime()
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchNeighborAnime = async () => {
  try {
    const res = await fetch(`http://localhost:2333/api/anime/list?pageNum=1&pageSize=100`)
    const result = await res.json()
    if (result.code === 200) {
      const list = result.data.records
      const currentIndex = list.findIndex(item => item.id === parseInt(currentId.value))

      // 重置相邻动漫
      prevAnime.value = null
      nextAnime.value = null

      if (currentIndex > 0) {
        prevAnime.value = list[currentIndex - 1]
      }
      if (currentIndex >= 0 && currentIndex < list.length - 1) {
        nextAnime.value = list[currentIndex + 1]
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const goToAnime = (animeId) => {
  console.log('goToAnime:', animeId)
  router.push(`/anime/${animeId}`)
}

const startEdit = () => {
  // 复制当前数据到编辑表单
  editForm.name = anime.value.name || ''
  editForm.description = anime.value.description || ''
  editForm.evaluation = anime.value.evaluation || ''
  editForm.rating = anime.value.rating || ''
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
  selectedFile.value = null
  uploadPreview.value = ''
  bgFile.value = null
  bgPreview.value = ''
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

const handleBgChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    bgFile.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      bgPreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const uploadImage = async (file) => {
  if (!file) return null

  const formData = new FormData()
  formData.append('file', file)

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
  return null
}

const handleSave = async () => {
  uploading.value = true
  try {
    // 如果选了新封面图片，先上传
    let imageUrl = anime.value.image
    if (selectedFile.value) {
      imageUrl = await uploadImage(selectedFile.value) || anime.value.image
    }

    // 如果选了新背景图片，先上传
    let bgImageUrl = anime.value.bgImg
    if (bgFile.value) {
      bgImageUrl = await uploadImage(bgFile.value) || anime.value.bgImg
    }

    const updateData = {
      ...editForm,
      image: imageUrl,
      bgImg: bgImageUrl
    }

    const res = await fetch(`http://localhost:2333/api/anime/${currentId.value}`, {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(updateData)
    })
    const result = await res.json()
    if (result.code === 200) {
      alert('保存成功')
      // 更新显示数据
      anime.value = {...editForm, image: imageUrl, bgImg: bgImageUrl}
      isEditing.value = false
      selectedFile.value = null
      uploadPreview.value = ''
      bgFile.value = null
      bgPreview.value = ''
    } else {
      alert(result.message)
    }
  } catch (e) {
    alert('保存失败')
  } finally {
    uploading.value = false
  }
}

const handleDelete = async () => {
  if (!confirm('确定要删除这部动漫吗？')) return

  try {
    const res = await fetch(`http://localhost:2333/api/anime/${currentId.value}`, {
      method: 'DELETE'
    })
    const result = await res.json()
    if (result.code === 200) {
      alert('删除成功')
      router.push('/anime')
    } else {
      alert(result.message)
    }
  } catch (e) {
    alert('删除失败')
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
  min-height: 100vh;
  box-sizing: border-box;
  width: 100%;
}

/* 导航箭头 */
.nav-arrow {
  position: fixed;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 80px;
  background: rgba(0, 0, 0, 0.3);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 40px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
  z-index: 100;
}

.nav-arrow:hover {
  background: rgba(0, 0, 0, 0.6);
}

.left-arrow {
  left: 20px;
}

.right-arrow {
  right: 20px;
}

.content-wrapper {
  animation: fadeIn 0.5s ease-out forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.content-wrapper.slide-left {
  animation: fadeIn 0.5s ease-out forwards;
}

.content-wrapper.slide-right {
  animation: fadeIn 0.5s ease-out forwards;
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

.header .edit-btn {
  background: #FF9800;
  margin-left: auto;
}

.header h1 {
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.detail-content {
  display: flex;
  gap: 30px;
  background: rgba(255, 255, 255, 0.95);
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 70%;
  margin: 0 auto;
  min-height: 800px;
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

.display-value {
  padding: 10px;
  background: #f9f9f9;
  border-radius: 5px;
  min-height: 20px;
  color: #333;
  line-height: 1.5;
  white-space: pre-wrap;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 10px;
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

.cancel-btn {
  padding: 12px 30px;
  background: #9E9E9E;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.cancel-btn:hover {
  background: #757575;
}

.delete-btn {
  padding: 12px 30px;
  background: #F44336;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.delete-btn:hover {
  background: #D32F2F;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
