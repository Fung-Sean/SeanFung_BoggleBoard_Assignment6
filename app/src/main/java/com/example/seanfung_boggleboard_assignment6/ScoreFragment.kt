package com.example.seanfung_boggleboard_assignment6

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seanfung_boggleboard_assignment6.databinding.ScoreFragmentBinding

class ScoreFragment : Fragment() {
    interface ScoreFragmentListener {
        fun newGameClicked()
    }
    var TAG = "ScoreFragment"
    private var listener: ScoreFragmentListener? = null
    private var score: Int = 0
    private var _binding: ScoreFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ScoreFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ScoreFragmentListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateScore(score)
        binding.newGameButton.setOnClickListener {
            listener?.newGameClicked()
            score = 0
            binding.textViewScore.text = "Score: $score"
        }
    }

    fun updateScore(newScore: Int) {
        Log.d(TAG, "Entered UpdateScore!!")
        score += newScore
        binding.textViewScore.text = "Score: $score"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScoreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
