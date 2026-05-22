import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081/api'
})

export function setAuthToken(token) {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`
    return
  }

  delete api.defaults.headers.common.Authorization
}

export default api