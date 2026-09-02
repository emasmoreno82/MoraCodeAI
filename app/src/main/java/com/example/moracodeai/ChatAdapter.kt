package com.example.moracodeai

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon

class ChatAdapter(private val messages: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    // Variable global de Markwon para inicializarla una sola vez
    private var markwon: Markwon? = null

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bubbleContainer: LinearLayout = view.findViewById(R.id.bubbleContainer)
        val bubbleCard: CardView = view.findViewById(R.id.bubbleCard)
        val textMessage: TextView = view.findViewById(R.id.textMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        // Inicializamos Markwon usando el contexto de la pantalla
        if (markwon == null) {
            markwon = Markwon.create(parent.context)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        // MAGIA DE MARKWON: Convierte el markdown (asteriscos, etc.) en formato real de TextView
        markwon?.setMarkdown(holder.textMessage, message.text)

        if (message.isFromMora) {
            holder.bubbleContainer.gravity = Gravity.START
            holder.bubbleCard.setCardBackgroundColor(Color.parseColor("#334155"))
            holder.textMessage.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            holder.bubbleContainer.gravity = Gravity.END
            holder.bubbleCard.setCardBackgroundColor(Color.parseColor("#0ea5e9"))
            holder.textMessage.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount(): Int = messages.size
}