package com.estrada.newsuty.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.estrada.newsuty.databinding.FragmentMisNoticiasBinding
import com.estrada.newsuty.db.AppDatabase
import com.estrada.newsuty.db.News

class MisNoticiasFragment : Fragment() {
    private lateinit var binding: FragmentMisNoticiasBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var userID = arguments?.getString("USERUID")

        binding = FragmentMisNoticiasBinding.inflate(layoutInflater)
        var db = AppDatabase.getDatabase(requireContext());
        var newsDAO = db.newsDAO()
        val datos = newsDAO.obtenerNewsUsuario(userID!!)
        val adaptador = NewsAdaptador(datos as MutableList<News>, userID, context)

        binding.MisNoticiasRV.adapter = adaptador
        binding.MisNoticiasRV.layoutManager = LinearLayoutManager(activity)
        binding.MisNoticiasRV.setHasFixedSize(true)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MisNoticiasFragment()
    }
}