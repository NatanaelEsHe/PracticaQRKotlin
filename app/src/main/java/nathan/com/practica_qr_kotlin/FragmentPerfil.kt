package nathan.com.practica_qr_kotlin

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class FragmentPerfil : Fragment() {

    private var sharedPreferences: SharedPreferences? = null
    private var tvCambiarPass: TextView? = null
    private var ivDesconectar: ImageView? = null
    private var swNm: Switch? = null
    private var user: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_perfil, container, false)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        user = sharedPreferences!!.getString("user", "")

        setHasOptionsMenu(true)

        tvCambiarPass = v.findViewById<TextView>(R.id.tv_cambiar_pass) as TextView
        swNm = v.findViewById<Switch>(R.id.sw_perfil_nm) as Switch
        ivDesconectar = v.findViewById<ImageView>(R.id.iv_perfil_desc) as ImageView

        val sw = sharedPreferences!!.getBoolean("modo_noche", false)
        if (sw)
            swNm!!.isChecked = true
        else
            swNm!!.isChecked = false

        tvCambiarPass!!.setOnClickListener { dialogCambiarPass() }

        swNm!!.setOnClickListener {
            if (swNm!!.isChecked) {
                sharedPreferences!!.edit().putBoolean("modo_noche", true).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                sharedPreferences!!.edit().putBoolean("modo_noche", false).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            restart()
        }

        ivDesconectar!!.setOnClickListener { FirebaseAuth.getInstance().signOut() }

        return v
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_tabs, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun restart() {
        val i = Intent(context, MainActivity::class.java)
        startActivity(i)
        activity.finish()
    }

    private fun dialogCambiarPass() {
        val alertDialogBu = AlertDialog.Builder(context)
        alertDialogBu.setTitle(R.string.d_aviso_tit)
        alertDialogBu.setMessage(getString(R.string.d_aviso_cambiar_pass) + ":\n" + user!!.trim { it <= ' ' } + "\n" + getString(R.string.d_aviso_cambiar_pass2))

        alertDialogBu.setPositiveButton(R.string.d_bt_ok) { dialogInterface, i -> enviarMensaje() }

        alertDialogBu.setNegativeButton(R.string.d_bt_cancel) { dialogInterface, i -> }

        val alertDialog = alertDialogBu.create()
        alertDialog.show()
    }

    private fun enviarMensaje() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(user!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val snack = Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.snack_email_enviado), Snackbar.LENGTH_LONG)
                            snack.view.setBackgroundColor(ContextCompat.getColor(activity, R.color.amarillo))
                            val textView = snack.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
                            textView.setTextColor(activity.resources.getColor(R.color.negro_static))
                            snack.show()
                        }
                    }
        }
    }
}