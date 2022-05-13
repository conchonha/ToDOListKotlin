package com.example.todolist.bottomSheetFragment

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import butterknife.Unbinder
import com.example.todolist.activity.MainActivity
import butterknife.BindView
import com.example.todolist.R
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import android.app.Dialog
import butterknife.ButterKnife
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import com.applandeo.materialcalendarview.CalendarView
import com.example.todolist.database.TaskDatabase
import com.applandeo.materialcalendarview.EventDay
import com.example.todolist.model.Task
import java.util.*

class ShowCalendarViewBottomSheet : BottomSheetDialogFragment() {
    var unbinder: Unbinder? = null
    var activity: MainActivity? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.calendarView)
    var calendarView: CalendarView? = null
    var tasks: List<Task> = ArrayList()
    var tasksToday: List<Task> = ArrayList()
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_calendar_view, null)
        unbinder = ButterKnife.bind(this, contentView)
        dialog.setContentView(contentView)
        calendarView!!.setHeaderColor(R.color.colorAccent)
        savedTasks
        back!!.setOnClickListener { view: View? -> dialog.dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }//kết thúc tiến trình - nhận kết quả từ doInBackground

    //thực hiện run
//chỉ thực hiện tính toán nền - ko trả ra giao diện
    //(thread) đồng bộ danh sách task
    private val savedTasks: Unit
        private get() {
            class GetSavedTasks : AsyncTask<Void?, Void?, List<Task>>() {
                override fun doInBackground(vararg p0: Void?): List<Task> {
                    tasks = TaskDatabase
                        .getInstance(getActivity())
                        .taskDao()
                        .allTasksList
                    return tasks
                }

                override fun onPostExecute(tasks: List<Task>) { //kết thúc tiến trình - nhận kết quả từ doInBackground
                    super.onPostExecute(tasks)
                    calendarView!!.setEvents(highlitedDays)
                }
            }

            val savedTasks = GetSavedTasks()
            savedTasks.execute() //thực hiện run
        }

    //Lấy ngày
    val highlitedDays:

//            Calendar currentDate = Calendar.getInstance();
//            if (currentDate.after(calendar)) {
//                tasksToday.add(tasks.get(i));
//            }
            List<EventDay>
        get() {
            val events: MutableList<EventDay> = ArrayList()
            for (i in tasks.indices) {
                val calendar = Calendar.getInstance()
                val items1 = tasks[i].date.split("-").toTypedArray() //Lấy ngày
                val dd = items1[0]
                val month = items1[1]
                val year = items1[2]
                calendar[Calendar.DAY_OF_MONTH] = dd.toInt()
                calendar[Calendar.MONTH] = month.toInt() - 1
                calendar[Calendar.YEAR] = year.toInt()

//            Calendar currentDate = Calendar.getInstance();
//            if (currentDate.after(calendar)) {
//                tasksToday.add(tasks.get(i));
//            }
                events.add(EventDay(calendar, R.drawable.dot))
            }
            return events
        }
}