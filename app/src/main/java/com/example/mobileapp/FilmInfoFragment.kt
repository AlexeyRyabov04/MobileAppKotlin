package com.example.mobileapp

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FilmInfoFragment : DialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_film_info, container, false)
        displayInformation(rootView)
        var btn = rootView.findViewById<Button>(R.id.favouritesButton)
        btn.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val email = FirebaseAuth.getInstance().currentUser?.email
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userSnapshot = dataSnapshot.children.first()
                        val user = userSnapshot.getValue(User::class.java)!!
                        val list : MutableList<Int>
                        list = mutableListOf()
                        for (a in user.favourites){
                            list.add(a)
                        }
                        a
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Ошибка при выполнении запроса: ${databaseError.message}")
                }
            })
        }
        return rootView
    }

    private fun displayInformation(rootView : View){
        viewPager = rootView.findViewById<ViewPager2>(R.id.viewPager)
        sliderAdapter = SliderAdapter()
        viewPager.adapter = sliderAdapter
        val title = rootView.findViewById<TextView>(R.id.titleTextView)
        val producer = rootView.findViewById<TextView>(R.id.producerTextView)
        val description = rootView.findViewById<TextView>(R.id.descriptionTextView)
        title.text = arguments?.getString("title")
        producer.text = arguments?.getString("producer")
        description.text = arguments?.getString("description")
        val id = arguments?.getInt("id")
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("images/$id")
        val list = storageReference.listAll()
        list.addOnSuccessListener { result ->
            result.items.forEach { imageReference ->
                imageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    sliderAdapter.addImage(imageUrl.toString())
                }
            }
        }
            .addOnFailureListener {
            }


    }


    companion object {

        @JvmStatic
        fun newInstance(data: Movie): FilmInfoFragment {
            val fragment = FilmInfoFragment()
            val args = Bundle()
            args.putString("title", data.title)
            args.putInt("id", data.id)
            args.putString("description", data.description)
            args.putString("producer", data.producer)
            fragment.arguments = args
            return fragment
        }
    }
}