package com.example.weisnake




import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : Activity() {

    // Initialize variables
    private lateinit var sharedPreferences: SharedPreferences
    private var highScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences for storing high score
        sharedPreferences = getSharedPreferences("HighScore", Context.MODE_PRIVATE)
        highScore = sharedPreferences.getInt("highScore", 0)

        // Find views by their IDs
        val board = findViewById<RelativeLayout>(R.id.board)
        val border = findViewById<RelativeLayout>(R.id.relativeLayout)
        val lilu = findViewById<LinearLayout>(R.id.lilu)
        val upButton = findViewById<Button>(R.id.up)
        val downButton = findViewById<Button>(R.id.down)
        val leftButton = findViewById<Button>(R.id.left)
        val rightButton = findViewById<Button>(R.id.right)
        val pauseButton = findViewById<Button>(R.id.pause)
        val newgame = findViewById<Button>(R.id.new_game)
        val resume = findViewById<Button>(R.id.resume)
        val playagain = findViewById<Button>(R.id.playagain)
        val score = findViewById<TextView>(R.id.score)
        val score2 = findViewById<TextView>(R.id.score2)
        val meat = ImageView(this)
        val snake = ImageView(this)
        val snakeSegments =
            mutableListOf(snake) // Keep track of the position of each snake segment
        val handler = Handler()
        var delayMillis = 30L // Update snake position every 100 milliseconds
        var currentDirection = "→" // Start moving right by default
        var scorex = 0

        // Initialize visibility of UI elements
        board.visibility = View.INVISIBLE
        playagain.visibility = View.INVISIBLE
        score.visibility = View.INVISIBLE
        score2.visibility = View.VISIBLE

        // Start a new game when "New Game" button is clicked
        newgame.setOnClickListener {
            board.visibility = View.VISIBLE
            newgame.visibility = View.INVISIBLE
            resume.visibility = View.INVISIBLE
            score2.visibility = View.VISIBLE

            // Initialize snake and meat positions
            snake.setImageResource(R.drawable.snake)
            snake.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(snake)
            snakeSegments.add(snake) // Add the new snake segment to the list

            var snakeX = snake.x
            var snakeY = snake.y

            meat.setImageResource(R.drawable.meat)
            meat.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            board.addView(meat)
//position of meat
            val random = Random() // create a Random object
            val randomX =
                random.nextInt(801) - 400 // generate a random x-coordinate between -400 and 400
            val randomY =
                random.nextInt(801) - 400 // generate a random y-coordinate between -400 and 400

            meat.x = randomX.toFloat()
            meat.y = randomY.toFloat()

            // Function to check collision with food (meat)
            fun checkFoodCollision() {
                val distanceThreshold = 50

                val distance =
                    sqrt((snake.x - meat.x).pow(2) + (snake.y - meat.y).pow(2))

                if (distance < distanceThreshold) { // Check if the distance between the snake head and the meat is less than the threshold

                    // Add a new segment to the snake
                    val newSnake =
                        ImageView(this) // Create a new ImageView for the additional snake segment
                    newSnake.setImageResource(R.drawable.snake)
                    newSnake.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    board.addView(newSnake)

                    snakeSegments.add(newSnake) // Add the new snake segment to the list

                    // Move the meat to a new random position
                    val randomX =
                        random.nextInt(801) - -100
                    val randomY =
                        random.nextInt(801) - -100

                    meat.x = randomX.toFloat()
                    meat.y = randomY.toFloat()

                    delayMillis-- // Reduce delay value by 1
                    scorex++

                    score2.text = "score : " + scorex.toString() // Update delay text view

                    // Update high score if necessary
                    if (scorex > highScore) {
                        highScore = scorex
                        val editor = sharedPreferences.edit()
                        editor.putInt("highScore", highScore)
                        editor.apply()
                    }
                }
            }
// Runnable to update snake position
            val runnable = object : Runnable {
                override fun run() {

                    // Update the position of each snake segment except for the head
                    for (i in snakeSegments.size - 1 downTo 1) { // Update the position of each snake segment except for the head
                        snakeSegments[i].x = snakeSegments[i - 1].x
                        snakeSegments[i].y = snakeSegments[i - 1].y
                    }
                    // Move snake according to current direction
                    when (currentDirection) {
                        "↑" -> {
                            snakeY -= 10
                            if (snakeY < -490) { // Check if the ImageView goes off the top of the board

                                // Handle game over condition
                                snakeY = -490f
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE

                                score.text = "your score is  " + scorex.toString() // Update delay text view
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE

                            }

                            snake.translationY = snakeY
                        }
                        "↓" -> {
                            snakeY += 10
                            val maxY =
                                board.height / 2 - snake.height + 30 // Calculate the maximum y coordinate
                            if (snakeY > maxY) { // Check if the ImageView goes off the bottom of the board
                                snakeY = maxY.toFloat()
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE

                                score.text = "your score is  " + scorex.toString() // Update delay text view
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE
                            }
                            snake.translationY = snakeY
                        }
                        "←" -> {
                            snakeX -= 10
                            if (snakeX < -490) { // Check if the ImageView goes off the top of the board
                                snakeX = -490f
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE
                                score.text = "your score is  " + scorex.toString() // Update delay text view
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE
                            }
                            snake.translationX = snakeX
                        }
                        "→" -> {
                            snakeX += 10
                            val maxX =
                                board.height / 2 - snake.height + 30 // Calculate the maximum y coordinate
                            if (snakeX > maxX) { // Check if the ImageView goes off the bottom of the board
                                snakeX = maxX.toFloat()
                                border.setBackgroundColor(resources.getColor(R.color.red))
                                playagain.visibility = View.VISIBLE
                                currentDirection = "pause"
                                lilu.visibility = View.INVISIBLE

                                score.text = "your score is  " + scorex.toString() // Update delay text view
                                score.visibility = View.VISIBLE
                                score2.visibility = View.INVISIBLE
                            }
                            snake.translationX = snakeX
                        }
                        "pause" -> {
                            snakeX += 0
                            snake.translationX = snakeX
                        }
                    }

                    checkFoodCollision()
                    handler.postDelayed(this, delayMillis)
                }
            }

            handler.postDelayed(runnable, delayMillis)

            // Set button onClickListeners to update the currentDirection variable when pressed
            upButton.setOnClickListener {
                currentDirection = "↑"
            }
            downButton.setOnClickListener {
                currentDirection = "↓"
            }
            leftButton.setOnClickListener {
                currentDirection = "←"
            }
            rightButton.setOnClickListener {
                currentDirection = "→"
            }
            pauseButton.setOnClickListener {
                currentDirection = "pause"
                board.visibility = View.INVISIBLE
                newgame.visibility = View.VISIBLE
                resume.visibility = View.VISIBLE
            }
            resume.setOnClickListener {
                currentDirection = "→"
                board.visibility = View.VISIBLE
                newgame.visibility = View.INVISIBLE
                resume.visibility = View.INVISIBLE
            }
            playagain.setOnClickListener {
                recreate()
            }
        }

        // Display high score
        score2.text = "High Score: $highScore"
    }
}