package com.example.seanfung_boggleboard_assignment6

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), ScoreFragment.ScoreFragmentListener, BoardFragment.BoardFragmentListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    override fun newGameClicked() {
        val boardFragment = supportFragmentManager.findFragmentById(R.id.boardFragment) as? BoardFragment
        boardFragment?.newGame()
    }

    override fun updateScore(score: Int) {
        val scoreFragment = supportFragmentManager.findFragmentById(R.id.scoreFragment) as ScoreFragment?
        scoreFragment?.updateScore(score)
    }
}