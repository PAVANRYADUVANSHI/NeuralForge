import React from 'react'
import { useAuthStore } from '../store/authStore'
import { Code, Bug, Zap, TrendingUp } from 'lucide-react'

export const StatsPanel: React.FC = () => {
  const { user } = useAuthStore()

  const stats = [
    {
      icon: Code,
      label: 'Features Generated',
      value: user?.totalFeaturesGenerated || 0,
      color: 'text-neural-400'
    },
    {
      icon: Bug,
      label: 'Bugs Predicted',
      value: user?.totalBugsPredicted || 0,
      color: 'text-red-400'
    },
    {
      icon: Zap,
      label: 'AI Credits',
      value: user?.aiCredits || 0,
      color: 'text-forge-400'
    },
    {
      icon: TrendingUp,
      label: 'Success Rate',
      value: '98.7%',
      color: 'text-green-400'
    }
  ]

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
      {stats.map((stat, idx) => (
        <div key={idx} className="neural-card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm mb-1">{stat.label}</p>
              <p className={`text-3xl font-bold ${stat.color}`}>{stat.value}</p>
            </div>
            <stat.icon className={`w-10 h-10 ${stat.color} opacity-50`} />
          </div>
        </div>
      ))}
    </div>
  )
}
