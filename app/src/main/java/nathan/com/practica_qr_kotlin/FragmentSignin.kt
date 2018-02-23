package nathan.com.practica_qr_kotlin

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class FragmentSignin : Fragment() {

    private var etEmail: EditText? = null
    private var etPass: EditText? = null
    private var tinUser: TextInputLayout? = null
    private var tinPass: TextInputLayout? = null

    // firebase
    private var auth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_signin, container, false)

        setHasOptionsMenu(true)

        auth = FirebaseAuth.getInstance()

        etEmail = view.findViewById<EditText>(R.id.et_email) as EditText
        etPass = view.findViewById<EditText>(R.id.et_pass) as EditText
        tinPass = view.findViewById<TextInputLayout>(R.id.tin_pass_signin) as TextInputLayout
        tinUser = view.findViewById<TextInputLayout>(R.id.tin_user_signin) as TextInputLayout

        val btSingin = view.findViewById<Button>(R.id.bt_signin) as Button
        btSingin.setOnClickListener { sigin() }

        var animation = AnimationUtils.loadAnimation(context, R.anim.login_leftoright)
        tinUser!!.startAnimation(animation)
        animation = AnimationUtils.loadAnimation(context, R.anim.modo_seleccion)
        btSingin.startAnimation(animation)
        animation = AnimationUtils.loadAnimation(context, R.anim.login_rightoleft)
        tinPass!!.startAnimation(animation)


        try {
            val ac = activity as AppCompatActivity
            ac.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            ac.title = getString(R.string.fragment_signin)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        return view
    }

    fun sigin() {
        val email = etEmail!!.text.toString().trim { it <= ' ' }
        val password = etPass!!.text.toString().trim { it <= ' ' }

        if (comprobarET()) {
            ocultarTeclado(etEmail!!)
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.isIndeterminate = false
            progressDialog.setCancelable(true)
            progressDialog.show()
            auth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity as AppCompatActivity) { task ->
                        if (progressDialog.isShowing) {
                            progressDialog.dismiss()
                        }
                        if (task.isSuccessful) {
                            val snack = Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.snack_ok_signin), Snackbar.LENGTH_LONG)
                            snack.view.setBackgroundColor(ContextCompat.getColor(activity, R.color.amarillo))
                            val textView = snack.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
                            textView.setTextColor(activity.resources.getColor(R.color.negro_static))
                            snack.show()
                            etEmail!!.setText(null)
                            etPass!!.setText(null)
                        } else {
                            if (task.exception!!.toString().contains("already in use")) {
                                val snack = Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.snack_fail_signin), Snackbar.LENGTH_LONG)
                                snack.view.setBackgroundColor(ContextCompat.getColor(activity, R.color.amarillo))
                                val textView = snack.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
                                textView.setTextColor(activity.resources.getColor(R.color.negro_static))
                                snack.show()
                            }
                        }
                    }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                activity.onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
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

    private fun ocultarTeclado(v: View) {
        val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(v.windowToken, 0)
    }
}
