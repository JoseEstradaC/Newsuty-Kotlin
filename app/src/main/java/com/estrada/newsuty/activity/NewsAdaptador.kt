package com.estrada.newsuty.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.estrada.newsuty.R
import com.estrada.newsuty.databinding.ListaNewsItemBinding
import com.estrada.newsuty.db.AppDatabase
import com.estrada.newsuty.db.News
import com.estrada.newsuty.db.NewsVotes
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.android.material.snackbar.Snackbar
import java.util.*

class NewsAdaptador(
    var datos: MutableList<News>,
    private var userID: String?,
    context: Context?,
) : RecyclerView.Adapter<NewsAdaptador.NewsContenedor>() {
    val gestionarPulsacionCorta: (News) -> Unit = {
        if (!it.url.isNullOrEmpty()) {
            fun Uri?.openInBrowser(context: Context) {
                this ?: return
                val browserIntent = Intent(Intent.ACTION_VIEW, this)
                ContextCompat.startActivity(context, browserIntent, null)
            }

            fun String?.asUri(): Uri? {
                return try {
                    Uri.parse(this)
                } catch (e: Exception) {
                    null
                }
            }
            it.url.asUri()?.openInBrowser(context!!)
        }
    }
    val gestionarPulsacionLarga: (MenuItem, News, ListaNewsItemBinding) -> Boolean = { item, news, binding ->
        when (item.itemId) {
            R.id.ListaMenuDelete -> {
                val db = AppDatabase.getDatabase(context!!);
                val votesDAO = db.voteDAO()
                val newsDAO = db.newsDAO()
                val votes = votesDAO.getNewsVotes(news.newsID)

                if (news.userCreatorUID == userID!!)
                {
                    if (votes.users.isNotEmpty()) {
                        votes.users.forEach {
                            val newsVotes = votesDAO.userHasVote(news.newsID, it.userUID)
                            votesDAO.borrarVote(newsVotes)
                        }
                    }
                    newsDAO.borrarNews(news)
                    datos.remove(news)
                    notifyDataSetChanged()
                    Snackbar.make(binding.root, R.string.Registro_nesw_del, Snackbar.LENGTH_SHORT).show()
                }
                else
                {
                    Snackbar.make(binding.root, R.string.Registro_nesw_del_permision, Snackbar.LENGTH_SHORT).show()
                }
            }
            R.id.ListaMenuVotaLike -> {
                val db = AppDatabase.getDatabase(context!!);
                val voteDAO = db.voteDAO()
                val newsVote = voteDAO.userHasVote(news.newsID, userID!!)
                if (newsVote == null) {
                    voteDAO.insertarVote(NewsVotes(news.newsID, userID!!, true))
                    notifyDataSetChanged()
                    Snackbar.make(binding.root, R.string.news_vote, Snackbar.LENGTH_SHORT).show()
                } else {
                    if (newsVote.esLike) {
                        voteDAO.borrarVote(newsVote)
                        notifyDataSetChanged()
                        Snackbar.make(binding.root, R.string.news_vote_del, Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(binding.root, R.string.news_vote_del_first, Snackbar.LENGTH_SHORT).show()
                    }
                }
                true
            }
            R.id.ListaMenuVotaDislike -> {
                var db = AppDatabase.getDatabase(context!!);
                var voteDAO = db.voteDAO()
                val newsVote = voteDAO.userHasVote(news.newsID, userID!!)
                if (newsVote == null) {
                    voteDAO.insertarVote(NewsVotes(news.newsID, userID!!, false))
                    notifyDataSetChanged()
                    Snackbar.make(binding.root, R.string.news_vote, Snackbar.LENGTH_SHORT).show()
                } else {
                    if (newsVote.esLike) {
                        Snackbar.make(binding.root, R.string.news_vote_del_first, Snackbar.LENGTH_SHORT).show()
                    } else {
                        voteDAO.borrarVote(newsVote)
                        notifyDataSetChanged()
                        Snackbar.make(binding.root, R.string.news_vote_del, Snackbar.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
        false
    }

    override fun getItemCount(): Int = datos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsContenedor {
        val inflador = LayoutInflater.from(parent.context)
        val binding = ListaNewsItemBinding.inflate(inflador, parent, false)

        return NewsContenedor(binding)
    }

    override fun onBindViewHolder(holder: NewsContenedor, position: Int) {
        holder.bindNews(datos[position])
    }

    inner class NewsContenedor(val binding: ListaNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindNews(news: News) {
            binding.listaNewsTitulo.text = news.titulo
            val LocaleBylanguageTag: Locale = Locale.forLanguageTag("es")
            val messages: TimeAgoMessages =
                TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build()
            val text = TimeAgo.using(news.fechaPublicacion, messages)

            binding.listaNewsTimeAgo.text = text
            val db = AppDatabase.getDatabase(binding.root.context);
            val voteDAO = db.voteDAO()
            val likes = voteDAO.getNewsLikeVotes(news.newsID).size
            val dislike = voteDAO.getNewsDislikeVotes(news.newsID).size
            val votos = likes - dislike
            val textVotos = "$votos puntos"
            val userVote = voteDAO.userHasVote(news.newsID, userID!!)

            if (userVote !== null) {
                if (userVote.esLike) {
                    binding.listaNewsVote.setTextColor(Color.parseColor("#4CAF50"))
                } else {
                    binding.listaNewsVote.setTextColor(Color.parseColor("#AF4C4C"))
                }
            } else {
                binding.listaNewsVote.setTextColor(Color.parseColor("#FF000000"))
            }

            binding.listaNewsVote.text = textVotos


            Glide.with(binding.root)
                .load(news.urlImagen)
                .into(binding.listaNewsImagen)

            binding.root.setOnClickListener { gestionarPulsacionCorta(news) }

            binding.root.setOnLongClickListener {
                val pop = PopupMenu(binding.root.context, binding.listaNewsTitulo)
                pop.inflate(R.menu.lista_news_menu)
                pop.setOnMenuItemClickListener {
                    gestionarPulsacionLarga(it, news, binding)
                }

                pop.show()

                true
            }
        }
    }
}