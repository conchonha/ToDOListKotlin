package com.example.todolist.adapter

import android.app.AlertDialog
import android.content.Context
import com.example.todolist.activity.MainActivity
import com.example.todolist.activity.SetRefreshListener
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapter.TaskAdapter.TaskViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.todolist.R
import android.os.AsyncTask
import com.example.todolist.database.TaskDatabase
import android.content.DialogInterface
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.todolist.bottomSheetFragment.CreateTaskBottomSheetFragment
import butterknife.BindView
import butterknife.ButterKnife
import com.example.todolist.model.Task
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val context: MainActivity,
    private val taskList: List<Task>,
    var setRefreshListener: SetRefreshListener
) : RecyclerView.Adapter<TaskViewHolder>() {
    private val inflater: LayoutInflater
    var dateFormat = SimpleDateFormat("EE dd MMM yyyy", Locale.US)
    var inputDateFormat = SimpleDateFormat("dd-M-yyyy", Locale.US)
    var date: Date? = null
    var outputDateString: String? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = inflater.inflate(R.layout.item_task, viewGroup, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.title?.text = task.taskTitle
        holder.description?.text = task.taskDescrption
        holder.time?.text = task.lastAlarm
        holder.status?.isChecked = toBoolean(task.isComplete)
        holder.status?.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (holder.status?.isChecked == true) {
                updateStatusID(task.taskId, 1)
            } else {
                updateStatusID(task.taskId, 0)
            }
        }
        holder.options?.setOnClickListener { view: View? -> showPopUpMenu(view, position) }
        try {
            date = inputDateFormat.parse(task.date)
            outputDateString = dateFormat.format(date)
            val items1 = outputDateString?.split(" ")?.toTypedArray()
            if(items1 == null)return
            val day = items1[0]
            val dd = items1[1]
            val month = items1[2]
            holder.day?.text = day
            holder.date?.text = dd
            holder.month?.text = month
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateStatusID(taskId: Int, i: Int) {
        class GetSavedTasks : AsyncTask<Void?, Void?, List<Task>>() {
            override fun doInBackground(vararg p0: Void?): List<Task> {
                TaskDatabase.getInstance(context)
                    .taskDao()
                    .updateStatusRow(taskId, i)
                return taskList
            }
            override fun onPostExecute(tasks: List<Task>) {
                super.onPostExecute(tasks)
                setRefreshListener.refresh()
            }
        }

        val savedTasks = GetSavedTasks()
        savedTasks.execute()
    }

    fun toBoolean(num: Int): Boolean {
        return num != 0
    }

    fun showPopUpMenu(view: View?, position: Int) {
        val task = taskList[position]
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menuDelete -> {
                    val alertDialogBuilder = AlertDialog.Builder(
                        context, R.style.AppTheme_Dialog
                    )
                    alertDialogBuilder.setTitle(R.string.delete_confirmation)
                        .setMessage(R.string.sureToDelete)
                        .setPositiveButton(R.string.yes) { dialog: DialogInterface?, which: Int ->
                            deleteTaskFromId(
                                task.taskId,
                                position
                            )
                        }
                        .setNegativeButton(R.string.no) { dialog: DialogInterface, which: Int -> dialog.cancel() }
                        .show()
                }
                R.id.menuUpdate -> {
                    val createTaskBottomSheetFragment = CreateTaskBottomSheetFragment()
                    createTaskBottomSheetFragment.setTaskId(task.taskId, true, context, context)
                    createTaskBottomSheetFragment.show(
                        context.supportFragmentManager,
                        createTaskBottomSheetFragment.tag
                    )
                }
            }
            false
        }
        popupMenu.show()
    }

    //    public void showCompleteDialog(int taskId, int position) {
    //        Dialog dialog = new Dialog(context, R.style.AppTheme);
    //        dialog.setContentView(R.layout.dialog_completed_theme);
    //        Button close = dialog.findViewById(R.id.closeButton);
    //        close.setOnClickListener(view -> {
    //            deleteTaskFromId(taskId, position);
    //            dialog.dismiss();
    //        });
    //        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    //        dialog.show();
    //    }
    private fun deleteTaskFromId(taskId: Int, position: Int) {
        class GetSavedTasks : AsyncTask<Void?, Void?, List<Task>>() {
            override fun doInBackground(vararg p0: Void?): List<Task> {
                TaskDatabase.getInstance(context)
                    .taskDao()
                    .deleteTaskFromId(taskId)
                return taskList
            }

            override fun onPostExecute(tasks: List<Task>) {
                super.onPostExecute(tasks)
                removeAtPosition(position)
                setRefreshListener.refresh()
            }
        }

        val savedTasks = GetSavedTasks()
        savedTasks.execute()
    }

    private fun removeAtPosition(position: Int) {
        taskList.toMutableList().removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, taskList.size)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class TaskViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        @JvmField
        @BindView(R.id.day)
        var day: TextView? = null

        @JvmField
        @BindView(R.id.date)
        var date: TextView? = null

        @JvmField
        @BindView(R.id.month)
        var month: TextView? = null

        @JvmField
        @BindView(R.id.title)
        var title: TextView? = null

        @JvmField
        @BindView(R.id.description)
        var description: TextView? = null

        @JvmField
        @BindView(R.id.checkBtn)
        var status: CheckBox? = null

        @JvmField
        @BindView(R.id.options)
        var options: ImageView? = null

        @JvmField
        @BindView(R.id.time)
        var time: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}