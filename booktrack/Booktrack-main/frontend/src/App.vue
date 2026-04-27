<template>
  <div class="shell">
    <section v-if="!token" class="auth-layout">
      <div class="hero-panel">
        <p class="eyebrow">BookTrack</p>
        <h1>Tu espacio para organizar lecturas, medir progreso y cerrar el ano con objetivos claros.</h1>
        <p class="hero-copy">
          El anteproyecto pide una plataforma con resumen personal, biblioteca, detalle de libro,
          buscador y estadisticas. Esta version ya articula ese flujo completo.
        </p>

        <div class="hero-metrics">
          <article>
            <strong>Resumen</strong>
            <span>Lecturas del mes y del ano</span>
          </article>
          <article>
            <strong>Biblioteca</strong>
            <span>Leidos, en curso y pendientes</span>
          </article>
          <article>
            <strong>Estadisticas</strong>
            <span>Graficas por mes y genero</span>
          </article>
        </div>
      </div>

      <div class="auth-panel">
        <div class="auth-toggle">
          <button :class="{ active: showLogin }" @click="showLogin = true">Iniciar sesion</button>
          <button :class="{ active: !showLogin }" @click="showLogin = false">Crear cuenta</button>
        </div>

        <form v-if="showLogin" class="auth-form" @submit.prevent="login">
          <h2>Accede a tu biblioteca</h2>
          <label>
            Usuario
            <input v-model="loginForm.username" required placeholder="alvaro" />
          </label>
          <label>
            Contrasena
            <input v-model="loginForm.password" type="password" required placeholder="Tu contrasena" />
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

    <section v-else class="app-layout">
      <aside class="side-panel">
        <div>
          <p class="eyebrow">Panel personal</p>
          <h2>{{ user.username }}</h2>
          <p class="side-copy">{{ user.email }}</p>
        </div>

        <nav class="side-nav">
          <button :class="{ active: currentView === 'summary' }" @click="currentView = 'summary'">Resumen</button>
          <button :class="{ active: currentView === 'library' }" @click="currentView = 'library'">Biblioteca</button>
          <button :class="{ active: currentView === 'statistics' }" @click="currentView = 'statistics'">Estadisticas</button>
          <button :class="{ active: currentView === 'profile' }" @click="currentView = 'profile'">Perfil</button>
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
            <h1>{{ viewTitles[currentView] }}</h1>
          </div>
          <button v-if="currentView === 'library'" class="primary-btn" @click="prepareNewBook">Nuevo libro</button>
        </header>

        <p v-if="feedback.message" :class="['feedback', feedback.type]">{{ feedback.message }}</p>

        <section v-if="currentView === 'summary'" class="view-grid summary-grid">
          <article class="metric-card accent">
            <span>Leidos este mes</span>
            <strong>{{ summary.currentMonthReadCount }}</strong>
            <small>Libros terminados en el mes actual</small>
          </article>
          <article class="metric-card">
            <span>Total anual</span>
            <strong>{{ summary.currentYearReadCount }}</strong>
            <small>Libros finalizados este ano</small>
          </article>
          <article class="metric-card">
            <span>En curso</span>
            <strong>{{ summary.inProgressCount }}</strong>
            <small>Lecturas abiertas ahora mismo</small>
          </article>
          <article class="metric-card">
            <span>Pendientes</span>
            <strong>{{ summary.pendingCount }}</strong>
            <small>Titulos en espera dentro de tu biblioteca</small>
          </article>

          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Progreso anual</h3>
                <p>Control del objetivo lector definido en tu perfil.</p>
              </div>
              <strong>{{ Math.round(summary.annualGoalProgress || 0) }}%</strong>
            </div>

            <div class="progress-track">
              <div class="progress-fill" :style="{ width: progressWidth }"></div>
            </div>

            <p class="progress-copy">
              Llevas {{ summary.currentYearReadCount }} libros completados de un objetivo de
              {{ summary.annualGoal || 0 }}.
            </p>
          </article>

          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Lecturas activas</h3>
                <p>Resumen rapido del estado actual de tu biblioteca.</p>
              </div>
            </div>

            <div v-if="inProgressBooks.length" class="book-strip">
              <article v-for="book in inProgressBooks" :key="book.id" class="book-chip" @click="openBook(book)">
                <span class="pill amber">En curso</span>
                <strong>{{ book.title }}</strong>
                <small>{{ book.author }}</small>
              </article>
            </div>
            <p v-else class="empty-state">Todavia no tienes lecturas en curso.</p>
          </article>
        </section>

        <section v-if="currentView === 'library'" class="library-layout">
          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Buscador avanzado</h3>
                <p>Filtra por titulo, autor, genero, editorial o estado.</p>
              </div>
            </div>

            <form class="filters-grid" @submit.prevent="fetchBooks">
              <input v-model="filters.search" placeholder="Buscar por titulo o autor" />
              <select v-model="filters.status">
                <option value="">Todos los estados</option>
                <option value="PENDING">Pendiente</option>
                <option value="IN_PROGRESS">En curso</option>
                <option value="READ">Leido</option>
              </select>
              <input v-model="filters.author" placeholder="Autor" />
              <input v-model="filters.genre" placeholder="Genero" />
              <input v-model="filters.publisher" placeholder="Editorial" />
              <div class="filter-actions">
                <button class="primary-btn" type="submit">Aplicar</button>
                <button class="ghost-btn" type="button" @click="resetFilters">Limpiar</button>
              </div>
            </form>
          </article>

          <article class="panel">
            <div class="panel-head">
              <div>
                <h3>{{ editingBookId ? 'Editar libro' : 'Anadir libro' }}</h3>
                <p>Completa la ficha con los datos necesarios del libro.</p>
              </div>
            </div>

            <form class="book-form" @submit.prevent="submitBook">
              <label>
                Titulo
                <input v-model="bookForm.title" required placeholder="El nombre del viento" />
              </label>
              <label>
                Autor
                <input v-model="bookForm.author" required placeholder="Patrick Rothfuss" />
              </label>
              <label>
                Genero
                <input v-model="bookForm.genre" placeholder="Fantasia" />
              </label>
              <label>
                Editorial
                <input v-model="bookForm.publisher" placeholder="Plaza y Janes" />
              </label>
              <label>
                Paginas
                <input v-model.number="bookForm.totalPages" type="number" min="0" placeholder="0" />
              </label>
              <label>
                Nota personal
                <input v-model.number="bookForm.personalRating" type="number" min="0" max="10" placeholder="0-10" />
              </label>
              <label>
                Estado
                <select v-model="bookForm.status">
                  <option value="PENDING">Pendiente</option>
                  <option value="IN_PROGRESS">En curso</option>
                  <option value="READ">Leido</option>
                </select>
              </label>
              <label>
                Inicio
                <input v-model="bookForm.startedDate" type="date" />
              </label>
              <label>
                Fin
                <input v-model="bookForm.finishedDate" type="date" />
              </label>
              <label class="full-width">
                Comentarios
                <textarea v-model="bookForm.comments" rows="4" placeholder="Valoracion, sensaciones, seguimiento..."></textarea>
              </label>
              <div class="form-actions full-width">
                <button class="primary-btn" type="submit">{{ editingBookId ? 'Guardar cambios' : 'Crear libro' }}</button>
                <button class="ghost-btn" type="button" @click="prepareNewBook">Cancelar</button>
              </div>
            </form>
          </article>

          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Biblioteca</h3>
                <p>{{ books.length }} libros encontrados en tu espacio personal.</p>
              </div>
            </div>

            <div v-if="books.length" class="library-cards">
              <article v-for="book in books" :key="book.id" class="library-card">
                <div class="library-card-head">
                  <div>
                    <span :class="['pill', statusClass(book.status)]">{{ statusLabel(book.status) }}</span>
                    <h4>{{ book.title }}</h4>
                    <p>{{ book.author }}</p>
                  </div>
                  <button class="link-btn" @click="openBook(book)">Ver detalle</button>
                </div>

                <dl>
                  <div>
                    <dt>Genero</dt>
                    <dd>{{ book.genre || 'Sin definir' }}</dd>
                  </div>
                  <div>
                    <dt>Editorial</dt>
                    <dd>{{ book.publisher || 'Sin definir' }}</dd>
                  </div>
                  <div>
                    <dt>Paginas</dt>
                    <dd>{{ book.totalPages || 0 }}</dd>
                  </div>
                  <div>
                    <dt>Nota</dt>
                    <dd>{{ book.personalRating ?? '-' }}</dd>
                  </div>
                </dl>

                <div class="card-actions">
                  <button class="ghost-btn" @click="editBook(book)">Editar</button>
                  <button class="ghost-btn" @click="markAsRead(book)">Marcar leido</button>
                  <button class="danger-btn" @click="deleteBook(book)">Eliminar</button>
                </div>
              </article>
            </div>
            <p v-else class="empty-state">No hay libros que coincidan con los filtros actuales.</p>
          </article>

          <article class="panel detail-panel">
            <div class="panel-head">
              <div>
                <h3>Detalle del libro</h3>
                <p>Ficha completa con informacion adicional.</p>
              </div>
            </div>

            <div v-if="selectedBook" class="detail-content">
              <span :class="['pill', statusClass(selectedBook.status)]">{{ statusLabel(selectedBook.status) }}</span>
              <h4>{{ selectedBook.title }}</h4>
              <p class="detail-author">{{ selectedBook.author }}</p>

              <ul class="detail-list">
                <li>Genero: {{ selectedBook.genre || 'Sin definir' }}</li>
                <li>Editorial: {{ selectedBook.publisher || 'Sin definir' }}</li>
                <li>Paginas: {{ selectedBook.totalPages || 0 }}</li>
                <li>Nota personal: {{ selectedBook.personalRating ?? '-' }}</li>
                <li>Inicio: {{ formatDate(selectedBook.startedDate) }}</li>
                <li>Fin: {{ formatDate(selectedBook.finishedDate) }}</li>
              </ul>

              <p class="detail-comments">{{ selectedBook.comments || 'Sin comentarios todavia.' }}</p>
            </div>
            <p v-else class="empty-state">Selecciona un libro para ver su ficha completa.</p>
          </article>
        </section>

        <section v-if="currentView === 'statistics'" class="view-grid stats-grid">
          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Libros leidos por mes</h3>
                <p>Grafica anual segun la fecha de finalizacion registrada.</p>
              </div>
            </div>

            <div class="chart-bars">
              <div v-for="point in statistics.monthlyReads" :key="point.label" class="bar-column">
                <div class="bar-value">{{ point.value }}</div>
                <div class="bar-track">
                  <div class="bar-fill" :style="{ height: monthlyBarHeight(point.value) }"></div>
                </div>
                <span>{{ point.label }}</span>
              </div>
            </div>
          </article>

          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Generos mas leidos</h3>
                <p>Distribucion porcentual sobre libros marcados como leidos.</p>
              </div>
            </div>

            <div v-if="statistics.genreBreakdown.length" class="genre-list">
              <article v-for="genre in statistics.genreBreakdown" :key="genre.genre" class="genre-row">
                <div class="genre-copy">
                  <strong>{{ genre.genre }}</strong>
                  <span>{{ genre.count }} libros</span>
                </div>
                <div class="progress-track compact">
                  <div class="progress-fill warm" :style="{ width: `${genre.percentage}%` }"></div>
                </div>
                <strong>{{ Math.round(genre.percentage) }}%</strong>
              </article>
            </div>
            <p v-else class="empty-state">Todavia no hay suficientes libros leidos con genero definido.</p>
          </article>
        </section>

        <section v-if="currentView === 'profile'" class="view-grid profile-grid">
          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Datos personales</h3>
                <p>Informacion basica del usuario autenticado.</p>
              </div>
            </div>

            <div class="profile-summary">
              <div>
                <span>Usuario</span>
                <strong>{{ user.username }}</strong>
              </div>
              <div>
                <span>Email</span>
                <strong>{{ user.email }}</strong>
              </div>
              <div>
                <span>Total en biblioteca</span>
                <strong>{{ summary.totalBooks }}</strong>
              </div>
            </div>
          </article>

          <article class="panel wide">
            <div class="panel-head">
              <div>
                <h3>Objetivo lector anual</h3>
                <p>Define cuantos libros quieres completar este ano.</p>
              </div>
            </div>

            <form class="goal-form" @submit.prevent="saveGoal">
              <label>
                Objetivo anual
                <input v-model.number="summary.annualGoal" type="number" min="0" required />
              </label>
              <button class="primary-btn" type="submit">Guardar objetivo</button>
            </form>
          </article>
        </section>
      </main>
    </section>
  </div>
