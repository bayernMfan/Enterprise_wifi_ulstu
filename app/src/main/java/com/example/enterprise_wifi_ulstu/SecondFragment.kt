package com.example.enterprise_wifi_ulstu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.enterprise_wifi_ulstu.databinding.FragmentSecondBinding
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_ContactsFragment)
        }

//         fun Context.toast(message: CharSequence) =
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        binding.connectButton.setOnClickListener {
            val login = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (login.isNotEmpty() && password.isNotEmpty()){
                connectToWiFi(login, password)
            }
            else {
                toast{"Введите логин и пароль"}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun connectToWiFi(login:String, password: String) {

        //Read CaCert from raw resources
        val certIS = resources.openRawResource(R.raw.wifi_ustu_ca)
        val certFact = CertificateFactory.getInstance("X.509")
        val cert = certFact.generateCertificate(certIS)
        certIS.close()

        //Configure enterprise settings
        val enterpriseConfig = WifiEnterpriseConfig()
        enterpriseConfig.eapMethod = WifiEnterpriseConfig.Eap.TTLS
        enterpriseConfig.phase2Method = WifiEnterpriseConfig.Phase2.PAP
        enterpriseConfig.domainSuffixMatch = "wifi.ulstu.ru"
        enterpriseConfig.caCertificate = cert  as X509Certificate
        enterpriseConfig.identity = login
        enterpriseConfig.password = password


        val ustuCorp  = WifiNetworkSuggestion.Builder()
            .setSsid("USTU_CORP")
            .setWpa2EnterpriseConfig(enterpriseConfig)
            .build()


//        val dom = WifiNetworkSuggestion.Builder()
//            .setSsid(login)
//            .setWpa2Passphrase(password)
///            .setIsAppInteractionRequired(true)// Optional (Needs location permission)
//            .build()

        val suggestionsList = listOf(ustuCorp)


        val wifiManager = ContextCompat.getSystemService(requireContext(), WifiManager::class.java)
            if (wifiManager!!.isWifiEnabled) {
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.R) {
                    startActivity(
                        Intent(Settings.ACTION_WIFI_ADD_NETWORKS).putParcelableArrayListExtra(
                            Settings.EXTRA_WIFI_NETWORK_LIST,
                            ArrayList<Parcelable?>(suggestionsList)
                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                else {
                    val wifiManager =
                        context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    wifiManager.removeNetworkSuggestions(suggestionsList)
                    val status = wifiManager.addNetworkSuggestions(suggestionsList)
                    if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                        toast{"WIFI добавлен в список сохраненных сетей"}
                    }else{
                        toast { "WIFI сеть не сохранена" }
                    }
//                    val config = WifiConfiguration()
//                    config.SSID="USTU_CORP"
//                    config.enterpriseConfig=enterpriseConfig
//                    val networkId = wifiManager.addNetwork(config)
//
//                    if(networkId==-1){
//                        toast { "Failure" }
//                    }else {
//                        toast { "Success $networkId" }
//                    }


                }
            }
        else {
            toast { "WIFI не включен" }
        }


        //The only way to connect directly to specified wifi dated to 23.05.2023


//        startActivity( Intent("android.settings.panel.action.INTERNET_CONNECTIVITY"))
//        val connectivityManager =
//            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as
//                    ConnectivityManager
//        val specifier = WifiNetworkSpecifier.Builder()
//            .setSsid("TP-Link_AF24_5G")
//            .setWpa2Passphrase("25596205")
//            .setSsidPattern(PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
////            .setIsHiddenSsid(true)
//            .build()
//        val request = NetworkRequest.Builder()
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
//            .setNetworkSpecifier(specifier)
//            .build()
//        val networkCallback = object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                super.onAvailable(network)
//                print("connection_success")
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    connectivityManager.bindProcessToNetwork(network);
//                } else {
//                    ConnectivityManager.setProcessDefaultNetwork(network);
//                }
////                connectivityManager.bindProcessToNetwork(network)
//            }
//
//            override fun onUnavailable() {
//                super.onUnavailable()
//                print("connection_success")
//
//            }
//
//            override fun onLost(network: Network) {
//                super.onLost(network)
//                connectivityManager.bindProcessToNetwork(null)
//                connectivityManager.unregisterNetworkCallback(this)
//                print("connection_lost")
//
//            }
//        }
//        connectivityManager.requestNetwork(request, networkCallback)
    }

}