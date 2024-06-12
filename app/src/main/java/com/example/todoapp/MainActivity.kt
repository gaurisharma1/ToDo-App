package com.example.todoapp

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

data class TodoItemWithDueDate(val text: String, val dueDate: Date, var isCompleted: Boolean = false)

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<TodoItemWithDueDate> = ArrayList()
    private lateinit var itemsAdapter: ArrayAdapter<TodoItemWithDueDate>
    private lateinit var listView: ListView
    private lateinit var button: Button
    private lateinit var sortButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        button = findViewById(R.id.button)
        sortButton = findViewById(R.id.sortButton)

        button.setOnClickListener { view: View -> addItem(view) }
        sortButton.setOnClickListener { sortItemsAlphabetically() }

        itemsAdapter = object : ArrayAdapter<TodoItemWithDueDate>(this, android.R.layout.simple_list_item_1, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val item = getItem(position)

                if (item != null) {
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.text = formatTodoItem(item)

                    // Set a strike-through style for completed items
                    if (item.isCompleted) {
                        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }

                return view
            }
        }

        listView.adapter = itemsAdapter
        setUpListViewListener()
    }

    private fun formatTodoItem(item: TodoItemWithDueDate): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dueDate = dateFormat.format(item.dueDate)
        return "${item.text} (Due: $dueDate)"
    }

    private fun addItem(view: View) {
        val input = findViewById<EditText>(R.id.editText)
        val itemText = input.text.toString()

        if (itemText.isNotEmpty()) {
            val dueDate = Date() // Replace this with the actual due date selection logic
            val newItem = TodoItemWithDueDate(itemText, dueDate)
            itemsAdapter.add(newItem)
            input.setText("")
        } else {
            Toast.makeText(applicationContext, "Please enter text...", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpListViewListener() {
        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val clickedItem = items[position]
            clickedItem.isCompleted = !clickedItem.isCompleted
            itemsAdapter.notifyDataSetChanged()
        }

        listView.setOnItemLongClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val context: Context = applicationContext
            Toast.makeText(context, "Item Removed", Toast.LENGTH_LONG).show()
            items.removeAt(position)
            itemsAdapter.notifyDataSetChanged()
            true
        }
    }

    private fun sortItemsAlphabetically() {
        items.sortBy { it.text.toLowerCase(Locale.getDefault()) }
        itemsAdapter.notifyDataSetChanged()
    }
}
