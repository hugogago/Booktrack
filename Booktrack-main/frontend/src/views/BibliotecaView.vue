<template>
  <section class="library-layout">
    <article class="panel wide">
      <div class="panel-head">
        <div>
          <h3>Buscador avanzado</h3>
          <p>Filtra por titulo, autor, genero, editorial o estado.</p>
        </div>
      </div>

      <form class="filters-grid" @submit.prevent="aplicarFiltros">
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
          <p>{{ localBooks.length }} libros encontrados en tu espacio personal.</p>
        </div>
      </div>

      <div v-if="localBooks.length" class="library-cards">
        <article v-for="book in localBooks" :key="book.id" class="library-card">
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
</template>

<script>
import api from '../api'

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

export default {
  name: 'BibliotecaView',
  props: {
    books: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update-books', 'update-all', 'set-feedback'],
  data() {
    return {
      localBooks: [...this.books],
      filters: { search: '', status: '', author: '', genre: '', publisher: '' },
      bookForm: createEmptyBookForm(),
      selectedBook: null,
      editingBookId: null
    }
  },
  watch: {
    books(newBooks) {
      this.localBooks = [...newBooks]
      if (this.selectedBook) {
        this.selectedBook = newBooks.find((b) => b.id === this.selectedBook.id) || null
      }
    }
  },
  methods: {
    async aplicarFiltros() {
      const params = {}
      Object.entries(this.filters).forEach(([key, value]) => {
        if (value !== '' && value !== null && value !== undefined) {
          params[key] = value
        }
      })
      const response = await api.get('/books', { params })
      this.localBooks = response.data
    },
    resetFilters() {
      this.filters = { search: '', status: '', author: '', genre: '', publisher: '' }
      this.localBooks = [...this.books]
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
          this.$emit('set-feedback', 'success', 'Libro actualizado correctamente.')
        } else {
          await api.post('/books', payload)
          this.$emit('set-feedback', 'success', 'Libro anadido a la biblioteca.')
        }
        this.prepareNewBook()
        this.$emit('update-all')
      } catch (error) {
        this.$emit('set-feedback', 'error', this.extractError(error, 'No se pudo guardar el libro.'))
      }
    },
    prepareNewBook() {
      this.editingBookId = null
      this.bookForm = createEmptyBookForm()
    },
    editBook(book) {
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
        this.$emit('set-feedback', 'success', 'Libro marcado como leido.')
        this.$emit('update-all')
      } catch (error) {
        this.$emit('set-feedback', 'error', this.extractError(error, 'No se pudo actualizar el estado del libro.'))
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
        this.$emit('set-feedback', 'success', 'Libro eliminado correctamente.')
        this.$emit('update-all')
      } catch (error) {
        this.$emit('set-feedback', 'error', this.extractError(error, 'No se pudo eliminar el libro.'))
      }
    },
    openBook(book) {
      this.selectedBook = book
    },
    statusLabel(status) {
      return { PENDING: 'Pendiente', IN_PROGRESS: 'En curso', READ: 'Leido' }[status] || status
    },
    statusClass(status) {
      return { PENDING: 'slate', IN_PROGRESS: 'amber', READ: 'green' }[status] || 'slate'
    },
    formatDate(value) {
      if (!value) return 'Sin registrar'
      return new Date(`${value}T00:00:00`).toLocaleDateString('es-ES')
    },
    normalizeEmpty(value) {
      return value === '' ? null : value
    },
    extractError(error, fallbackMessage) {
      return error?.response?.data?.message || error?.response?.data?.error || fallbackMessage
    }
  }
}
</script>
