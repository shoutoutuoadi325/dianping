import {createRouter, createWebHistory} from 'vue-router'
import AuthPage from '@/views/AuthPage.vue'
import Home from '@/views/home.vue'
import BusinessDetail from "@/views/BusinessDetail.vue";
import UserInfo from "@/views/UserInfo.vue";
import NearbyFood from '@/views/NearbyFood.vue'
import LifeZone from '@/views/LifeZone.vue'
import AddReview from '@/views/AddReview.vue'
import My from '@/views/My.vue'
import PackageDetail from '@/views/PackageDetail.vue'
import OrderConfirmation from '@/views/OrderConfirmation.vue'
import CouponCode from '@/views/CouponCode.vue'
import MyOrders from '@/views/MyOrders.vue'
import MyCoupons from '@/views/MyCoupons.vue'
import NewUserCoupons from '@/views/NewUserCoupons.vue'

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
        meta: {requiresAuth: true} // 需要登录才能访问
    },
    {
        path: '/user-info',
        name: 'UserInfo',
        component: UserInfo,
    },
    {
        path: '/businessDetail/:id',
        name: 'BusinessDetail',
        component: BusinessDetail,
        meta: {requiresAuth: true}
    },
    {
        path: '/nearby-food',
        name: 'NearbyFood',
        component: NearbyFood,
        meta: {requiresAuth: true}
    },
    {
        path: '/life-zone',
        name: 'LifeZone',
        component: LifeZone,
        meta: {requiresAuth: true}
    },
    {
        path: '/add-review',
        name: 'AddReview',
        component: AddReview,
        meta: {requiresAuth: true}
    },
    {
        path: '/my',
        name: 'My',
        component: My,
        meta: {requiresAuth: true}
    },
    {
        path: '/package/:id',
        name: 'PackageDetail',
        component: PackageDetail,
        meta: {requiresAuth: true}
    },
    {
        path: '/order-confirmation/:packageId',
        name: 'OrderConfirmation',
        component: OrderConfirmation,
        meta: {requiresAuth: true}
    },
    {
        path: '/coupon-code/:orderId',
        name: 'CouponCode',
        component: CouponCode,
        meta: {requiresAuth: true}
    },
    {
        path: '/my-orders',
        name: 'MyOrders',
        component: MyOrders,
        meta: {requiresAuth: true}
    },
    {
        path: '/my-coupons',
        name: 'MyCoupons',
        component: MyCoupons,
        meta: {requiresAuth: true}
    },
    {
        path: '/new-user-coupons',
        name: 'NewUserCoupons',
        component: NewUserCoupons,
        meta: {requiresAuth: true}
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