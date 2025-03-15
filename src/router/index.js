// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import AuthPage from '@/views/AuthPage.vue'
import My from '@/views/My.vue'

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
    path: '/my',
    name: 'My',
    component: My,
    meta: { requiresAuth: true } // 需要登录才能访问
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
// import { createRouter, createWebHashHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue'
//
// const routes = [
//   {
//     path: '/home',
//     name: 'HomeView',
//     component: HomeView
//   },
//   {
//     path: '/about',
//     name: 'about',
//     // route level code-splitting
//     // this generates a separate chunk (about.[hash].js) for this route
//     // which is lazy-loaded when the route is visited.
//     component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
//   },// 添加my路由配置
//   {
//     path: '/my',
//     name: 'My',
//     component: () => import('@/views/My.vue'),
//     meta: { requiresAuth: true } // 需要登录的页面
//   }
// ]
//
// const router = createRouter({
//   history: createWebHashHistory(),
//   routes
// })
// // 在路由配置后添加全局前置守卫
// router.beforeEach((to, from, next) => {
//   const isAuthenticated = localStorage.getItem('userInfo');
//
//   // 需要登录的页面且未登录
//   if (to.matched.some(record => record.meta.requiresAuth)) {
//     if (!isAuthenticated) {
//       next({
//         path: '/login',
//         query: { redirect: to.fullPath }
//       });
//     } else {
//       next();
//     }
//   } else {
//     next();
//   }
// });
// export default router
