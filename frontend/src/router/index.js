import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import Home from '../components/Home.vue'
import AnimeList from '../components/AnimeList.vue'
import AnimeDetail from '../components/AnimeDetail.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: Login
    },
    {
      path: '/home',
      name: 'home',
      component: Home,
      meta: { requiresAuth: true }
    },
    {
      path: '/anime',
      name: 'anime-list',
      component: AnimeList,
      meta: { requiresAuth: true }
    },
    {
      path: '/anime/:id',
      name: 'anime-detail',
      component: AnimeDetail,
      meta: { requiresAuth: true }
    }
  ]
})

// 导航守卫
router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    return '/'
  }
  if (to.path === '/' && token) {
    return '/home'
  }
})

export default router
