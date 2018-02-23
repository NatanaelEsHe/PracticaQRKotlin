package nathan.com.practica_qr_kotlin


import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import java.util.ArrayList


class AdaptadorRVFavoritos(private val items: MutableList<Favorito>, private val main: FragmentFav) : RecyclerView.Adapter<AdaptadorRVFavoritos.AdaptadorViewHolder>() {

    private val select: MutableList<Favorito>
    private var modoSeleccion: Boolean = false
    private var animation: Animation? = null

    val selectedItems: List<String>
        get() {
            val items = ArrayList<String>(select.size)
            for (i in select.indices) {
                items.add(select[i].key.toString())
            }
            return items
        }

    val selectedItemsCount: Int
        get() = select.size

    init {

        select = ArrayList()
        try {
            animation = AnimationUtils.loadAnimation(main.getContext(), R.anim.modo_seleccion)
            animation!!.fillAfter = true
            animation!!.repeatMode = Animation.REVERSE
        } catch (e: NullPointerException) {
        }

    }

    override fun onBindViewHolder(holder: AdaptadorViewHolder, position: Int) {
        val item = items[position]
        holder.bindView(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_recycler_favoritos, parent, false)
        return AdaptadorViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    /*public void restoreItem(Favorito item, int position){
        items.add(position,item);
        notifyItemInserted(position);
    }*/

    fun setModoSeleccion(modoSeleccion: Boolean) {
        this.modoSeleccion = modoSeleccion
    }

    inner class AdaptadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var viewBG: RelativeLayout
        var viewFG: ConstraintLayout

        private val imagen: ImageView
        private val tvUrl: TextView
        private val tvFec: TextView

        init {

            imagen = itemView.findViewById<ImageView>(R.id.iv_fav) as ImageView
            tvUrl = itemView.findViewById(R.id.tv_url)
            tvFec = itemView.findViewById(R.id.tv_fecha)

            viewBG = itemView.findViewById<RelativeLayout>(R.id.view_bg) as RelativeLayout
            viewFG = itemView.findViewById<ConstraintLayout>(R.id.view_fg) as ConstraintLayout
        }

        fun bindView(item: Favorito) {

            imagen.setImageDrawable(main.getResources().getDrawable(R.drawable.empty))
            tvUrl.setText(item.url)
            tvFec.setText(item.fecha)

            viewFG.setOnLongClickListener { view ->
                if (!modoSeleccion) {
                    select.clear()
                    modoSeleccion = true
                    view.isSelected = true
                    imagen.setImageDrawable(main.getResources().getDrawable(R.drawable.check))
                    //animacion
                    imagen.startAnimation(animation)
                    //
                    select.add(items[adapterPosition])
                    main.seleccionOk()
                }
                true
            }
            viewFG.setOnClickListener { view ->
                if (modoSeleccion) {
                    if (!view.isSelected) {
                        view.isSelected = true
                        imagen.setImageDrawable(main.getResources().getDrawable(R.drawable.check))
                        //animacion
                        imagen.startAnimation(animation)
                        //
                        select.add(items[adapterPosition])
                        main.selectedItems()
                    } else {
                        view.isSelected = false
                        select.remove(items[adapterPosition])
                        imagen.setImageDrawable(main.getResources().getDrawable(R.drawable.empty))
                        //animacion
                        imagen.startAnimation(animation)
                        //
                        main.selectedItems()
                        if (!haySeleccionados()) {
                            modoSeleccion = false
                            main.seleccionFuera()
                        }
                    }
                } else {
                    val i = Intent(main.getActivity(), WebViewActivity::class.java)
                    i.putExtra("favorito", items[adapterPosition])
                    main.startActivity(i)
                }
            }
        }

        fun haySeleccionados(): Boolean {
            return if (select.size != 0) true else false
        }
    }
}