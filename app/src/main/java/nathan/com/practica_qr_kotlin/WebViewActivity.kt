package nathan.com.practica_qr_kotlin

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class WebViewActivity : AppCompatActivity() {

    private var webView: WebView? = null
    private var userID: String? = null
    private var refresh: SwipeRefreshLayout? = null
    private var favorito: Favorito? = null
    private var menu: Menu? = null
    private var sw: Boolean = false
    private var array: ArrayList<Favorito>? = null

    // firebase
    private var database: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null
    private var menuListener: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        favorito = intent.extras!!.getSerializable("favorito") as Favorito

        database = FirebaseDatabase.getInstance()
        myRef = database!!.reference
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        array = ArrayList()

        menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                array!!.clear()
                for (ds in dataSnapshot.children) {
                    val f = ds.getValue(Favorito::class.java)
                    f!!.key = ds.key
                    array!!.add(f)
                }
                checkExists()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        myRef!!.child("usuarios").child(userID!!).child("favoritos").addValueEventListener(menuListener)

        webView = findViewById<WebView>(R.id.web_view) as WebView
        refresh = findViewById<SwipeRefreshLayout>(R.id.refresh) as SwipeRefreshLayout

        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.setAppCacheEnabled(true)
        cargarWeb()

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_x_mark_1)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.loading)

        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                refresh!!.isRefreshing = true
                if (menu != null) {
                    menu!!.getItem(0).icon = getDrawable(R.drawable.ic_fav_no_pulsado)
                    menu!!.getItem(0).isEnabled = false
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if(menu != null)
                    menu!!.getItem(0).isEnabled = true
                supportActionBar!!.setTitle(url)

                checkExists()
                comprobarFAV()

                refresh!!.isRefreshing = false
            }
        }

        refresh!!.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorAccent,
                R.color.colorPrimary)

        refresh!!.setOnRefreshListener { webView!!.reload() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_fav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.opt_fav -> {
                if (sw) {
                    menu!!.getItem(0).setIcon(R.drawable.ic_fav_no_pulsado)
                    myRef!!.child("usuarios").child(userID!!).child("favoritos").child(favorito!!.key).removeValue()
                } else {
                    menu!!.getItem(0).setIcon(R.drawable.ic_fav_pulsado)
                    favorito = Favorito(supportActionBar!!.title!!.toString(), SimpleDateFormat("dd/MM/yyyy").format(Date()))
                    myRef!!.child("usuarios").child(userID!!).child("favoritos").push().setValue(favorito)
                }
                sw = !sw
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun checkExists() {
        var favorito = Favorito("","")
        val url = supportActionBar!!.title!!.toString().trim()
        for (f in array!!) {
            if (f.url!!.trim().equals(url)) {
                favorito = f
                this.favorito = favorito
                return
            }
        }
        favorito!!.url = url
        favorito!!.key = ""
        this.favorito = favorito
    }

    private fun comprobarFAV() {
        if (favorito!!.key!!.isEmpty()) {
            menu!!.getItem(0).setIcon(R.drawable.ic_fav_no_pulsado)
            sw = false
        } else {
            menu!!.getItem(0).setIcon(R.drawable.ic_fav_pulsado)
            sw = true
        }
    }

    private fun cargarWeb() {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting) {
            if (!favorito!!.url!!.contains("youtube")) {
                webView!!.loadUrl(favorito!!.url)
            } else {
                val uri = Uri.parse(favorito!!.url)
                val gSearchIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(gSearchIntent)
                finish()
            }
        } else {
            Snackbar.make(findViewById<View>(android.R.id.content), getString(R.string.snack_internet), Snackbar.LENGTH_LONG).show()
            refresh!!.isRefreshing = false
        }
    }
}
