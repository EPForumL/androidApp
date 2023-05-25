package com.github.ybecker.epforuml.features.latex

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.github.ybecker.epforuml.R
//import katex.hourglass.`in`.mathlib.MathView

/**
 * Custom dialog class that shows a dialog containing a latex renderer. You can enter latex code
 * and show the rendered result directly by clicking the render button. You can save the latex code
 * into the targetEditText and exit by clicking the save button or just exit by clicking cancel.
 *
 * @param context: The base activity context
 * @param targetEditText: The editText that will contain the saved text
 */
class LatexDialog(context: Context, private val targetEditText: EditText?) : Dialog(context) {
    private val editText: EditText
    private val latexView: MathView
    private val renderButton: Button
    private val saveButton: Button
    private val cancelButton: Button

    init {
        // Initialize the layout and components
        setContentView(R.layout.latex_window)
        editText = findViewById(R.id.latex_editText)
        latexView = findViewById(R.id.latex_mathView)
        renderButton = findViewById(R.id.latex_render_button)
        saveButton = findViewById(R.id.latex_save_button)
        cancelButton = findViewById(R.id.latex_cancel_button)

        renderButton.setOnClickListener { updateLatexView(editText.text.toString()) }

        // Save text and close dialog
        saveButton.setOnClickListener {
            targetEditText?.text = editText.text
            cancel()
        }

        // Just close the dialog without saving
        cancelButton.setOnClickListener { cancel() }
    }

    override fun show() {
        // Fill the latex editText with the text already filled before calling the dialog and
        // update the latex mathView
        editText.text = targetEditText?.text
        updateLatexView(targetEditText?.text.toString())
        super.show()
    }

    private fun updateLatexView(text: String) {
        // Update the latex mathView with the given text string
        latexView.setDisplayText(text)
    }
}