import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const authAPI = {
  register: (username: string, email: string, password: string) =>
    api.post('/auth/register', { username, email, password }),

  login: (username: string, password: string) =>
    api.post('/auth/login', { username, password })
}

export const aiAPI = {
  generateFeature: (intent: string) =>
    api.post('/ai/intent', { intent }),

  getMyFeatures: (page = 0, size = 10) =>
    api.get('/ai/features', { params: { page, size } }),

  predictBugs: (filePath: string, fileContent: string) =>
    api.post('/oracle/predict-bugs', { filePath, fileContent }),

  reviewCode: (code: string, language: string) =>
    api.post('/oracle/code-review', { code, language })
}

export default api
