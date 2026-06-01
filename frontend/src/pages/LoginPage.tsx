import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authAPI } from '../services/api'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import { Brain, Sparkles, Mail, Lock, User } from 'lucide-react'

export const LoginPage: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true)
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)

  const navigate = useNavigate()
  const { login } = useAuthStore()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      const response = isLogin
        ? await authAPI.login(email || username, password)
        : await authAPI.register(username, email, password)

      const { accessToken, userId, username: uname, email: uemail, role, aiCredits } = response.data

      login(accessToken, {
        id: userId,
        username: uname,
        email: uemail,
        role,
        aiCredits: aiCredits ?? 1000,
        totalFeaturesGenerated: 0,
        totalBugsPredicted: 0
      })

      toast.success(`🎉 Welcome to NeuralForge, ${uname}!`)
      navigate('/dashboard')
    } catch (error: any) {
      toast.error(error.response?.data?.message || error.message || 'Authentication failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen neural-grid-bg flex items-center justify-center p-4">
      <div className="neural-card max-w-md w-full">

        {/* Header */}
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <Brain className="w-16 h-16 text-neural-400 animate-pulse-slow" />
          </div>
          <h1 className="text-4xl font-bold glow-text mb-2">NeuralForge</h1>
          <p className="text-gray-400 text-sm">Autonomous AI Development Platform</p>
        </div>

        {/* Tabs */}
        <div className="flex mb-6 bg-neural-900/50 rounded-lg p-1">
          <button
            onClick={() => setIsLogin(true)}
            className={`flex-1 py-2 rounded-md text-sm font-semibold transition-all ${
              isLogin ? 'bg-neural-600 text-white' : 'text-gray-400 hover:text-white'
            }`}
          >Login</button>
          <button
            onClick={() => setIsLogin(false)}
            className={`flex-1 py-2 rounded-md text-sm font-semibold transition-all ${
              !isLogin ? 'bg-neural-600 text-white' : 'text-gray-400 hover:text-white'
            }`}
          >Register</button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">

          {/* Username — only on register */}
          {!isLogin && (
            <div className="relative">
              <User className="absolute left-3 top-3.5 w-4 h-4 text-gray-400" />
              <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={e => setUsername(e.target.value)}
                className="neural-input w-full pl-10"
                required={!isLogin}
              />
            </div>
          )}

          {/* Email — login accepts email OR username */}
          <div className="relative">
            <Mail className="absolute left-3 top-3.5 w-4 h-4 text-gray-400" />
            <input
              type={isLogin ? 'text' : 'email'}
              placeholder={isLogin ? 'Email or Username' : 'Email Address'}
              value={email}
              onChange={e => setEmail(e.target.value)}
              className="neural-input w-full pl-10"
              required
            />
          </div>

          {/* Password */}
          <div className="relative">
            <Lock className="absolute left-3 top-3.5 w-4 h-4 text-gray-400" />
            <input
              type="password"
              placeholder={isLogin ? 'Password' : 'Password (min 8 chars)'}
              value={password}
              onChange={e => setPassword(e.target.value)}
              className="neural-input w-full pl-10"
              minLength={8}
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="neural-button w-full flex items-center justify-center gap-2 mt-2"
          >
            {loading ? (
              <><div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" /> Processing...</>
            ) : (
              <><Sparkles className="w-5 h-5" />{isLogin ? 'Login' : 'Create Account'}</>
            )}
          </button>
        </form>

        {/* Demo hint */}
        <div className="mt-6 p-3 bg-neural-900/50 rounded-lg border border-neural-700/50 text-center">
          <p className="text-xs text-gray-400">
            {isLogin
              ? '💡 Login with your email or username'
              : '💡 Register with any email & password (min 8 chars)'}
          </p>
        </div>

      </div>
    </div>
  )
}
