package com.estrada.newsuty.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.estrada.newsuty.R
import com.estrada.newsuty.activity.MainActivity
import com.estrada.newsuty.databinding.FragmentDialogIdiomaBinding

class IdiomaDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentDialogIdiomaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogIdiomaBinding.inflate(layoutInflater)
        val sharedPref = context?.getSharedPreferences("idioma", Context.MODE_PRIVATE)
        if (sharedPref?.getString("idioma", "ES").equals("ES")) {
            binding.idiomaSelector.check(R.id.idiomaRadioEspañol)
        }
        else
            binding.idiomaSelector.check(R.id.idiomaRadioIngles)
        binding.idiomaCancel.setOnClickListener{
            dismiss()
        }
        binding.idiomaOk.setOnClickListener{
            val select = binding.idiomaSelector.checkedRadioButtonId
            val radio = binding.root.findViewById<RadioButton>(select)

            if (radio.id == R.id.idiomaRadioEspañol)
                sharedPref?.edit()?.putString("idioma", "ES")?.apply()
            else
                sharedPref?.edit()?.putString("idioma", "EN")?.apply()
            Toast.makeText(binding.root.context, "Idioma Actualizado", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return binding.root;
    }
}