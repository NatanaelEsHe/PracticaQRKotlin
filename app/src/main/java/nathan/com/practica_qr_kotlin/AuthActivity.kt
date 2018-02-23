package nathan.com.practica_qr_kotlin


import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val manager = supportFragmentManager
        manager.beginTransaction().add(R.id.contenedor_login, FragmentLogin()).addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0)
            fragmentManager.popBackStack()
        else
            super.onBackPressed()
    }

    fun ocultarTeclado(v: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

