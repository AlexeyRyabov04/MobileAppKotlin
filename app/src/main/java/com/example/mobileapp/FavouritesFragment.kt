package com.example.mobileapp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouritesFragment : Fragment(), MovieAdapter.OnItemClickListener {

    private lateinit var reference: DatabaseReference
    private lateinit var movieList: MutableList<Movie>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private  lateinit var moviesID: MutableList<Int>
    lateinit var user : User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_favourites, container, false)
        getUser(rootView)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        movieList = mutableListOf()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val a = moviesID.size
        for (movieIdToFind in moviesID) {
            val moviesReference = FirebaseDatabase.getInstance().getReference("movies")

            val query = moviesReference.orderByChild("id").equalTo(movieIdToFind.toDouble())

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    movieList.clear()
                    for (movieSnapshot in dataSnapshot.children) {
                        val movie = movieSnapshot.getValue(Movie::class.java)
                        movie?.let {
                            if (!movieList.contains(it)) {
                                movieList.add(it)
                            }
                        }
                    }
                    adapter = MovieAdapter(movieList, this@FavouritesFragment)
                    recyclerView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Ошибка при получении фильма: ${error.message}")
                }
            })
        }
        return rootView
    }
    private fun getUser(rootView: View) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val email = FirebaseAuth.getInstance().currentUser?.email
        moviesID = mutableListOf<Int>()
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userSnapshot = dataSnapshot.children.first()
                    user = userSnapshot.getValue(User::class.java)!!
                    for (fav in user.favourites){
                        moviesID.add(fav)

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Ошибка при выполнении запроса: ${databaseError.message}")
            }
        })

    }

    override fun onItemClick(movie: Movie) {
        val fragment = FilmInfoFragment.newInstance(movie)
        fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {

            }
    }
}