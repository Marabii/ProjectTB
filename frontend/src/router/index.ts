// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/views/HomePage.vue'
import SellFiles from '@/views/SellFiles.vue'
import AuthPage from '@/views/AuthPage.vue'
import axios from 'axios'
import { serverURL } from '@/utilis/constants'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage,
  },
  {
    path: '/sell-files',
    name: 'sell-files',
    component: SellFiles,
    meta: { requiresAuth: true },
  },
  {
    path: '/auth-page',
    name: 'Authentication page',
    component: AuthPage,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Add navigation guard
router.beforeEach(async (to, from, next) => {
  let isLoggedIn = false
  try {
    // Adjust the endpoint as per your backend implementation
    await axios.get(`${serverURL}/api/protected/verifyUser`, { withCredentials: true })
    isLoggedIn = true
  } catch (error: unknown) {
    console.error('Authentication verification failed:', error)
    isLoggedIn = false
  }

  // Check if the route requires authentication
  if (to.meta.requiresAuth && !isLoggedIn) {
    // Redirect to the login page with the intended route as a query parameter
    next({
      name: 'Authentication page',
      query: { redirect: to.fullPath },
    })
  } else {
    next()
  }
})

export default router
