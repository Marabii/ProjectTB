<!-- src/components/Auth/LoginForm.vue -->
<script setup lang="ts">
import { reactive, ref } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'
import ErrorMessage from './ErrorMessage.vue'
import { serverURL } from '@/utilis/constants'

const router = useRouter()
const route = useRoute()

const EMAIL_REGEX =
  /^[a-zA-Z0-9_+&*\-]+(?:\.[a-zA-Z0-9_+&*\-]+)*@(?:[a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,7}$/
const PASSWORD_REGEX = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$/

const email = ref('')
const password = ref('')

const errors = reactive({
  email: '',
  password: '',
  server: '',
})

const validateEmail = () => {
  if (!email.value || !EMAIL_REGEX.test(email.value)) {
    errors.email = 'Invalid email format.'
  } else {
    errors.email = ''
  }
}

const validatePassword = () => {
  if (!password.value || !PASSWORD_REGEX.test(password.value)) {
    errors.password =
      'Password must have 8+ chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char.'
  } else {
    errors.password = ''
  }
}

const onLoginSubmit = async () => {
  validateEmail()
  validatePassword()

  if (errors.email || errors.password) {
    return
  }

  try {
    await axios.post(
      `${serverURL}/api/v1/auth/login`,
      {
        email: email.value,
        password: password.value,
      },
      { withCredentials: true },
    )

    // Retrieve the redirect path from query parameters
    const redirectPath = route.query.redirect as string

    if (redirectPath) {
      router.push(redirectPath)
    } else {
      router.push({ name: 'home' }) // Default route after login
    }

    alert('Logged in successfully!')
  } catch (error: unknown) {
    if (axios.isAxiosError(error) && error.response) {
      // Extract error message from the response
      errors.server = error.response.data.message || 'Login failed.'
    } else {
      console.error(error)
      errors.server = 'Something went wrong, login failed'
    }
  }
}
</script>

<template>
  <div class="w-full">
    <h2 class="text-2xl font-semibold mb-4 text-center">Login</h2>
    <form @submit.prevent="onLoginSubmit" class="space-y-4">
      <!-- Email -->
      <div>
        <label for="login-email" class="block text-gray-700">Email</label>
        <input
          id="login-email"
          type="email"
          v-model="email"
          @blur="validateEmail"
          class="mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-black"
          placeholder="Enter your email"
        />
        <ErrorMessage :message="errors.email" />
      </div>

      <!-- Password -->
      <div>
        <label for="login-password" class="block text-gray-700">Password</label>
        <input
          id="login-password"
          type="password"
          v-model="password"
          @blur="validatePassword"
          class="mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-black"
          placeholder="Enter your password"
        />
        <ErrorMessage :message="errors.password" />
      </div>

      <!-- Server Error -->
      <ErrorMessage :message="errors.server" />

      <!-- Submit Button -->
      <button
        type="submit"
        class="w-full bg-black text-white py-2 rounded-md hover:bg-gray-800 transition-colors"
      >
        Login
      </button>
    </form>

    <!-- Google Sign-In -->
    <div class="mt-4">
      <a
        href="https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=http://localhost:8080/google/callback&response_type=code&client_id=1028629889843-4vf1i5nfjis4o4ht1posamrrrv5l106p.apps.googleusercontent.com&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid&access_type=offline"
        class="w-full flex items-center justify-center border border-gray-300 py-2 rounded-md hover:bg-gray-100 transition-colors"
      >
        <svg class="w-5 h-5 mr-2" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
          <path
            fill="#FFC107"
            d="M43.6 20.2H42V20H24v8h11.3c-1.7 4.5-6.1 12-16.3 12-9.8 0-17.8-8-17.8-17.8S14.2 12.4 24 12.4c4.9 0 8.3 2.1 10.2 3.8l7-6c-3-2.8-6.9-4.5-13-4.5-9.8 0-17.8 8-17.8 17.8S14.2 42 24 42c11.5 0 17.5-7.9 17.5-17.4 0-1.2-.1-2.1-.3-3z"
          />
        </svg>
        Sign in with Google
      </a>
    </div>
  </div>
</template>
