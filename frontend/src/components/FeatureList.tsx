import React from 'react'
import { CheckCircle, Clock, XCircle, Loader } from 'lucide-react'

interface Feature {
  id: string
  intent: string
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  generatedFiles: string[]
  tokensUsed?: number
  processingTimeMs?: number
  createdAt: string
}

interface FeatureListProps {
  features: Feature[]
}

export const FeatureList: React.FC<FeatureListProps> = ({ features }) => {
  if (features.length === 0) {
    return (
      <div className="text-center py-12 text-gray-500">
        No features generated yet. Start by describing what you want to build!
      </div>
    )
  }

  return (
    <div className="space-y-4">
      {features.map((feature) => (
        <div
          key={feature.id}
          className="bg-neural-900/30 border border-neural-700/50 rounded-lg p-4 
                     hover:border-neural-600 transition-all"
        >
          <div className="flex items-start justify-between mb-3">
            <div className="flex-1">
              <p className="text-gray-300 mb-2">{feature.intent}</p>
              <div className="flex items-center gap-4 text-sm text-gray-500">
                <span>{new Date(feature.createdAt).toLocaleString()}</span>
                {feature.tokensUsed && <span>{feature.tokensUsed} tokens</span>}
                {feature.processingTimeMs && (
                  <span>{(feature.processingTimeMs / 1000).toFixed(2)}s</span>
                )}
              </div>
            </div>
            <StatusBadge status={feature.status} />
          </div>

          {feature.generatedFiles.length > 0 && (
            <div className="flex flex-wrap gap-2 mt-3">
              {feature.generatedFiles.map((file, idx) => (
                <span
                  key={idx}
                  className="px-3 py-1 bg-neural-800/50 text-forge-400 text-xs rounded-full 
                             border border-neural-700"
                >
                  {file}
                </span>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  )
}

const StatusBadge: React.FC<{ status: Feature['status'] }> = ({ status }) => {
  const config = {
    PENDING: { icon: Clock, color: 'text-yellow-400', bg: 'bg-yellow-400/10' },
    PROCESSING: { icon: Loader, color: 'text-blue-400', bg: 'bg-blue-400/10' },
    COMPLETED: { icon: CheckCircle, color: 'text-green-400', bg: 'bg-green-400/10' },
    FAILED: { icon: XCircle, color: 'text-red-400', bg: 'bg-red-400/10' }
  }

  const { icon: Icon, color, bg } = config[status]

  return (
    <div className={`flex items-center gap-2 px-3 py-1 rounded-full ${bg}`}>
      <Icon className={`w-4 h-4 ${color} ${status === 'PROCESSING' ? 'animate-spin' : ''}`} />
      <span className={`text-sm font-medium ${color}`}>{status}</span>
    </div>
  )
}
