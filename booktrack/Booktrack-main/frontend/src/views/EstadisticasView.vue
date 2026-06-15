<template>
  <section class="view-grid stats-grid">
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
</template>

<script>
export default {
  name: 'EstadisticasView',
  props: {
    statistics: {
      type: Object,
      default: () => ({ monthlyReads: [], genreBreakdown: [] })
    }
  },
  computed: {
    maxMonthlyReadValue() {
      const values = this.statistics.monthlyReads.map((point) => point.value)
      return Math.max(1, ...values)
    }
  },
  methods: {
    monthlyBarHeight(value) {
      if (value === 0) return '0%'
      return `${Math.max(8, (value / this.maxMonthlyReadValue) * 100)}%`
    }
  }
}
</script>
