/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.activities.main.history.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.linphone.R
import org.linphone.activities.*
import org.linphone.activities.main.*
import org.linphone.activities.main.history.viewmodels.CallLogViewModel
import org.linphone.activities.main.history.viewmodels.CallLogViewModelFactory
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log
import org.linphone.databinding.HistoryConfDetailFragmentBinding
import org.linphone.utils.Event

class DetailConferenceCallLogFragment : GenericFragment<HistoryConfDetailFragmentBinding>() {
    private lateinit var viewModel: CallLogViewModel
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int = R.layout.history_conf_detail_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this)[SharedMainViewModel::class.java]
        }
        binding.sharedMainViewModel = sharedViewModel

        val callLogGroup = sharedViewModel.selectedCallLogGroup.value
        if (callLogGroup == null) {
            Log.e("[History] Call log group is null, aborting!")
            findNavController().navigateUp()
            return
        }

        viewModel = ViewModelProvider(
            this,
            CallLogViewModelFactory(callLogGroup.lastCallLog)
        )[CallLogViewModel::class.java]
        binding.viewModel = viewModel

        useMaterialSharedAxisXForwardAnimation = sharedViewModel.isSlidingPaneSlideable.value == false

        viewModel.relatedCallLogs.value = callLogGroup.callLogs

        binding.setBackClickListener {
            goBack()
        }

        viewModel.onMessageToNotifyEvent.observe(
            viewLifecycleOwner
        ) {
            it.consume { messageResourceId ->
                (activity as MainActivity).showSnackBar(messageResourceId)
            }
        }
    }

    override fun goBack() {
        if (sharedViewModel.isSlidingPaneSlideable.value == true) {
            sharedViewModel.closeSlidingPaneEvent.value = Event(true)
        } else {
            navigateToEmptyCallHistory()
        }
    }
}