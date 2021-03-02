package cat.copernic.daniel.marketcomparator.ui.showUsers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.daniel.marketcomparator.R
import cat.copernic.daniel.marketcomparator.model.UsuariDTO

class UsersAdapter(private val context: Context): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private var dataList = mutableListOf<UsuariDTO>()

    fun setListData(data: MutableList<UsuariDTO>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.UsersViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product,parent,false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersAdapter.UsersViewHolder, position: Int) {
        val user = dataList[position]
        holder.bindView(user)
    }

    override fun getItemCount(): Int {
        if (dataList.size > 0){
            return dataList.size
        }else{
            return 0
        }
    }

    inner class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(user: UsuariDTO){
            itemView.findViewById<TextView>(R.id.tvNameProduct).setText(user.mail)
            itemView.findViewById<TextView>(R.id.tvContainerProduct).setText(user.nomUsuari)
        }
    }
}