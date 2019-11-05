package com.example.simplecallerid.ui.main

import android.graphics.Rect
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecallerid.R
import com.example.simplecallerid.models.PhoneType
import com.example.simplecallerid.models.User
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.user_dialog.view.*
import kotlinx.android.synthetic.main.user_dialog.view.phone_input
import kotlinx.android.synthetic.main.user_layout.view.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initRecyclerView()

        viewModel.users.observe(this, Observer { users ->
            users.isEmpty().let {
                recycler_view.isVisible = !it
                message.isVisible = it
            }
            adapter.submitList(users)
            adapter.notifyDataSetChanged()
            message.text = getString(R.string.no_users)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> openUserDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openUserDialog(user: User? = null) {
        val dialog = View.inflate(this.requireContext(), R.layout.user_dialog, null)

        if (user == null) initDialogPhoneFields(dialog) else initEditUserDialog(dialog, user)

        AlertDialog.Builder(this.requireContext())
            .setTitle(getString(R.string.add_user))
            .setView(dialog)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                if (dialog.first_name_input.text.isNullOrEmpty()
                    || dialog.last_name_input.text.isNullOrEmpty()
                    || dialog.phone_input.text.isNullOrEmpty()
                    || dialog.phone_type_spinner.selectedItem == null) return@setPositiveButton

                val tempUser = User(
                        dialog.first_name_input.text.toString(),
                        dialog.last_name_input.text.toString(),
                        dialog.phone_input.text.toString(),
                        PhoneType.parse(dialog.phone_type_spinner.selectedItem.toString())
                    )
                if (user == null) viewModel.insert(tempUser) else viewModel.update(tempUser.apply { id = user.id })
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun initRecyclerView() {
        adapter = UserAdapter()
        recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val dimen = resources.getDimensionPixelSize(R.dimen.card_margin)
                with(outRect) {
                    if (parent.getChildAdapterPosition(view) == 0) top = dimen
                    left =  dimen
                    right = dimen
                    bottom = dimen
                }
            }
        })
        recycler_view.adapter = adapter
        message.text = getString(R.string.loading)
    }

    private fun initDialogPhoneFields(dialog: View) {
        dialog.phone_input.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        dialog.phone_type_spinner?.let {
            it.adapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.phone_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
    }

    private fun initEditUserDialog(dialog: View, user: User) {
        dialog.first_name_input.setText(user.firstName)
        dialog.last_name_input.setText(user.lastName)
        initDialogPhoneFields(dialog)
        dialog.phone_input.setText(user.phoneNumber)
        val phoneTypeArray = resources.getStringArray(R.array.phone_types)
        val index = phoneTypeArray.indexOfFirst { it == user.phoneType.label }
        dialog.phone_type_spinner.setSelection(index)
    }

    inner class UserAdapter: ListAdapter<User, UserAdapter.UserViewHolder>(USER_DIFF) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val layoutInflater = LayoutInflater.from(context)
            return UserViewHolder(layoutInflater.inflate(R.layout.user_layout, parent, false))
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = getItem(position)
            holder.bind(user)
        }

        inner class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
            fun bind(user: User) {
                itemView.user_full_name.text = user.fullName
                itemView.user_data.text = user.prettyPrint
                itemView.delete_user_button.setOnClickListener { viewModel.delete(user) }
                itemView.layout.setOnClickListener { openUserDialog(user) }
            }
        }
    }

    companion object {
        fun newInstance() = MainFragment()

        private val USER_DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.fullName == newItem.fullName
            }

            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

}
