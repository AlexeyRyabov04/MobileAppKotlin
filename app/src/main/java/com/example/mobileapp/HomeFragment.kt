package com.example.mobileapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), MovieAdapter.OnItemClickListener {
    private lateinit var reference: DatabaseReference
    private lateinit var movieList: MutableList<Movie>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        movieList = mutableListOf()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        reference = FirebaseDatabase.getInstance().getReference("movies")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                movieList.clear()
                for (snapshot in dataSnapshot.children) {
                    val movie = snapshot.getValue(Movie::class.java)
                    movie?.let {
                        if (!movieList.contains(it)) {
                            movieList.add(it)
                        }
                    }
                }
                adapter = MovieAdapter(movieList, this@HomeFragment)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to read movies.", databaseError.toException())

            }

        })
        return rootView
    }

    override fun onItemClick(movie: Movie) {
        val fragment = FilmInfoFragment.newInstance(movie, false)
        fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}