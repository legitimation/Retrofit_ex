package com.example.retrofit_ex

import androidx.appcompat.app.AppCompatActivity
import retrofit2.Retrofit
import com.example.retrofit_ex.RetrofitInterface
import android.os.Bundle
import com.example.retrofit_ex.R
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.EditText
import com.example.retrofit_ex.LoginResult
import android.content.Intent
import android.view.View
import android.widget.Button
import com.example.retrofit_ex.MainChatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null
    private val BASE_URL = "http://192.249.18.137:80"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().also { retrofit = it }

        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)
        findViewById<View>(R.id.login).setOnClickListener { handleLoginDialog() }
        findViewById<View>(R.id.signup).setOnClickListener { handleSignupDialog() }
    }

    private fun handleLoginDialog() {
        val view = layoutInflater.inflate(R.layout.login_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view).show()
        val loginBtn = view.findViewById<Button>(R.id.login)
        val emailEdit = view.findViewById<EditText>(R.id.emailEdit)
        val passwordEdit = view.findViewById<EditText>(R.id.passwordEdit)
        loginBtn.setOnClickListener {
            val map = HashMap<String, String>()
            map["email"] = emailEdit.text.toString()
            map["password"] = passwordEdit.text.toString()
            val call = retrofitInterface!!.executeLogin(map)
            call.enqueue(object : Callback<LoginResult?> {
                override fun onResponse(
                    call: Call<LoginResult?>,
                    response: Response<LoginResult?>
                ) {
                    if (response.code() == 200) {
                        val result = response.body()
                        val builder1 = AlertDialog.Builder(this@MainActivity)
                        builder1.setTitle(result!!.name)
                        builder1.setMessage(result.email)
                        builder1.show()
                        val intent = Intent(this@MainActivity, MainChatActivity::class.java)
                        startActivity(intent)
                    } else if (response.code() == 404) {
                        Toast.makeText(
                            this@MainActivity, "Wrong Credentials",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResult?>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity, t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun handleSignupDialog() {
        val view = layoutInflater.inflate(R.layout.signup_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view).show()
        val signupBtn = view.findViewById<Button>(R.id.signup)
        val nameEdit = view.findViewById<EditText>(R.id.nameEdit)
        val emailEdit = view.findViewById<EditText>(R.id.emailEdit)
        val passwordEdit = view.findViewById<EditText>(R.id.passwordEdit)
        signupBtn.setOnClickListener {
            val map = HashMap<String, String>()
            map["name"] = nameEdit.text.toString()
            map["email"] = emailEdit.text.toString()
            map["password"] = passwordEdit.text.toString()
            val call = retrofitInterface!!.executeSignup(map)
            call.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if (response.code() == 200) {
                        Toast.makeText(
                            this@MainActivity,
                            "Signed up successfully", Toast.LENGTH_LONG
                        ).show()
                    } else if (response.code() == 400) {
                        Toast.makeText(
                            this@MainActivity,
                            "Already registered", Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity, t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
}