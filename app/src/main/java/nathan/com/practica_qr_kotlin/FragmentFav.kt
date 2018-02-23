package nathan.com.practica_qr_kotlin

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.ArrayList

import nathan.com.practica_qr_kotlin.helper.RecyclerItemTouchHelper
import nathan.com.practica_qr_kotlin.helper.RecyclerItemTouchHelperListener

class FragmentFav : Fragment(), RecyclerItemTouchHelperListener {

    //private Activity ac;
    private val mFab: FloatingActionButton? = null
    private var rv: RecyclerView? = null
    private var refresh: SwipeRefreshLayout? = null
    private var adapter: AdaptadorRVFavoritos? = null
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var userID: String? = null
    private var array: MutableList<Favorito>? = null

    // firebase
    private var database: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_fav, container, false)

        setHasOptionsMenu(true)

        database = FirebaseDatabase.getInstance()
        myRef = database!!.reference
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        array = ArrayList()
        actionModeCallback = ActionModeCallback()

        rv = view.findViewById<RecyclerView>(R.id.rv_fav) as RecyclerView



        refresh = view.findViewById<SwipeRefreshLayout>(R.id.refresh) as SwipeRefreshLayout
        refresh!!.isRefreshing = true

        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!refresh!!.isRefreshing) {
                    refresh!!.isRefreshing = true
                }
                array!!.clear()
                for (ds in dataSnapshot.children) {
                    val f = ds.getValue(Favorito::class.java)
                    f!!.key = ds.key
                    array!!.add(f)
                }
                setAdaptadorRecycler()
                refresh!!.isRefreshing = false
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        myRef!!.child("usuarios").child(userID!!).child("favoritos").addValueEventListener(menuListener)

        refresh!!.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorAccent,
                R.color.colorPrimary)

        refresh!!.setOnRefreshListener {
            refresh!!.isRefreshing = false
            myRef!!.removeEventListener(menuListener)
            myRef!!.child("usuarios").child(userID!!).child("favoritos").addValueEventListener(menuListener)
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_tabs, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun setAdaptadorRecycler() {
        val layout = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv!!.layoutManager = layout
        adapter = AdaptadorRVFavoritos(array!!, this)
        rv!!.adapter = adapter

        val itemHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemHelperCallback).attachToRecyclerView(rv)
    }

    override fun onSwipe(viewHolder: RecyclerView.ViewHolder, dir: Int, position: Int) {
        if (viewHolder is AdaptadorRVFavoritos.AdaptadorViewHolder) {
            val fav = array!![viewHolder.getAdapterPosition()]
            val deleteIndex = viewHolder.getAdapterPosition()

            adapter!!.removeItem(deleteIndex)
            myRef!!.child("usuarios").child(userID!!).child("favoritos").child(fav.key).removeValue()

            val snack = Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.snack_delete), Snackbar.LENGTH_LONG)
            snack.view.setBackgroundColor(ContextCompat.getColor(activity, R.color.amarillo))
            val textView = snack.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
            textView.setTextColor(activity.resources.getColor(R.color.negro_static))
            textView.setTypeface(null, Typeface.BOLD)

            snack.setAction(getString(R.string.snack_undo)) {
                val favorito = Favorito(fav.url!!, fav.fecha!!)
                myRef!!.child("usuarios").child(userID!!).child("favoritos").push().setValue(favorito)
                //adapter.restoreItem(favorito, deleteIndex);
            }.setActionTextColor(resources.getColor(R.color.negro_static)).show()
        }
    }

    // class action callback
    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_delete, menu)

            refresh!!.isEnabled = false
            val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout) as TabLayout
            tabLayout.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            tabLayout.visibility = View.GONE

            val viewPager = activity.findViewById<MyViewPager>(R.id.pager) as MyViewPager
            viewPager.setEnabledSwipe(false)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.opt_delete -> {
                    dialogDelMasivo(mode)
                    return true
                }

                else -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout) as TabLayout
            tabLayout.startLayoutAnimation()
            tabLayout.visibility = View.VISIBLE

            val viewPager = activity.findViewById<MyViewPager>(R.id.pager) as MyViewPager
            viewPager.setEnabledSwipe(true)

            refresh!!.isEnabled = true
            actionMode = null
            rv!!.post {
                setAdaptadorRecycler()
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun dialogDelMasivo(mode: ActionMode) {
        val alertDialogBu = AlertDialog.Builder(context)
        alertDialogBu.setTitle(R.string.d_aviso_tit)
        alertDialogBu.setMessage(getString(R.string.d_aviso))

        alertDialogBu.setPositiveButton(R.string.d_bt_ok) { dialogInterface, i ->
            eliminarFavoritos()
            mode.finish()
        }

        alertDialogBu.setNegativeButton(R.string.d_bt_cancel) { dialogInterface, i -> }

        val alertDialog = alertDialogBu.create()
        alertDialog.show()
    }

    private fun eliminarFavoritos() {
        val array = adapter!!.selectedItems
        for (i in array.indices) {
            myRef!!.child("usuarios").child(userID!!).child("favoritos").child(array.get(i)).removeValue()
        }
        setAdaptadorRecycler()
    }

    fun seleccionOk() {
        actionMode = (activity as AppCompatActivity).startSupportActionMode(actionModeCallback!!)
        selectedItems()
    }

    fun selectedItems() {
        val count = adapter!!.selectedItemsCount
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.setTitle(count.toString())
            actionMode!!.invalidate()
        }
    }

    fun seleccionFuera() {
        adapter!!.setModoSeleccion(false)
        if (actionMode != null)
            actionMode!!.finish()
    }
}