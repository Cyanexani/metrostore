package com.metro.store.view.ui.preferences.network

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.metro.extensions.showKeyboard
import com.metro.extensions.toast
import com.metro.store.R
import com.metro.store.util.CommonUtil
import com.metro.store.util.Preferences
import com.metro.store.util.Preferences.PREFERENCE_PROXY_INFO
import com.metro.store.util.Preferences.PREFERENCE_PROXY_URL
import com.metro.store.util.remove
import com.metro.store.util.save
import com.metro.store.viewmodel.preferences.ProxyURLViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProxyURLDialog: DialogFragment() {

    private val viewModel: ProxyURLViewModel by viewModels()

    private val currentProxyUrl: String
        get() = Preferences.getString(requireContext(), PREFERENCE_PROXY_URL)
    private val textInputLayout: TextInputLayout?
        get() = dialog?.findViewById(R.id.textInputLayout)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_text_input_edit_text, null)
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.pref_network_proxy_url)
            .setMessage(R.string.pref_network_proxy_url_message)
            .setView(view)
            .setPositiveButton(getString(R.string.set), null)
            .setNeutralButton(getString(R.string.disable), null)
            .setNegativeButton(getString(android.R.string.cancel)) { _, _ -> dialog?.dismiss()}
            .create()

        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                isEnabled = currentProxyUrl.isNotBlank()
                setOnClickListener { saveProxyUrl() }
            }
            textInputLayout?.editText?.doOnTextChanged { text, _, _, _ ->
                positiveButton.isEnabled = !text.isNullOrBlank()
            }

            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).apply {
                isEnabled = currentProxyUrl.isNotBlank()
                setOnClickListener { deleteProxyUrl() }
            }
        }
        return alertDialog
    }

    override fun onResume() {
        super.onResume()
        textInputLayout?.editText?.apply {
            setText(currentProxyUrl)
            showKeyboard()
        }
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun saveProxyUrl() {
        val url = textInputLayout?.editText?.text?.toString()?.trim()
        if (url.isNullOrEmpty()) {
            toast(R.string.toast_proxy_invalid)
            return
        }

        val proxyInfo = CommonUtil.parseProxyUrl(url)
        if (proxyInfo != null) {
            save(PREFERENCE_PROXY_URL, url)
            save(PREFERENCE_PROXY_INFO, viewModel.json.encodeToString(proxyInfo))
            toast(R.string.toast_proxy_success)
            findNavController().navigate(R.id.forceRestartDialog)
            return
        } else {
            toast(R.string.toast_proxy_failed)
        }
    }

    private fun deleteProxyUrl() {
        remove(PREFERENCE_PROXY_URL)
        remove(PREFERENCE_PROXY_INFO)
        toast(R.string.toast_proxy_disabled)
        findNavController().navigate(R.id.forceRestartDialog)
    }
}
