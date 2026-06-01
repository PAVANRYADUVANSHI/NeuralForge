import { create } from 'zustand'

interface AIFeature {
  id: string
  intent: string
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  generatedFiles: string[]
  generatedCode?: string
  tokensUsed?: number
  processingTimeMs?: number
  createdAt: string
}

interface AIState {
  features: AIFeature[]
  currentFeature: AIFeature | null
  isGenerating: boolean
  addFeature: (feature: AIFeature) => void
  updateFeature: (id: string, updates: Partial<AIFeature>) => void
  setCurrentFeature: (feature: AIFeature | null) => void
  setGenerating: (isGenerating: boolean) => void
}

export const useAIStore = create<AIState>((set) => ({
  features: [],
  currentFeature: null,
  isGenerating: false,
  
  addFeature: (feature) => set((state) => ({
    features: [feature, ...state.features]
  })),
  
  updateFeature: (id, updates) => set((state) => ({
    features: state.features.map(f => f.id === id ? { ...f, ...updates } : f),
    currentFeature: state.currentFeature?.id === id 
      ? { ...state.currentFeature, ...updates } 
      : state.currentFeature
  })),
  
  setCurrentFeature: (feature) => set({ currentFeature: feature }),
  setGenerating: (isGenerating) => set({ isGenerating })
}))
