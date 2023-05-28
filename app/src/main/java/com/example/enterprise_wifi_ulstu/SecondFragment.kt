package com.example.enterprise_wifi_ulstu

import android.content.Intent
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiNetworkSuggestion
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.enterprise_wifi_ulstu.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate



class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.connectButton.setOnClickListener {
            val login = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
//            connectToWiFi(login, password)


            if (login.isNullOrEmpty() && password.isNullOrEmpty()) {
                Snackbar.make(view, "Введите имя пользователя и пароль", Snackbar.LENGTH_LONG)
                    .show()
//                Toast.makeText(this@SecondFragment, "hui", Toast.LENGTH_LONG).show()
            } else {
                connectToWiFi(login, password)
            }

//        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//            connectToWiFi("25596205","TP-Link_AF24_5G")
//        }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
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

        val ustuCorp = WifiNetworkSuggestion.Builder()
            .setSsid("USTU_CORP")
            .setWpa2EnterpriseConfig(enterpriseConfig)
            .build()

        val dom = WifiNetworkSuggestion.Builder()
            .setSsid(login)
            .setWpa2Passphrase(password)
//            .setIsAppInteractionRequired(true)// Optional (Needs location permission)
            .build()

        val suggestionsList = listOf(ustuCorp, dom)
//        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager;
//        val status = wifiManager.addNetworkSuggestions(suggestionsList);
//        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//            print("PIZDA")
//        }

        //The only way to connect directly to specified wifi dated to 23.05.2023
        startActivity(
            Intent(Settings.ACTION_WIFI_ADD_NETWORKS).putParcelableArrayListExtra(
                Settings.EXTRA_WIFI_NETWORK_LIST,
                ArrayList<Parcelable?>(suggestionsList)
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )

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