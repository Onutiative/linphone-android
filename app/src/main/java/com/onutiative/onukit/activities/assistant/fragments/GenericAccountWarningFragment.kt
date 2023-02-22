/*
 * Copyright (c) 2010-2022 Belledonne Communications SARL.
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
package com.onutiative.onukit.activities.assistant.fragments

import android.os.Bundle
import android.view.View
import com.onutiative.onukit.R
import com.onutiative.onukit.activities.GenericFragment
import com.onutiative.onukit.activities.navigateToGenericLogin
import com.onutiative.onukit.databinding.AssistantGenericAccountWarningFragmentBinding
import org.linphone.core.tools.Log

class GenericAccountWarningFragment : GenericFragment<AssistantGenericAccountWarningFragmentBinding>() {
    override fun getLayoutId(): Int = R.layout.assistant_generic_account_warning_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // [Dirty HACK] Go to login page directly. Comment this page if you want to keep showing the warning page.
        navigateToGenericLogin()

        binding.setUnderstoodClickListener {
            Log.i("navigateToGenericLogin [Generic Account Warning] User understood the warning, going to login page...")
            navigateToGenericLogin()
        }
    }
}
