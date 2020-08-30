package com.developer.vitapbookhub.Activity.Books

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.firebase.database.*
import com.developer.vitapbookhub.Activity.LibraryBooksActivity
import com.developer.vitapbookhub.Adapter.CivilRecyclerAdapter
import com.developer.vitapbookhub.R
import com.developer.vitapbookhub.model.Book
import kotlinx.android.synthetic.main.activity_civil.*
import java.util.*
import kotlin.Comparator

class CivilActivity : AppCompatActivity() {
    lateinit var recyclercivil: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
lateinit var adLoader:AdLoader
    lateinit var ref: DatabaseReference
    lateinit var booklist:MutableList<Book>
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    var url:String=""
    lateinit var recycleradapter: CivilRecyclerAdapter
    var order=-1
    /*var items_per_ad=8
    var native_ad_height=150
    var ad_unit_id="ca-app-pub-6555297486385984/3480691888"
    */var ratingcomparator=Comparator<Book>{book1,book2->
        book1.bookName.compareTo(book2.bookName,true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_civil)
        SetUpToolbar()
        MobileAds.initialize(this,getString(R.string.admob_app_id))
        val adRequest = AdRequest.Builder().build()
        adViewcivil.loadAd(adRequest)

        adViewcivil.visibility = View.GONE




        progressBar = findViewById(R.id.progressbar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        booklist= mutableListOf()

        recyclercivil=findViewById(R.id.recyclerCivil)

        ref= FirebaseDatabase.getInstance().getReference().child("civil")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                System.out.println(p0)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists())
                {
                    booklist.clear()
                    for(b in p0.children)
                    {
                        val book=b.getValue(Book::class.java)
                        booklist.add(book!!)
                    }
                    layoutManager= LinearLayoutManager(applicationContext)
                    progressLayout.visibility=View.GONE
                    recycleradapter= CivilRecyclerAdapter(applicationContext,booklist)
                    recyclercivil.adapter=recycleradapter
                    recyclercivil.layoutManager=layoutManager

                    recyclercivil.addItemDecoration(
                        DividerItemDecoration(
                            recyclercivil.context,
                            (layoutManager as LinearLayoutManager).orientation
                        )
                    )

                }
            }

        })
        adViewcivil.adListener = object : AdListener(){
            override fun onAdLoaded() {
                adViewcivil.visibility = View.VISIBLE
                super.onAdLoaded()
            }
        }
    }
    fun SetUpToolbar(){
        setSupportActionBar(civiltoolbar)
        supportActionBar?.title="Civil Books"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        when(id)
        {
            android.R.id.home ->{
                startActivity(Intent(this, LibraryBooksActivity::class.java))
                finish()
            }
            R.id.action_sort_inc ->{
                if(order==1) {
                    Collections.sort(booklist, ratingcomparator)
                    order=0
                }
                else if(order==0)
                {
                    Collections.sort(booklist, ratingcomparator)
                    booklist.reverse()
                    order=1
                }
                else
                {
                    Collections.sort(booklist, ratingcomparator)
                    order=0
                }
            }
        }
        recycleradapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
