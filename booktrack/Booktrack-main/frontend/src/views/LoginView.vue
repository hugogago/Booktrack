<template>
  <section class="auth-layout">
    <div class="hero-panel">
      <p class="eyebrow">BookTrack</p>
      <h1>Tu espacio para organizar lecturas, medir progreso y cerrar el año con objetivos claros.</h1>

      <div class="hero-metrics">
        <article>
          <strong>Resumen</strong>
          <span>Lecturas del mes y del año</span>
        </article>
        <article>
          <strong>Biblioteca</strong>
          <span>Leídos, en curso y pendientes</span>
        </article>
        <article>
          <strong>Estadísticas</strong>
          <span>Gráficas por mes y género</span>
        </article>
      </div>
    </div>

    <div class="auth-panel">
      <div class="auth-toggle">
        <button :class="{ active: showLogin }" @click="showLogin = true">Iniciar sesión</button>
        <button :class="{ active: !showLogin }" @click="showLogin = false">Crear cuenta</button>
      </div>

      <form v-if="showLogin" class="auth-form" @submit.prevent="login">
        <h2>Accede a tu biblioteca</h2>
        <label>
          Usuario
          <input v-model="loginForm.username" required placeholder="alvaro" />
        </label>
        <label>
          Contraseña
          <input v-model="loginForm.password" type="password" required placeholder="Tu contraseña" />
        </label>
        <button class="primary-btn" type="submit">Entrar</button>
      </form>

      <form v-else class="auth-form" @submit.prevent="register">
        <h2>Crea tu espacio personal</h2>
        <label>
          Usuario
          <input v-model="registerForm.username" required placeholder="lectora25" />
        </label>
        <label>
          Email
          <input v-model="registerForm.email" type="email" required placeholder="tu@email.com" />
        </label>
        <label>
          Contrasena
          <input v-model="registerForm.password" type="password" required placeholder="Minimo una segura" />
        </label>
        <label>
          Repite la contrasena
          <input v-model="registerForm.confirmPassword" type="password" required placeholder="Repite la contrasena" />
        </label>
        <button class="primary-btn" type="submit">Registrarme</button>
      </form>

      <p v-if="feedback.message" :class="['feedback', feedback.type]">{{ feedback.message }}</p>
    </div>
  </section>
</template>

<script>
import api, { setAuthToken } from '../api'

export default {
  name: 'LoginView',
  data() {
    return {
      showLogin: true,
      loginForm: { username: '', password: '' },
      registerForm: { username: '', email: '', password: '', confirmPassword: '' },
      feedback: { type: '', message: '' }
    }
  },
  methods: {
    async login() {
      try {
        const response = await api.post('/auth/login', this.loginForm)
        await this.handleAuthSuccess(response.data, 'Sesion iniciada correctamente.')
      } catch (error) {
        this.setFeedback('error', this.extractError(error, 'No se pudo iniciar sesion.'))
      }
    },
    async register() {
      try {
        const response = await api.post('/auth/register', this.registerForm)
        await this.handleAuthSuccess(response.data, 'Cuenta creada correctamente.')
      } catch (error) {
        this.setFeedback('error', this.extractError(error, 'No se pudo registrar el usuario.'))
      }
    },
    async handleAuthSuccess(payload, message) {
      localStorage.setItem('token', payload.token)
      localStorage.setItem('user', JSON.stringify({ username: payload.username, email: payload.email }))
      setAuthToken(payload.token)
      this.setFeedback('success', message)
      this.$router.push({ name: 'resumen' })
    },
    setFeedback(type, message) {
      this.feedback = { type, message }
    },
    extractError(error, fallbackMessage) {
      return error?.response?.data?.message || error?.response?.data?.error || fallbackMessage
    }
  }
}
</script>
