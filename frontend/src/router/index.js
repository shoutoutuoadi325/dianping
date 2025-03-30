import { createRouter, createWebHistory } from 'vue-router'
import AuthPage from '@/views/AuthPage.vue'
import Home from '@/views/home.vue'

const routes = [
  {
    path: '/',
    redirect: '/login' // 默认跳转到登录页
  },
  {
    path: '/login',
    name: 'AuthPage',
    component: AuthPage
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/nearby-food',
    component: () => import('@/views/NearbyFood.vue')
  },
  {
    path: '/add-review',
    component: () => import('@/views/AddReview.vue')
  },
  {
    path: '/life-zone',
    component: () => import('@/views/LifeZone.vue')
  },
  {
    path: '/user-info',
    component: () => import('@/views/UserInfo.vue')
  },{
    path: '/merchants/:id',
    component: () => import('../views/MerchantDetail.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：拦截未登录访问
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('userInfo')
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login') // 未登录则跳转登录页
  } else {
    next()
  }
})

export default router