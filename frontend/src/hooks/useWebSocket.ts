import { useEffect, useRef } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useAuthStore } from '../store/authStore'
import { useAIStore } from '../store/aiStore'
import toast from 'react-hot-toast'

export const useWebSocket = () => {
  const clientRef = useRef<Client | null>(null)
  const { accessToken, user } = useAuthStore()
  const { updateFeature } = useAIStore()

  useEffect(() => {
    if (!accessToken || !user) return

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: { Authorization: `Bearer ${accessToken}` },
      
      onConnect: () => {
        console.log('🔌 WebSocket connected')
        
        // Subscribe to AI progress updates
        client.subscribe(`/user/queue/ai-progress`, (message) => {
          const update = JSON.parse(message.body)
          updateFeature(update.requestId, { status: update.status })
          
          if (update.status === 'COMPLETED') {
            toast.success('✨ Feature generated successfully!')
          } else if (update.status === 'FAILED') {
            toast.error('❌ Generation failed: ' + update.message)
          }
        })
        
        // Subscribe to live AI suggestions
        client.subscribe(`/user/queue/ai-suggestions`, (message) => {
          const suggestion = JSON.parse(message.body)
          toast.success('💡 AI Suggestion: ' + suggestion.suggestion, { duration: 5000 })
        })
        
        // Subscribe to team channel
        if (user.id) {
          client.subscribe(`/topic/team/${user.id}`, (message) => {
            const teamUpdate = JSON.parse(message.body)
            console.log('👥 Team update:', teamUpdate)
          })
        }
      },
      
      onStompError: (frame) => {
        console.error('❌ WebSocket error:', frame)
      }
    })

    client.activate()
    clientRef.current = client

    return () => {
      client.deactivate()
    }
  }, [accessToken, user, updateFeature])

  const sendCodeChange = (codeSnippet: string, fileName: string) => {
    if (clientRef.current?.connected && user) {
      clientRef.current.publish({
        destination: '/app/code-change',
        body: JSON.stringify({
          userId: user.id,
          teamId: user.id,
          codeSnippet,
          fileName
        })
      })
    }
  }

  return { sendCodeChange }
}
