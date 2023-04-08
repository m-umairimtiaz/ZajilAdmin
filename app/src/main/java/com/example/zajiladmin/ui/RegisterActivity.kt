package com.example.zajiladmin.ui

import KemsViewModel
import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.zajiladmin.R
import com.example.zajiladmin.repository.KemsRepository
import com.example.zajiladmin.utils.Resource
import com.gicproject.pdadeviceappkotlin.ui.viewmodel.KemsViewModelProviderFactory

class RegisterActivity : AppCompatActivity() {


    private lateinit var viewModel: KemsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        supportActionBar!!.hide()

        val findAssetRepository = KemsRepository()
        val viewModelProviderFactory =
            KemsViewModelProviderFactory(
                application,
                findAssetRepository
            )
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(KemsViewModel::class.java)

        clickListener()
        setObserver()
    }

    fun setObserver(){
        val progressbar = findViewById<ProgressBar>(R.id.mRegisterProgressbar)

        val registerButton = findViewById<Button>(R.id.mRegister)
       viewModel.verifyEmailLiveData.observe(this, Observer {response ->

           when (response) {
               is Resource.Success -> {

                   var myData = response.data
                   progressbar.visibility = View.GONE
                   registerButton.isEnabled = true

                   if(response.data?.status == 1){
                       Toast.makeText(this,myData?.data?.otpCode + "Email Sent Successfully",Toast.LENGTH_LONG).show()
                   }else{
                       Toast.makeText(this,myData?.message,Toast.LENGTH_LONG).show()

                   }
               }

               is Resource.Loading -> {
                   progressbar.visibility = View.VISIBLE
                   progressbar.bringToFront()
                   registerButton.isEnabled = false


               }

               is Resource.Error -> {
                   var errorMessage = response.message
                   progressbar.visibility = View.GONE
                   registerButton.isEnabled = true

                   Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show()



               }
           }
       })

    }



  fun clickListener(){

      var registerButton = findViewById<Button>(R.id.mRegister)
      var emailTextField = findViewById<EditText>(R.id.mEditTextEmail)
      var nameTextField = findViewById<EditText>(R.id.mEditTextName)
      var passwordTextField = findViewById<EditText>(R.id.mEditTextPassword)

      registerButton.setOnClickListener {
          if(emailTextField.text.trim().isEmpty()){
              emailTextField.setError("Email Required")
          }else if(nameTextField.text.trim().isEmpty()){
              nameTextField.setError("Name Required")
          }else if(passwordTextField.text.trim().isEmpty()){
              passwordTextField.setError("Password Required")
          }
          else{
              viewModel.getVerifyModel(emailTextField.text.toString().trim())
          }

      }

    }
}