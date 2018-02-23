package nathan.com.practica_qr_kotlin

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private var tabLayout: TabLayout? = null
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
            }
        }


        val fragA = FragmentFav()
        val fragB = FragmentPerfil()

        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // set tab, view pager
        tabLayout = findViewById<TabLayout>(R.id.tab_layout) as TabLayout
        val viewPager = findViewById<MyViewPager>(R.id.pager) as MyViewPager

        val manager = supportFragmentManager as FragmentManager
        val adapter = AdaptadorVPMain(manager)

        adapter.addFragment(fragA, "FAVORITOS")
        adapter.addFragment(fragB, "PERFIL")

        viewPager.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                tabLayout!!.setScrollPosition(position, 0f, true)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(authListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth!!.removeAuthStateListener(authListener!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.opt_qr -> {
                val i = Intent(this, ReaderActivity::class.java)
                startActivity(i)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
