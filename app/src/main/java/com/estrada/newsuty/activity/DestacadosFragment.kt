package com.estrada.newsuty.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.estrada.newsuty.databinding.FragmentDestacadosBinding
import com.estrada.newsuty.db.AppDatabase
import com.estrada.newsuty.db.News

class DestacadosFragment : Fragment() {
    private lateinit var binding: FragmentDestacadosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var userID = arguments?.getString("USERUID")

        binding = FragmentDestacadosBinding.inflate(layoutInflater)
        var db = AppDatabase.getDatabase(requireContext());
        var newsDAO = db.newsDAO()
        var voteDAO = db.voteDAO()
        var userDAO = db.userDAO()
        var datos: MutableList<News> = newsDAO.obtenerNews() as MutableList<News>

        datos.sortByDescending {
            val likes = voteDAO.getNewsLikeVotes(it.newsID).size
            val dislikes = voteDAO.getNewsDislikeVotes(it.newsID).size

            likes - dislikes
        }

        val isSpanish = userDAO.isSpanish(userID!!);
        datos = datos.filter { it.spanish == isSpanish} as MutableList<News>
        val adaptador = NewsAdaptador(datos, userID, context)

        binding.DestacadosRV.adapter = adaptador
        binding.DestacadosRV.layoutManager = LinearLayoutManager(activity)
        binding.DestacadosRV.setHasFixedSize(true)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = DestacadosFragment()
    }
}