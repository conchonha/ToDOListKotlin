package com.example.todolist.bottomSheetFragment

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import butterknife.Unbinder
import butterknife.BindView
import com.example.todolist.R
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import com.example.todolist.activity.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.annotation.RequiresApi
import android.os.Build
import android.annotation.SuppressLint
import butterknife.ButterKnife
import android.view.MotionEvent
import android.app.Dialog
import android.os.AsyncTask
import android.view.View
import android.widget.*
import com.example.todolist.activity.SetRefreshListener
import com.example.todolist.database.TaskDatabase
import com.example.todolist.model.Task
import java.util.*

//???
class CreateTaskBottomSheetFragment : BottomSheetDialogFragment() {
    var unbinder: Unbinder? = null

    @JvmField
    @BindView(R.id.addTaskTitle)
    var addTaskTitle: EditText? = null

    @JvmField
    @BindView(R.id.addTaskDescription)
    var addTaskDescription: EditText? = null

    @JvmField
    @BindView(R.id.taskDate)
    var taskDate: EditText? = null

    @JvmField
    @BindView(R.id.taskTime)
    var taskTime: EditText? = null

    //    @BindView(R.id.taskEvent)
    //    EditText taskEvent;
    @JvmField
    @BindView(R.id.addTask)
    var addTask: Button? = null
    var taskId = 0
    var isEdit = false
    var task: Task? = null
    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var mHour = 0
    var mMinute = 0
    var SetRefreshListener: SetRefreshListener? = null
    var alarmManager: AlarmManager? = null
    var timePickerDialog: TimePickerDialog? = null
    var datePickerDialog: DatePickerDialog? = null
    var activity: MainActivity? = null
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    fun setTaskId(
        taskId: Int,
        isEdit: Boolean,
        SetRefreshListener: SetRefreshListener?,
        activity: MainActivity?
    ) {
        this.taskId = taskId
        this.isEdit = isEdit
        this.activity = activity
        this.SetRefreshListener = SetRefreshListener
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_create_task, null)
        unbinder = ButterKnife.bind(this, contentView)
        dialog.setContentView(contentView)
        addTask?.setOnClickListener { view: View? -> if (validateFields()) createTask() }
        if (isEdit) {
            showTaskFromId()
        }
        taskDate?.setOnTouchListener { view: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_MONTH]
                datePickerDialog = DatePickerDialog(
                    getActivity()!!,
                    { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        taskDate?.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                        datePickerDialog?.dismiss()
                    }, mYear, mMonth, mDay
                )
                datePickerDialog?.datePicker?.minDate = System.currentTimeMillis() - 1000
                datePickerDialog?.show()
            }
            true
        }
        taskTime?.setOnTouchListener { view: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // Get Current Time
                val c = Calendar.getInstance()
                mHour = c[Calendar.HOUR_OF_DAY]
                mMinute = c[Calendar.MINUTE]

                // Launch Time Picker Dialog
                timePickerDialog = TimePickerDialog(getActivity(),
                    { view12: TimePicker?, hourOfDay: Int, minute: Int ->
                        taskTime?.setText("$hourOfDay:$minute")
                        timePickerDialog?.dismiss()
                    }, mHour, mMinute, false
                )
                timePickerDialog?.show()
            }
            true
        }
    }

    fun validateFields(): Boolean {
        return if (addTaskTitle?.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter a valid title", Toast.LENGTH_SHORT).show()
            false
        } else if (addTaskDescription?.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter a valid description", Toast.LENGTH_SHORT).show()
            false
        } else if (taskDate?.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter date", Toast.LENGTH_SHORT).show()
            false
        } else if (taskTime?.text.toString().equals("", ignoreCase = true)) {
            Toast.makeText(activity, "Please enter time", Toast.LENGTH_SHORT).show()
            false
            //        } else if (taskEvent.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(activity, "Please enter an event", Toast.LENGTH_SHORT).show();
//            return false;
        } else {
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun createTask() {
        class saveTaskInBackend : AsyncTask<Void?, Void?, Void?>() {
            @SuppressLint("WrongThread")
            override fun doInBackground(vararg p0: Void?): Void? {
                val createTask = Task()
                createTask.taskTitle = addTaskTitle?.text.toString()
                createTask.taskDescrption = addTaskDescription?.text.toString()
                createTask.date = taskDate?.text.toString()
                createTask.lastAlarm = taskTime?.text.toString()
                //                createTask.setEvent(taskEvent.getText().toString());
                if (!isEdit) TaskDatabase.getInstance(getActivity()).taskDao()
                    .insertDataIntoTaskList(createTask) else TaskDatabase.getInstance(getActivity())
                    .taskDao()
                    .updateAnExistingRow(
                        taskId, addTaskTitle?.text.toString(),
                        addTaskDescription?.text.toString(),
                        taskDate?.text.toString(),
                        taskTime?.text.toString()
                    )
                //                                    taskEvent.getText().toString());
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                //                createAnAlarm();
                SetRefreshListener?.refresh()
                Toast.makeText(getActivity(), "Your event is been added", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        val st = saveTaskInBackend()
        st.execute()
    }

    private fun showTaskFromId() {
        class showTaskFromId : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                task = TaskDatabase.getInstance(getActivity()).taskDao()
                    .selectDataFromAnId(taskId)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                setDataInUI()
            }
        }

        val st = showTaskFromId()
        st.execute()
    }

    private fun setDataInUI() {
        addTaskTitle?.setText(task?.taskTitle)
        addTaskDescription?.setText(task?.taskDescrption)
        taskDate?.setText(task?.date)
        taskTime?.setText(task?.lastAlarm)

//        taskEvent.setText(task.getEvent());
    }

    companion object {
        var count = 0
    }
}
