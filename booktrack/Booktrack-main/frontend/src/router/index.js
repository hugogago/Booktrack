import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import AppLayout from '../views/AppLayout.vue'
import ResumenView from '../views/ResumenView.vue'
import BibliotecaView from '../views/BibliotecaView.vue'
import EstadisticasView from '../views/EstadisticasView.vue'
import PerfilView from '../views/PerfilView.vue'

const routes = [
  {
    path: '/',
    name: 'login',
    component: LoginView
  },
  {
    path: '/app',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/app/resumen'
      },
      {
        path: 'resumen',
        name: 'resumen',
        component: ResumenView
      },
      {
        path: 'biblioteca',
        name: 'biblioteca',
        component: BibliotecaView
      },
      {
        path: 'estadisticas',
        name: 'estadisticas',
        component: EstadisticasView
      },
      {
        path: 'perfil',
        name: 'perfil',
        component: PerfilView
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'login' })
  } else if (to.name === 'login' && token) {
    next({ name: 'resumen' })
  } else {
    next()
  }
})

export default router
