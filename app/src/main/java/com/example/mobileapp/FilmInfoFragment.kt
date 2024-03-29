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
    private lateinit var user : User
    private lateinit var userSnapshot : DataSnapshot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_film_info, container, false)
        displayInformation(rootView)
        var btn = rootView.findViewById<Button>(R.id.favouritesButton)
        getUser { user ->
            if (user != null) {
                val id = arguments?.getInt("id")!!
                checkFavourites(btn)
                btn.setOnClickListener {
                    val isInFavorites = checkFavourites(btn)
                    if (isInFavorites){
                        user.favourites.remove(id)
                        btn.text = "В избранное"
                    }
                    else{
                        user.favourites.add(id)
                        btn.text = "Добавлено в избранное"
                    }
                    userSnapshot.ref.setValue(user)
                }
            }
        }
        return rootView
    }

    private fun getUser(completion: (User?) -> Unit){
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val email = FirebaseAuth.getInstance().currentUser?.email
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userSnapshot = dataSnapshot.children.first()
                    user = userSnapshot.getValue(User::class.java)!!
                    completion(user)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Ошибка при выполнении запроса: ${databaseError.message}")
            }
        })
    }
    private fun checkFavourites(btn : Button) : Boolean{
        val id = arguments?.getInt("id")!!
        if (user.favourites.contains(id)) {
            btn.text = "Добавлено в избранное"
            return true
        }
        return false
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
        val bool = arguments?.getBoolean("bool")!!
        val btn = rootView.findViewById<Button>(R.id.favouritesButton)
        if (bool){
            btn.text = "добавлено в избранное"
        }
        else{
            btn.text = "В избранное"
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(data: Movie, isFavourites: Boolean): FilmInfoFragment {
            val fragment = FilmInfoFragment()
            val args = Bundle()
            args.putString("title", data.title)
            args.putInt("id", data.id)
            args.putString("description", data.description)
            args.putString("producer", data.producer)
            args.putBoolean("bool", isFavourites)
            fragment.arguments = args
            return fragment
        }
    }
}