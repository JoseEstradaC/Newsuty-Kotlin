package com.estrada.newsuty.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.estrada.newsuty.databinding.FragmentUltimaHoraBinding
import com.estrada.newsuty.db.AppDatabase
import com.estrada.newsuty.db.News

class UltimaHoraFragment : Fragment() {
    private lateinit var binding: FragmentUltimaHoraBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var userID = arguments?.getString("USERUID")

        binding = FragmentUltimaHoraBinding.inflate(layoutInflater)
        var db = AppDatabase.getDatabase(requireContext());
        var newsDAO = db.newsDAO()
        var datos = newsDAO.obtenerRecentNews()
        var userDAO = db.userDAO()

        val isSpanish = userDAO.isSpanish(userID!!);
        datos = datos.filter { it.spanish == isSpanish} as MutableList<News>
        val adaptador = NewsAdaptador(datos as MutableList<News>, userID, context)
        binding.UltimaHoraRV.adapter = adaptador
        binding.UltimaHoraRV.layoutManager = LinearLayoutManager(activity)
        binding.UltimaHoraRV.setHasFixedSize(true)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = UltimaHoraFragment()
    }
}