<template>
  <section class="view-grid profile-grid">
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
          <p>Define cuantos libros quieres completar este año.</p>
        </div>
      </div>

      <form class="goal-form" @submit.prevent="saveGoal">
        <label>
          Objetivo anual
          <input v-model.number="localGoal" type="number" min="0" required />
        </label>
        <button class="primary-btn" type="submit">Guardar objetivo</button>
      </form>
    </article>
  </section>
</template>

<script>
import api from '../api'

function parseStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
}

export default {
  name: 'PerfilView',
  props: {
    summary: {
      type: Object,
      default: () => ({
        totalBooks: 0,
        annualGoal: 0
      })
    }
  },
  emits: ['update-summary', 'set-feedback'],
  data() {
    return {
      user: parseStoredUser(),
      localGoal: this.summary.annualGoal || 0
    }
  },
  watch: {
    'summary.annualGoal'(newGoal) {
      this.localGoal = newGoal
    }
  },
  methods: {
    async saveGoal() {
      try {
        const response = await api.put('/dashboard/goal', { annualGoal: this.localGoal })
        this.localGoal = response.data.annualGoal
        this.$emit('update-summary')
        this.$emit('set-feedback', 'success', 'Objetivo anual actualizado.')
      } catch (error) {
        this.$emit('set-feedback', 'error', 'No se pudo guardar el objetivo anual.')
      }
    }
  }
}
</script>
