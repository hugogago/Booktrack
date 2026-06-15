<template>
  <section class="app-layout">
    <aside class="side-panel">
      <div>
        <p class="eyebrow">Panel personal</p>
        <h2>{{ user.username }}</h2>
        <p class="side-copy">{{ user.email }}</p>
      </div>

      <nav class="side-nav">
        <button :class="{ active: $route.name === 'resumen' }" @click="$router.push({ name: 'resumen' })">Resumen</button>
        <button :class="{ active: $route.name === 'biblioteca' }" @click="$router.push({ name: 'biblioteca' })">Biblioteca</button>
        <button :class="{ active: $route.name === 'estadisticas' }" @click="$router.push({ name: 'estadisticas' })">Estadisticas</button>
        <button :class="{ active: $route.name === 'perfil' }" @click="$router.push({ name: 'perfil' })">Perfil</button>
      </nav>

      <div class="goal-card">
        <span>Objetivo anual</span>
        <strong>{{ summary.currentYearReadCount }} / {{ summary.annualGoal || 0 }}</strong>
        <div class="progress-track compact">
          <div class="progress-fill" :style="{ width: progressWidth }"></div>
        </div>
      </div>

      <button class="ghost-btn" @click="logout">Cerrar sesion</button>
    </aside>

    <main class="content-panel">
      <header class="content-header">
        <div>
          <p class="eyebrow">BookTrack Workspace</p>
          <h1>{{ viewTitles[$route.name] }}</h1>
        </div>
        <button v-if="$route.name === 'biblioteca'" class="primary-btn" @click="$router.push({ name: 'biblioteca', query: { nuevo: '1' } })">Nuevo libro</button>
      </header>

      <p v-if="feedback.message" :class="['feedback', feedback.type]">{{ feedback.message }}</p>

      <RouterView
        :summary="summary"
        :statistics="statistics"
        :books="books"
        :feedback="feedback"
        @update-summary="fetchSummary"
        @update-books="fetchBooks"
        @update-statistics="fetchStatistics"
        @update-all="bootstrapAuthenticatedArea"
        @set-feedback="setFeedback"
      />
    </main>
  </section>
</template>

<script>
import api, { setAuthToken } from '../api'

function parseStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
}

export default {
  name: 'AppLayout',
  data() {
    return {
      user: parseStoredUser(),
      summary: {
        currentMonthReadCount: 0,
        currentYearReadCount: 0,
        inProgressCount: 0,
        pendingCount: 0,
        totalBooks: 0,
        annualGoal: 0,
        annualGoalProgress: 0
      },
      statistics: { monthlyReads: [], genreBreakdown: [] },
      books: [],
      feedback: { type: '', message: '' }
    }
  },
  computed: {
    viewTitles() {
      return {
        resumen: 'Resumen personal',
        biblioteca: 'Biblioteca y buscador',
        estadisticas: 'Estadisticas de lectura',
        perfil: 'Perfil y objetivos'
      }
    },
    progressWidth() {
      return `${Math.min(100, Math.max(0, this.summary.annualGoalProgress || 0))}%`
    }
  },
  async created() {
    const token = localStorage.getItem('token')
    if (token) {
      setAuthToken(token)
      await this.bootstrapAuthenticatedArea()
    }
  },
  methods: {
    async bootstrapAuthenticatedArea() {
      try {
        await Promise.all([this.fetchSummary(), this.fetchStatistics(), this.fetchBooks()])
      } catch (error) {
        this.logout()
        this.setFeedback('error', 'La sesion no es valida.')
      }
    },
    async fetchSummary() {
      const response = await api.get('/dashboard/summary')
      this.summary = response.data
    },
    async fetchStatistics() {
      const response = await api.get('/dashboard/statistics')
      this.statistics = response.data
    },
    async fetchBooks(filters = {}) {
      const params = {}
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== '' && value !== null && value !== undefined) {
          params[key] = value
        }
      })
      const response = await api.get('/books', { params })
      this.books = response.data
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      setAuthToken(null)
      this.$router.push({ name: 'login' })
    },
    setFeedback(type, message) {
      this.feedback = { type, message }
    }
  }
}
</script>
