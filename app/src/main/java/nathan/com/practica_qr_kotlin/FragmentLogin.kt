package nathan.com.practica_qr_kotlin

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.varunest.sparkbutton.SparkButton

class FragmentLogin : Fragment() {

    private var sharedPreferences: SharedPreferences? = null
    private var etEmail: EditText? = null
    private var etPass: EditText? = null
    private var tinUser: TextInputLayout? = null
    private var tinPass: TextInputLayout? = null
    private var cb: SparkButton? = null

    // firebase
    private var auth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_login, container, false)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        auth = FirebaseAuth.getInstance()

        cb = view.findViewById<SparkButton>(R.id.cb_recordar) as SparkButton
        etEmail = view.findViewById<EditText>(R.id.et_email) as EditText
        etPass = view.findViewById<EditText>(R.id.et_pass) as EditText
        tinPass = view.findViewById<TextInputLayout>(R.id.tin_pass_login) as TextInputLayout
        tinUser = view.findViewById<TextInputLayout>(R.id.tin_user_login) as TextInputLayout

        var animation = AnimationUtils.loadAnimation(context, R.anim.login_leftoright)
        tinUser!!.startAnimation(animation)
        cb!!.startAnimation(animation)
        animation = AnimationUtils.loadAnimation(context, R.anim.login_rightoleft)
        tinPass!!.startAnimation(animation)
        view.findViewById<TextView>(R.id.tv_recordar).startAnimation(animation)
        animation = AnimationUtils.loadAnimation(context, R.anim.modo_seleccion)
        view.findViewById<Button>(R.id.bt_signin).startAnimation(animation)
        view.findViewById<Button>(R.id.bt_login).startAnimation(animation)
        view.findViewById<LinearLayout>(R.id.lin_cambiar_pass).startAnimation(animation)


        val remember = sharedPreferences!!.getBoolean("remember", true)
        if (remember) {
            etEmail!!.setText(sharedPreferences!!.getString("user", ""))
            etPass!!.setText(sharedPreferences!!.getString("pass", ""))
            cb!!.isChecked = true
        }

        try {
            val ac = activity as AppCompatActivity
            ac.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            ac.title = getString(R.string.fragment_login)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        view.findViewById<LinearLayout>(R.id.lin_recordar).setOnClickListener(View.OnClickListener { cb!!.onClick(null) })


        view.findViewById<Button>(R.id.bt_signin).setOnClickListener(View.OnClickListener { sigin() })


        view.findViewById<Button>(R.id.bt_login).setOnClickListener(View.OnClickListener { login() })

        view.findViewById<LinearLayout>(R.id.lin_cambiar_pass).setOnClickListener(View.OnClickListener { dialogCambiarPass() } )

        return view
    }

    fun login() {
        val email = etEmail!!.text.toString()
        val password = etPass!!.text.toString()

        if(comprobarET()) {
            if (cb!!.isChecked) {
                val edit = sharedPreferences!!.edit()
                edit.putBoolean("remember", true)
                edit.putString("user", email)
                edit.putString("pass", password)
                edit.apply()
            } else {
                val edit = sharedPreferences!!.edit()
                edit.putBoolean("remember", false)
                edit.putString("user", "")
                edit.putString("pass", "")
                edit.apply()
            }

            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.isIndeterminate = false
            progressDialog.setCancelable(true)
            progressDialog.show()
            auth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity as AppCompatActivity) { task ->
                        if (progressDialog.isShowing) {
                            progressDialog.dismiss()
                        }
                        if (!task.isSuccessful) {
                            val snack = Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.snack_fail_login), Snackbar.LENGTH_LONG)
                            snack.view.setBackgroundColor(ContextCompat.getColor(activity, R.color.amarillo))
                            val textView = snack.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
                            textView.setTextColor(activity.resources.getColor(R.color.negro_static))
                            snack.show()
                        } else {
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            activity.finish()
                        }
                    }
        }
    }

    private fun comprobarET(): Boolean {
        val error: String
        if (etEmail!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            etEmail!!.requestFocus()
            error = getString(R.string.tin_required)
            mostrarErrores(tinUser!!, error)
            return false
        } else {
            mostrarErrores(tinUser!!, null)
        }
        if (etPass!!.text.toString().trim { it <= ' ' }.length < 6) {
            etPass!!.requestFocus()
            error = getString(R.string.tin_pass_len)
            mostrarErrores(tinPass!!, error)
            return false
        } else {
            mostrarErrores(tinPass!!, null)
        }
        return true
    }

    private fun mostrarErrores(textInputLayout: TextInputLayout, error: String?) {
        try {
            textInputLayout.error = error
            if (error == null) {
                textInputLayout.isErrorEnabled = false
            } else {
                textInputLayout.isErrorEnabled = true
            }
        } catch (e: java.lang.RuntimeException) {
        }

    }

    private fun dialogCambiarPass() {
        val alertDialogBu = AlertDialog.Builder(context)
        alertDialogBu.setTitle(R.string.d_aviso_tit)
        alertDialogBu.setMessage(getString(R.string.d_aviso_cambiar_pass) + ":\n" + etEmail!!.text.toString().trim { it <= ' ' } + "\n" + getString(R.string.d_aviso_cambiar_pass2))

        alertDialogBu.setPositiveButton(R.string.d_bt_ok) { dialogInterface, i -> enviarMensaje() }

        alertDialogBu.setNegativeButton(R.string.d_bt_cancel) { dialogInterface, i -> }

        val alertDialog = alertDialogBu.create()
        alertDialog.show()
    }

    private fun enviarMensaje() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail!!.text.toString().trim { it <= ' '})
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

    fun sigin() {
        val manager = (activity as AppCompatActivity).supportFragmentManager
        manager.beginTransaction().replace(R.id.contenedor_login, FragmentSignin()).addToBackStack(null).commit()
    }
}
