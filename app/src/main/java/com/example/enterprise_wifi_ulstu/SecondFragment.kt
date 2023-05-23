package com.example.enterprise_wifi_ulstu

import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.Fragment
import com.example.enterprise_wifi_ulstu.databinding.FragmentSecondBinding
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            connectToWiFi("25596205","TP-Link_AF24_5G")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWiFi(pin: String, ssid:String) {

        val certIS = resources.openRawResource(R.raw.wifi_ustu_ca)
        val certFact = CertificateFactory.getInstance("X.509")
        val cert = certFact.generateCertificate(certIS)
        certIS.close()

        val enterpriseConfig = WifiEnterpriseConfig()
        enterpriseConfig.eapMethod = WifiEnterpriseConfig.Eap.TTLS
        enterpriseConfig.phase2Method = WifiEnterpriseConfig.Phase2.PAP
        enterpriseConfig.domainSuffixMatch = "wifi.ulstu.ru"
        enterpriseConfig.caCertificate = cert  as X509Certificate
        enterpriseConfig.identity = "i.grushin"
        enterpriseConfig.password = "e0e5eb7c"

        val ustu_corp = WifiNetworkSuggestion.Builder()
            .setSsid("USTU_CORP")
            .setWpa2EnterpriseConfig(enterpriseConfig)
            .build()

        val dom = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(pin)
            .setIsAppInteractionRequired(true)// Optional (Needs location permission)
            .build();

        val suggestionsList = listOf(ustu_corp, dom);
//        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager;
//        val status = wifiManager.addNetworkSuggestions(suggestionsList);
//        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//            print("PIZDA")
//        }

        startActivity(
            Intent(Settings.ACTION_WIFI_ADD_NETWORKS).putParcelableArrayListExtra(
                Settings.EXTRA_WIFI_NETWORK_LIST,
                ArrayList<Parcelable?>(suggestionsList)
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )

//
//        val connectivityManager =
//            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as
//                    ConnectivityManager
//        val specifier = WifiNetworkSpecifier.Builder()
//            .setSsid(ssid)
//            .setWpa2Passphrase(pin)
//            .setSsidPattern(PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
//            .build()
//        val request = NetworkRequest.Builder()
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .setNetworkSpecifier(specifier)
//            .build()
//        val networkCallback = object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                super.onAvailable(network)
////                showToast(context,context.getString(R.string.connection_success))
//                print("connection_success")
//                connectivityManager.bindProcessToNetwork(network)
//            }
//
//            override fun onUnavailable() {
//                super.onUnavailable()
////                showToast(context,context.getString(R.string.connection_fail))
//                print("connection_success")
//            }
//
//            override fun onLost(network: Network) {
//                super.onLost(network)
////                showToast(context,context.getString(R.string.out_of_range))
//                print("connection_success")
//
//            }
//        }
//        connectivityManager.requestNetwork(request, networkCallback)
    }

}