<template>
  <section class="view-grid summary-grid">
    <article class="metric-card accent">
      <span>Leidos este mes</span>
      <strong>{{ summary.currentMonthReadCount }}</strong>
      <small>Libros terminados en el mes actual</small>
    </article>
    <article class="metric-card">
      <span>Total anual</span>
      <strong>{{ summary.currentYearReadCount }}</strong>
      <small>Libros finalizados este año</small>
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
        <article v-for="book in inProgressBooks" :key="book.id" class="book-chip" @click="irABiblioteca">
          <span class="pill amber">En curso</span>
          <strong>{{ book.title }}</strong>
          <small>{{ book.author }}</small>
        </article>
      </div>
      <p v-else class="empty-state">Todavia no tienes lecturas en curso.</p>
    </article>
  </section>
</template>

<script>
export default {
  name: 'ResumenView',
  props: {
    summary: {
      type: Object,
      default: () => ({
        currentMonthReadCount: 0,
        currentYearReadCount: 0,
        inProgressCount: 0,
        pendingCount: 0,
        totalBooks: 0,
        annualGoal: 0,
        annualGoalProgress: 0
      })
    },
    books: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    progressWidth() {
      return `${Math.min(100, Math.max(0, this.summary.annualGoalProgress || 0))}%`
    },
    inProgressBooks() {
      return this.books.filter((book) => book.status === 'IN_PROGRESS').slice(0, 4)
    }
  },
  methods: {
    irABiblioteca() {
      this.$router.push({ name: 'biblioteca' })
    }
  }
}
</script>
