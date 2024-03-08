package com.example.mobileapp

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountFragment : Fragment() {

    lateinit var name : EditText
    lateinit var surname : EditText
    lateinit var nickname : EditText
    lateinit var phone : EditText
    lateinit var userSnapshot : DataSnapshot
    lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_account, container, false)
        getUser(rootView)
        val btn = rootView.findViewById<Button>(R.id.saveButton)

        name = rootView.findViewById<EditText>(R.id.nameEdit)
        nickname = rootView.findViewById<EditText>(R.id.nicknameEdit)
        surname = rootView.findViewById<EditText>(R.id.surnameEdit)
        phone = rootView.findViewById<EditText>(R.id.phoneEdit)
        btn.setOnClickListener{
            val user = User(
                name = name.text.toString(),
                surname = surname.text.toString(),
                phone = phone.text.toString(),
                nick =  nickname.text.toString(),
                email = FirebaseAuth.getInstance().currentUser?.email.toString(),
                favourites = user.favourites
            )
            userSnapshot.ref.setValue(user)
        }
        return rootView
    }

    private fun getUser(rootView: View) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val email = FirebaseAuth.getInstance().currentUser?.email
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userSnapshot = dataSnapshot.children.first()
                    user = userSnapshot.getValue(User::class.java)!!
                    name.setText(user.name)
                    nickname.setText(user.nick)
                    surname.setText(user.surname)
                    phone.setText(user.phone)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Ошибка при выполнении запроса: ${databaseError.message}")
            }
        })
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {

                }

    }
}