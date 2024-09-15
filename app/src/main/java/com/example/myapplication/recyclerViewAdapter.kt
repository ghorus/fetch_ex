package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private var itemsList : ArrayList<Request>): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewAdapter.MyViewHolder {
        //inflates layout(adds layout) to screen
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        //assign values to Views
        val currentItem = itemsList[position]
        holder.id.text = currentItem.id
        holder.listId.text = currentItem.listId
        holder.name.text = currentItem.name
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun setFilteredList(d:ArrayList<Request>){
        this.itemsList = d
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val id : TextView = itemView.findViewById(R.id.idView)
        val name : TextView = itemView.findViewById(R.id.nameView)
        val listId : TextView = itemView.findViewById(R.id.listIdView)
    }
}
