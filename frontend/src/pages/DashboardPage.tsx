import React, { useState } from 'react'
import { useAuthStore } from '../store/authStore'
import { useAIStore } from '../store/aiStore'
import { aiAPI } from '../services/api'
import { useWebSocket } from '../hooks/useWebSocket'
import toast from 'react-hot-toast'
import { Brain, Zap, Shield, Code, Sparkles, TrendingUp } from 'lucide-react'
import { NeuralBackground } from '../components/NeuralBackground'
import { IntentInput } from '../components/IntentInput'
import { FeatureList } from '../components/FeatureList'
import { StatsPanel } from '../components/StatsPanel'

export const DashboardPage: React.FC = () => {
  const { user } = useAuthStore()
  const { features, isGenerating, setGenerating, addFeature } = useAIStore()
  useWebSocket()
  const [intent, setIntent] = useState('')

  const handleGenerateFeature = async () => {
    if (!intent.trim()) {
      toast.error('Please enter your intent')
      return
    }

    setGenerating(true)
    try {
      const response = await aiAPI.generateFeature(intent)
      addFeature(response.data)
      toast.success('🚀 AI is generating your feature...')
      setIntent('')
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Generation failed')
    } finally {
      setGenerating(false)
    }
  }

  return (
    <div className="min-h-screen neural-grid-bg relative overflow-hidden">
      <NeuralBackground />
      
      <div className="relative z-10 container mx-auto px-4 py-8">
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-4xl font-bold glow-text flex items-center gap-3">
              <Brain className="w-10 h-10 animate-pulse-slow" />
              NeuralForge
            </h1>
            <p className="text-gray-400 mt-2">Welcome back, {user?.username}</p>
          </div>
          
          <div className="neural-card px-6 py-3">
            <div className="flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-forge-400" />
              <span className="text-2xl font-bold text-forge-400">{user?.aiCredits}</span>
              <span className="text-gray-400 text-sm">AI Credits</span>
            </div>
          </div>
        </div>

        {/* Stats Grid */}
        <StatsPanel />

        {/* Main Feature Cards */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Intent-to-Feature Engine */}
          <div className="neural-card">
            <div className="flex items-center gap-3 mb-4">
              <Zap className="w-6 h-6 text-neural-400" />
              <h2 className="text-2xl font-bold">Intent-to-Feature Engine</h2>
            </div>
            <p className="text-gray-400 mb-6">
              Describe what you want in plain English — AI generates the complete feature
            </p>
            <IntentInput
              value={intent}
              onChange={setIntent}
              onSubmit={handleGenerateFeature}
              isGenerating={isGenerating}
            />
          </div>

          {/* Quick Actions */}
          <div className="neural-card">
            <h2 className="text-2xl font-bold mb-4">AI Capabilities</h2>
            <div className="space-y-3">
              <ActionCard
                icon={<Shield className="w-5 h-5" />}
                title="Predictive Bug Oracle"
                description="Analyze code and predict bugs before runtime"
                onClick={() => toast('Navigate to Bug Oracle')}
              />
              <ActionCard
                icon={<Code className="w-5 h-5" />}
                title="Neural Code Review"
                description="Multi-agent AI reviews your code like senior engineers"
                onClick={() => toast('Navigate to Code Review')}
              />
              <ActionCard
                icon={<TrendingUp className="w-5 h-5" />}
                title="Self-Healing Runtime"
                description="AI monitors and auto-patches production errors"
                onClick={() => toast('Navigate to Self-Healing')}
              />
            </div>
          </div>
        </div>

        {/* Recent Features */}
        <div className="neural-card">
          <h2 className="text-2xl font-bold mb-6">Recent Generated Features</h2>
          <FeatureList features={features} />
        </div>
      </div>
    </div>
  )
}

const ActionCard: React.FC<{
  icon: React.ReactNode
  title: string
  description: string
  onClick: () => void
}> = ({ icon, title, description, onClick }) => (
  <button
    onClick={onClick}
    className="w-full bg-neural-900/50 hover:bg-neural-800/50 border border-neural-700 
               rounded-lg p-4 text-left transition-all hover:border-neural-500 group"
  >
    <div className="flex items-start gap-3">
      <div className="text-neural-400 group-hover:text-forge-400 transition-colors">
        {icon}
      </div>
      <div>
        <h3 className="font-semibold mb-1">{title}</h3>
        <p className="text-sm text-gray-400">{description}</p>
      </div>
    </div>
  </button>
)
