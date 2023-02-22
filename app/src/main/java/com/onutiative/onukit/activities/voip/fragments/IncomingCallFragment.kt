/*
 * Copyright (c) 2010-2021 Belledonne Communications SARL.
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
package com.onutiative.onukit.activities.voip.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import androidx.navigation.navGraphViewModels
import com.onutiative.onukit.LinphoneApplication.Companion.coreContext
import com.onutiative.onukit.R
import com.onutiative.onukit.activities.GenericFragment
import com.onutiative.onukit.activities.navigateToActiveCall
import com.onutiative.onukit.activities.voip.viewmodels.CallsViewModel
import com.onutiative.onukit.activities.voip.viewmodels.ControlsViewModel
import com.onutiative.onukit.databinding.VoipCallIncomingFragmentBinding
import org.linphone.core.tools.Log

class IncomingCallFragment : GenericFragment<VoipCallIncomingFragmentBinding>() {
    private val controlsViewModel: ControlsViewModel by navGraphViewModels(R.id.call_nav_graph)
    private val callsViewModel: CallsViewModel by navGraphViewModels(R.id.call_nav_graph)

    override fun getLayoutId(): Int = R.layout.voip_call_incoming_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.controlsViewModel = controlsViewModel

        binding.callsViewModel = callsViewModel

        callsViewModel.callConnectedEvent.observe(
            viewLifecycleOwner
        ) {
            it.consume {
                navigateToActiveCall()
            }
        }

        callsViewModel.callEndedEvent.observe(
            viewLifecycleOwner
        ) {
            it.consume {
                navigateToActiveCall()
            }
        }

        callsViewModel.currentCallData.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                val timer = binding.root.findViewById<Chronometer>(R.id.incoming_call_timer)
                timer.base =
                    SystemClock.elapsedRealtime() - (1000 * it.call.duration) // Linphone timestamps are in seconds
                timer.start()
            }
        }

        val earlyMediaVideo = arguments?.getBoolean("earlyMediaVideo") ?: false
        if (earlyMediaVideo) {
            Log.i("[Incoming Call] Video early media detected, setting native window id")
            coreContext.core.nativeVideoWindowId = binding.remoteVideoSurface
        }
    }

    // We don't want the proximity sensor to turn screen OFF in this fragment
    override fun onResume() {
        super.onResume()
        controlsViewModel.forceDisableProximitySensor.value = true
    }

    override fun onPause() {
        controlsViewModel.forceDisableProximitySensor.value = false
        super.onPause()
    }
}
