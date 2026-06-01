import React from 'react'
import { Send } from 'lucide-react'

interface IntentInputProps {
  value: string
  onChange: (value: string) => void
  onSubmit: () => void
  isGenerating: boolean
}

export const IntentInput: React.FC<IntentInputProps> = ({
  value,
  onChange,
  onSubmit,
  isGenerating
}) => {
  return (
    <div className="space-y-4">
      <textarea
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder="Example: Create a payment system with Stripe integration, send email receipts, and store transactions in PostgreSQL"
        className="neural-input w-full h-32 resize-none"
        disabled={isGenerating}
      />
      
      <button
        onClick={onSubmit}
        disabled={isGenerating}
        className="neural-button w-full flex items-center justify-center gap-2"
      >
        {isGenerating ? (
          <>
            <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" />
            Generating...
          </>
        ) : (
          <>
            <Send className="w-5 h-5" />
            Generate Feature
          </>
        )}
      </button>
    </div>
  )
}
