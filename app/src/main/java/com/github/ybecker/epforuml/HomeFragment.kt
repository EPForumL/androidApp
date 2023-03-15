package com.github.ybecker.epforuml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybecker.epforuml.database.FirebaseDatabaseAdapter
import com.github.ybecker.epforuml.database.Model

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var adapter : ForumAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var questionsList : MutableList<String> // switch to questions when able to transfer data from mainActivtiy

    /*
    lateinit var questions = Array<String> ??? // is this needed ?
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Initialize question array
     */
    private fun questionsRetrieval() {
        questionsList = mutableListOf(
            "What about me ?",
            "What about us ?",
            "An especially looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong question because" +
                    "we need to be able to test it and for now we have not had another way of testing it iti it " +
                    "it it it it it it it it it it it it it it it it it it it it it it it it it it it " +
                    "it it it it it it it it it it it it it it it it it it it it it it it it it it it " +
                    "it it it it it it it it it it it it it it it it it it it it it it it it it it it it it ",
            "last question I promise"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // remove when using mockDB
        questionsRetrieval()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_forum)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true) // maybe change that later

        adapter = ForumAdapter(questionsList)
        recyclerView.adapter = adapter
    }
}