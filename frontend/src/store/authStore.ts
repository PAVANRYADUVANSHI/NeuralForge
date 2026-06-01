import { create } from 'zustand'

interface User {
  id: string
  username: string
  email: string
  role: string
  aiCredits: number
  totalFeaturesGenerated: number
  totalBugsPredicted: number
}

interface AuthState {
  user: User | null
  accessToken: string | null
  isAuthenticated: boolean
  login: (token: string, user: User) => void
  logout: () => void
  updateCredits: (credits: number) => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: localStorage.getItem('accessToken'),
  isAuthenticated: !!localStorage.getItem('accessToken'),
  
  login: (token, user) => {
    localStorage.setItem('accessToken', token)
    set({ accessToken: token, user, isAuthenticated: true })
  },
  
  logout: () => {
    localStorage.removeItem('accessToken')
    set({ accessToken: null, user: null, isAuthenticated: false })
  },
  
  updateCredits: (credits) => set((state) => ({
    user: state.user ? { ...state.user, aiCredits: credits } : null
  }))
}))