</template>

<script>
import api, { setAuthToken } from './api'

function createEmptyBookForm() {
  return {
    title: '',
    author: '',
    genre: '',
    publisher: '',
    totalPages: 0,
    personalRating: null,
    comments: '',
    status: 'PENDING',
    startedDate: '',
    finishedDate: ''
  }
}

function parseStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
}

export default {
  data() {
    return {
      showLogin: true,
      token: localStorage.getItem('token') || '',
      user: parseStoredUser(),
      currentView: 'summary',
      loginForm: { username: '', password: '' },
      registerForm: { username: '', email: '', password: '', confirmPassword: '' },
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
      filters: { search: '', status: '', author: '', genre: '', publisher: '' },
      bookForm: createEmptyBookForm(),
      selectedBook: null,
      editingBookId: null,
      feedback: { type: '', message: '' }
    }
  },
  computed: {
    viewTitles() {
      return {
        summary: 'Resumen personal',
        library: 'Biblioteca y buscador',
        statistics: 'Estadisticas de lectura',
        profile: 'Perfil y objetivos'
      }
    },
    progressWidth() {
      return `${Math.min(100, Math.max(0, this.summary.annualGoalProgress || 0))}%`
    },
    inProgressBooks() {
      return this.books.filter((book) => book.status === 'IN_PROGRESS').slice(0, 4)
    },
    maxMonthlyReadValue() {
      const values = this.statistics.monthlyReads.map((point) => point.value)
      return Math.max(1, ...values)
    }
  },
  async created() {
    if (this.token) {
      setAuthToken(this.token)
      await this.bootstrapAuthenticatedArea()
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
      this.token = payload.token
      this.user = { username: payload.username, email: payload.email }
      localStorage.setItem('token', payload.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      setAuthToken(payload.token)
      this.setFeedback('success', message)
      this.currentView = 'summary'
      await this.bootstrapAuthenticatedArea()
    },
    async bootstrapAuthenticatedArea() {
      try {
        await Promise.all([this.fetchSummary(), this.fetchStatistics(), this.fetchBooks()])
      } catch (error) {
        this.logout()
        this.setFeedback('error', this.extractError(error, 'La sesion no es valida.'))
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
    async fetchBooks() {
      const params = {}
      Object.entries(this.filters).forEach(([key, value]) => {
        if (value !== '' && value !== null && value !== undefined) {
          params[key] = value
        }
      })

      const response = await api.get('/books', { params })
      this.books = response.data

      if (this.selectedBook) {
        this.selectedBook = this.books.find((book) => book.id === this.selectedBook.id) || null
      }
    },
    async submitBook() {
      const payload = {
        ...this.bookForm,
        genre: this.normalizeEmpty(this.bookForm.genre),
        publisher: this.normalizeEmpty(this.bookForm.publisher),
        comments: this.normalizeEmpty(this.bookForm.comments),
        personalRating: this.bookForm.personalRating === '' ? null : this.bookForm.personalRating,
        startedDate: this.normalizeEmpty(this.bookForm.startedDate),
        finishedDate: this.normalizeEmpty(this.bookForm.finishedDate)
      }

      try {
        if (this.editingBookId) {
          await api.put(`/books/${this.editingBookId}`, payload)
          this.setFeedback('success', 'Libro actualizado correctamente.')
        } else {
          await api.post('/books', payload)
          this.setFeedback('success', 'Libro anadido a la biblioteca.')
        }

        this.prepareNewBook()
        await Promise.all([this.fetchBooks(), this.fetchSummary(), this.fetchStatistics()])
      } catch (error) {
        this.setFeedback('error', this.extractError(error, 'No se pudo guardar el libro.'))
      }
    },
    prepareNewBook() {
      this.editingBookId = null
      this.bookForm = createEmptyBookForm()
    },
    editBook(book) {
      this.currentView = 'library'
      this.editingBookId = book.id
      this.bookForm = {
        title: book.title,
        author: book.author,
        genre: book.genre || '',
        publisher: book.publisher || '',
        totalPages: book.totalPages || 0,
        personalRating: book.personalRating,
        comments: book.comments || '',
        status: book.status,
        startedDate: book.startedDate || '',
        finishedDate: book.finishedDate || ''
      }
      this.openBook(book)
    },
    async markAsRead(book) {
      try {
        await api.put(`/books/${book.id}/status`, null, { params: { status: 'READ' } })
        this.setFeedback('success', 'Libro marcado como leido.')
        await Promise.all([this.fetchBooks(), this.fetchSummary(), this.fetchStatistics()])
      } catch (error) {
        this.setFeedback('error', this.extractError(error, 'No se pudo actualizar el estado del libro.'))
      }
    },
    async deleteBook(book) {
      if (!window.confirm(`Eliminar "${book.title}" de la biblioteca?`)) {
        return
      }

      try {
        await api.delete(`/books/${book.id}`)
        if (this.selectedBook?.id === book.id) {
          this.selectedBook = null
        }
        this.setFeedback('success', 'Libro eliminado correctamente.')
        await Promise.all([this.fetchBooks(), this.fetchSummary(), this.fetchStatistics()])
      } catch (error) {
        this.setFeedback('error', this.extractError(error, 'No se pudo eliminar el libro.'))
      }
    },
    openBook(book) {
      this.selectedBook = book
    },
    async saveGoal() {
      try {
        const response = await api.put('/dashboard/goal', { annualGoal: this.summary.annualGoal })
        this.summary.annualGoal = response.data.annualGoal
        await this.fetchSummary()
        this.setFeedback('success', 'Objetivo anual actualizado.')
      } catch (error) {
        this.setFeedback('error', this.extractError(error, 'No se pudo guardar el objetivo anual.'))
      }
    },
    resetFilters() {
      this.filters = { search: '', status: '', author: '', genre: '', publisher: '' }
      this.fetchBooks()
    },
    logout() {
      this.token = ''
      this.user = {}
      this.books = []
      this.statistics = { monthlyReads: [], genreBreakdown: [] }
      this.summary = {
        currentMonthReadCount: 0,
        currentYearReadCount: 0,
        inProgressCount: 0,
        pendingCount: 0,
        totalBooks: 0,
        annualGoal: 0,
        annualGoalProgress: 0
      }
      this.selectedBook = null
      this.prepareNewBook()
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      setAuthToken(null)
      this.setFeedback('success', 'Sesion cerrada.')
    },
    setFeedback(type, message) {
      this.feedback = { type, message }
    },
    extractError(error, fallbackMessage) {
      return error?.response?.data?.message || error?.response?.data?.error || fallbackMessage
    },
    normalizeEmpty(value) {
      return value === '' ? null : value
    },
    statusLabel(status) {
      return {
        PENDING: 'Pendiente',
        IN_PROGRESS: 'En curso',
        READ: 'Leido'
      }[status] || status
    },
    statusClass(status) {
      return {
        PENDING: 'slate',
        IN_PROGRESS: 'amber',
        READ: 'green'
      }[status] || 'slate'
    },
    formatDate(value) {
      if (!value) {
        return 'Sin registrar'
      }

      return new Date(`${value}T00:00:00`).toLocaleDateString('es-ES')
    },
    monthlyBarHeight(value) {
      if (value === 0) {
        return '0%'
      }

      return `${Math.max(8, (value / this.maxMonthlyReadValue) * 100)}%`
    }
  }
}
</script>

<style>
:root {
  --ink: #1e2430;
  --muted: #6a7280;
  --paper: #f8f4eb;
  --panel: rgba(255, 255, 255, 0.86);
  --border: rgba(30, 36, 48, 0.08);
  --accent: #ba4a00;
  --accent-soft: #f4c9a8;
  --deep: #12343b;
  --sea: #dce9e2;
  --danger: #b42318;
  --shadow: 0 24px 60px rgba(22, 34, 51, 0.14);
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: "Segoe UI", "Trebuchet MS", sans-serif;
  color: var(--ink);
  background:
    radial-gradient(circle at top left, rgba(241, 184, 126, 0.32), transparent 32%),
    radial-gradient(circle at bottom right, rgba(18, 52, 59, 0.18), transparent 28%),
    linear-gradient(135deg, #f6f0e3 0%, #eef2eb 100%);
}

button,
input,
select,
textarea {
  font: inherit;
}

.shell {
  min-height: 100vh;
  padding: 24px;
}

.auth-layout,
.app-layout {
  display: grid;
  gap: 24px;
}

.auth-layout {
  grid-template-columns: 1.15fr 0.85fr;
  align-items: stretch;
}

.hero-panel,
.auth-panel,
.side-panel,
.content-panel,
.panel,
.metric-card {
  background: var(--panel);
  backdrop-filter: blur(18px);
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
}

.hero-panel,
.auth-panel,
.side-panel,
.content-panel {
  border-radius: 28px;
}

.hero-panel {
  padding: 40px;
  background:
    linear-gradient(180deg, rgba(18, 52, 59, 0.9), rgba(18, 52, 59, 0.72)),
    linear-gradient(135deg, #0f2f34, #47665e);
  color: #f7f1e8;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hero-panel h1,
.content-header h1,
.panel h3,
.metric-card strong,
.side-panel h2,
.auth-form h2 {
  font-family: Georgia, "Times New Roman", serif;
}

.hero-panel h1 {
  font-size: clamp(2.5rem, 5vw, 4.2rem);
  line-height: 1.05;
  margin: 12px 0 18px;
  max-width: 9ch;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.18em;
  font-size: 0.78rem;
  margin: 0;
  opacity: 0.8;
}

.hero-copy,
.side-copy,
.panel-head p,
.progress-copy,
.empty-state,
.metric-card small {
  color: var(--muted);
  line-height: 1.6;
}

.hero-panel .hero-copy,
.hero-panel .eyebrow,
.hero-panel .hero-metrics span {
  color: rgba(247, 241, 232, 0.82);
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.hero-metrics article {
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.hero-metrics strong,
.goal-card strong,
.metric-card strong,
.panel strong,
.library-card h4,
.detail-content h4 {
  display: block;
}

.auth-panel,
.content-panel {
  padding: 28px;
}

.auth-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-toggle {
  display: inline-grid;
  grid-template-columns: repeat(2, 1fr);
  padding: 6px;
  background: #f3ecdf;
  border-radius: 999px;
  margin-bottom: 22px;
}

.auth-toggle button,
.side-nav button,
.ghost-btn,
.primary-btn,
.danger-btn,
.link-btn {
  border: none;
  border-radius: 999px;
  padding: 12px 16px;
  cursor: pointer;
  transition: transform 0.18s ease, opacity 0.18s ease, background 0.18s ease;
}

.auth-toggle button,
.side-nav button,
.ghost-btn,
.link-btn {
  background: transparent;
  color: var(--ink);
}

.auth-toggle button.active,
.side-nav button.active,
.primary-btn {
  background: var(--accent);
  color: #fff7ef;
}

.primary-btn:hover,
.ghost-btn:hover,
.danger-btn:hover,
.link-btn:hover,
.auth-toggle button:hover,
.side-nav button:hover {
  transform: translateY(-1px);
}

.ghost-btn {
  background: #ede5d7;
}

.danger-btn {
  background: rgba(180, 35, 24, 0.12);
  color: var(--danger);
}

.link-btn {
  padding: 0;
  color: var(--accent);
}

.auth-form,
.book-form,
.goal-form {
  display: grid;
  gap: 14px;
}

.auth-form label,
.book-form label,
.goal-form label {
  display: grid;
  gap: 8px;
  font-size: 0.95rem;
}

input,
select,
textarea {
  width: 100%;
  padding: 12px 14px;
  border-radius: 16px;
  border: 1px solid rgba(18, 52, 59, 0.14);
  background: rgba(255, 255, 255, 0.9);
  color: var(--ink);
}

textarea {
  resize: vertical;
}

.feedback {
  margin: 12px 0 0;
  padding: 12px 16px;
  border-radius: 14px;
}

.feedback.success {
  background: rgba(12, 120, 82, 0.12);
  color: #0c7852;
}

.feedback.error {
  background: rgba(180, 35, 24, 0.1);
  color: var(--danger);
}

.app-layout {
  grid-template-columns: 280px minmax(0, 1fr);
}

.side-panel {
  padding: 26px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.side-nav {
  display: grid;
  gap: 10px;
}

.side-nav button {
  text-align: left;
  border-radius: 18px;
  background: #ede5d7;
}

.goal-card {
  padding: 18px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(186, 74, 0, 0.1), rgba(18, 52, 59, 0.08));
}

.content-header,
.panel-head,
.library-card-head,
.card-actions,
.filter-actions,
.form-actions,
.profile-summary,
.genre-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.content-header {
  margin-bottom: 20px;
}

.view-grid,
.library-layout {
  display: grid;
  gap: 18px;
}

.summary-grid,
.stats-grid,
.profile-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.library-layout {
  grid-template-columns: 1.05fr 1fr;
}

.wide {
  grid-column: 1 / -1;
}

.panel,
.metric-card {
  border-radius: 24px;
  padding: 22px;
}

.metric-card {
  display: grid;
  gap: 8px;
  min-height: 150px;
}

.metric-card.accent {
  background: linear-gradient(135deg, rgba(186, 74, 0, 0.92), rgba(145, 62, 5, 0.84));
  color: #fff7ef;
}

.metric-card.accent small,
.metric-card.accent span {
  color: rgba(255, 247, 239, 0.82);
}

.progress-track {
  height: 14px;
  border-radius: 999px;
  background: #ece2d1;
  overflow: hidden;
}

.progress-track.compact {
  height: 10px;
}

.progress-fill,
.bar-fill {
  background: linear-gradient(90deg, #ba4a00, #d47a28);
}

.progress-fill {
  height: 100%;
  border-radius: inherit;
}

.progress-fill.warm {
  background: linear-gradient(90deg, #12343b, #4e766b);
}

.book-strip,
.library-cards,
.genre-list {
  display: grid;
  gap: 14px;
}

.book-strip {
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.book-chip,
.library-card {
  padding: 18px;
  border-radius: 20px;
  background: #f7f1e7;
  border: 1px solid rgba(18, 52, 59, 0.08);
}

.book-chip {
  cursor: pointer;
}

.pill {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 700;
  margin-bottom: 10px;
}

.pill.slate {
  background: #e6e9ee;
  color: #536071;
}

.pill.amber {
  background: #fde6c2;
  color: #9c5a00;
}

.pill.green {
  background: #d7f1e1;
  color: #16794d;
}

.filters-grid,
.book-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.filter-actions,
.form-actions,
.full-width {
  grid-column: 1 / -1;
}

.library-card dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 16px 0;
}

.library-card dt {
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--muted);
}

.library-card dd {
  margin: 6px 0 0;
}

.detail-panel {
  min-height: 320px;
}

.detail-content,
.detail-list {
  display: grid;
  gap: 10px;
}

.detail-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.detail-author,
.detail-comments {
  color: var(--muted);
}

.chart-bars {
  height: 280px;
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  gap: 12px;
  align-items: end;
}

.bar-column {
  height: 100%;
  display: grid;
  gap: 8px;
  justify-items: center;
}

.bar-track {
  width: 100%;
  height: 100%;
  border-radius: 18px 18px 10px 10px;
  background: #f0e6d7;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.bar-fill {
  width: 100%;
  border-radius: 18px 18px 0 0;
}

.bar-value {
  font-weight: 700;
}

.genre-row {
  display: grid;
  grid-template-columns: minmax(140px, 220px) 1fr auto;
  gap: 14px;
  align-items: center;
}

.genre-copy {
  display: grid;
}

.profile-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.profile-summary div {
  padding: 18px;
  border-radius: 18px;
  background: #f7f1e7;
}

@media (max-width: 1100px) {
  .auth-layout,
  .app-layout,
  .library-layout,
  .summary-grid,
  .stats-grid,
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .hero-metrics,
  .profile-summary,
  .filters-grid,
  .book-form,
  .library-card dl {
    grid-template-columns: 1fr;
  }

  .chart-bars {
    grid-template-columns: repeat(6, minmax(0, 1fr));
    height: auto;
  }
}

@media (max-width: 720px) {
  .shell {
    padding: 14px;
  }

  .hero-panel,
  .auth-panel,
  .content-panel,
  .side-panel,
  .panel,
  .metric-card {
    padding: 18px;
    border-radius: 20px;
  }

  .content-header,
  .panel-head,
  .library-card-head,
  .card-actions,
  .filter-actions,
  .form-actions,
  .genre-row {
    flex-direction: column;
    align-items: stretch;
  }

  .chart-bars {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .genre-row {
    grid-template-columns: 1fr;
  }
}
</style>