package com.example.todolist.activity

import com.example.todolist.database.TaskDatabase.Companion.getInstance
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.example.todolist.R
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.todolist.adapter.TaskAdapter
import android.os.Bundle
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.example.todolist.bottomSheetFragment.CreateTaskBottomSheetFragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.AsyncTask
import com.example.todolist.database.TaskDatabase
import android.app.SearchManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import com.example.todolist.bottomSheetFragment.ShowCalendarViewBottomSheet
import com.example.todolist.model.Task
import java.util.ArrayList

class MainActivity : AppCompatActivity(), SetRefreshListener {
    //    public static final String CHANNEL_ID ="channel1";
    @JvmField
    @BindView(R.id.taskRecycler)
    var taskRecycler: RecyclerView? = null

    @JvmField
    @BindView(R.id.btnAddTask)
    var addTask: FloatingActionButton? = null
    var taskAdapter: TaskAdapter? = null
    var tasks: List<Task> = ArrayList()

    @JvmField
    @BindView(R.id.noDataImage)
    var noDataImage: ImageView? = null

    //nạp chồng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //gọi đến giao diện liên kết - chèn giao diện vào Activity
        ButterKnife.bind(this)
        setUpAdapter()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.toolbar)
        Glide.with(applicationContext).load(R.drawable.new_todo).into(noDataImage!!)
        addTask!!.setOnClickListener { view: View? ->
            val createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
            createTaskBottomSheetFragment.setTaskId(0, false, this, this@MainActivity)
            createTaskBottomSheetFragment.show(
                supportFragmentManager,
                createTaskBottomSheetFragment.tag
            )
        }
        savedTasks
    }

    fun setUpAdapter() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        //Hiển thị danh sách
        taskAdapter = TaskAdapter(this, tasks, this@MainActivity)
        taskRecycler!!.layoutManager = linearLayoutManager
        taskRecycler!!.adapter = taskAdapter
    }

    private val savedTasks: Unit
        private get() {
            class GetSavedTasks : AsyncTask<Void?, Void?, List<Task>>() {
                override fun doInBackground(vararg p0: Void?): List<Task> {
                    tasks = getInstance(applicationContext)
                        .taskDao()
                        .allTasksList
                    return tasks
                }

                override fun onPostExecute(tasks: List<Task>) {
                    super.onPostExecute(tasks)
                    noDataImage!!.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
                    setUpAdapter()
                }
            }

            val savedTasks = GetSavedTasks()
            savedTasks.execute()
        }

    override fun refresh() {
        savedTasks
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchManager = this.getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))
        val theTextArea = searchView.findViewById<View>(R.id.search_src_text) as SearchAutoComplete
        theTextArea.setTextColor(resources.getColor(R.color.colorAccent)) //or any color that you want
        theTextArea.setHintTextColor(resources.getColor(R.color.colorAccent))
        //change icon color
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        searchIcon.setColorFilter(resources.getColor(R.color.colorAccent))
        val searchIconClose =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchIconClose.setColorFilter(resources.getColor(R.color.colorAccent))

        //Event
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                startSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        //Clear text when click to Clear button on Search View
        val closeButton = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener { v: View? ->
            val ed = searchView.findViewById<View>(R.id.search_src_text) as EditText
            //Clear Text
            ed.setText("")
            //Clear Query
            searchView.setQuery("", false)
            //Collapse the action view
            searchView.onActionViewCollapsed()
            //Collapse the search widget
            menuItem.collapseActionView()
            //Restore result to original
            setUpAdapter()
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_calendar) {
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
            val showCalendarViewBottomSheet = ShowCalendarViewBottomSheet()
            showCalendarViewBottomSheet.show(
                supportFragmentManager,
                showCalendarViewBottomSheet.tag
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startSearch(query: String) {
        val searchList: MutableList<Task> = ArrayList()
        for (i in tasks.indices) {
            val task = tasks[i]
            if (task.taskTitle.toLowerCase().contains(query)) {
                searchList.add(task)
            }
            taskAdapter = TaskAdapter(this, searchList, this@MainActivity)
            taskRecycler!!.layoutManager = LinearLayoutManager(applicationContext)
            taskRecycler!!.adapter = taskAdapter
        }
    }
}