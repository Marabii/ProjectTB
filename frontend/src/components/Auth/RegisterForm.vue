<script setup lang="ts">
import { reactive, ref } from 'vue'
import axios from 'axios'
import ErrorMessage from './ErrorMessage.vue'
import { serverURL } from '@/utilis/constants'

const EMAIL_REGEX =
  /^[a-zA-Z0-9_+&*\-]+(?:\.[a-zA-Z0-9_+&*\-]+)*@(?:[a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,7}$/
const PHONE_REGEX = /^\+?[0-9. ()-]{7,25}$/
const NAME_REGEX = /^[\p{L} .'-]+$/u
const PASSWORD_REGEX = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$/

const name = ref('')
const email = ref('')
const password = ref('')
const phoneNumber = ref('')

const errors = reactive({
  name: '',
  email: '',
  password: '',
  phoneNumber: '',
  server: '',
})

const validateName = () => {
  if (
    !name.value ||
    !NAME_REGEX.test(name.value) ||
    name.value.length < 2 ||
    name.value.length > 50
  ) {
    errors.name = 'Name must be 2-50 characters and contain only letters.'
  } else {
    errors.name = ''
  }
}

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

const validatePhoneNumber = () => {
  if (!phoneNumber.value || !PHONE_REGEX.test(phoneNumber.value)) {
    errors.phoneNumber = 'Invalid phone number format.'
  } else {
    errors.phoneNumber = ''
  }
}

const onRegisterSubmit = async () => {
  validateName()
  validateEmail()
  validatePassword()
  validatePhoneNumber()

  if (errors.name || errors.email || errors.password || errors.phoneNumber) {
    return
  }

  try {
    await axios.post(
      `${serverURL}/api/v1/auth/register`,
      {
        name: name.value,
        email: email.value,
        password: password.value,
        phoneNumber: phoneNumber.value,
      },
      { withCredentials: true },
    )

    alert('Registered successfully!')
  } catch (error: unknown) {
    if (error instanceof Error) {
      errors.server = error.message || 'Login failed.'
    } else {
      console.error(error)
      errors.server = 'Something went wrong, login failed'
    }
  }
}
</script>

<!-- src/components/Auth/RegisterForm.vue -->
<template>
  <div class="w-full">
    <h2 class="text-2xl font-semibold mb-4 text-center">Register</h2>
    <form @submit.prevent="onRegisterSubmit" class="space-y-4">
      <!-- Name -->
      <div>
        <label for="register-name" class="block text-gray-700">Name</label>
        <input
          id="register-name"
          type="text"
          v-model="name"
          @blur="validateName"
          class="mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-black"
          placeholder="Enter your name"
        />
        <ErrorMessage :message="errors.name" />
      </div>

      <!-- Email -->
      <div>
        <label for="register-email" class="block text-gray-700">Email</label>
        <input
          id="register-email"
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
        <label for="register-password" class="block text-gray-700">Password</label>
        <input
          id="register-password"
          type="password"
          v-model="password"
          @blur="validatePassword"
          class="mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-black"
          placeholder="Enter your password"
        />
        <ErrorMessage :message="errors.password" />
      </div>

      <!-- Phone Number -->
      <div>
        <label for="register-phone" class="block text-gray-700">Phone Number</label>
        <input
          id="register-phone"
          type="text"
          v-model="phoneNumber"
          @blur="validatePhoneNumber"
          class="mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-black"
          placeholder="Enter your phone number"
        />
        <ErrorMessage :message="errors.phoneNumber" />
      </div>

      <!-- Submit Button -->
      <button
        type="submit"
        class="w-full bg-black text-white py-2 rounded-md hover:bg-gray-800 transition-colors"
      >
        Register
      </button>
    </form>
  </div>
</template>
