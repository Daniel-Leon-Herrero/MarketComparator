package cat.copernic.daniel.marketcomparator.ui.configuration.configuration

import android.Manifest
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TabHost
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cat.copernic.daniel.marketcomparator.R
import cat.copernic.daniel.marketcomparator.databinding.FragmentAboutBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class aboutFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var binding: FragmentAboutBinding
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var mMap: GoogleMap
    private lateinit var myMarker: Marker
    private lateinit var tabHost: TabHost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = getString(R.string.aboutUs)
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentAboutBinding>(
            inflater,
            R.layout.fragment_about, container, false
        )
        inicializarHostTab()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        startvideo()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.search).setVisible(false)
        menu.findItem(R.id.trolley).setVisible(false)
    }

    fun startvideo() {
        val videoView: VideoView = binding.vVideo
        val mediaController: MediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        val offlineUri: Uri =
            Uri.parse("android.resource://" + requireActivity().packageName + "/${R.raw.video}")
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(offlineUri)
        videoView.requestFocus()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val Institut = LatLng(41.56981636285994, 1.9966395571577435)
        mMap.setOnMarkerClickListener(this)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Institut, 18F))
        myMarker = mMap.addMarker(
            MarkerOptions().position(Institut).title("Institut Nicolau Cop??rnic")
        )
        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker!!.equals(myMarker)) {
            findNavController()
                .navigate(R.id.action_aboutFragment_to_masInfoUbicacionFragment)
        }
        return true
    }

    fun inicializarHostTab() {
        tabHost = binding.tabHost
        tabHost.setup()

        // Tab 1
        var tabSpec: TabHost.TabSpec
        tabSpec = tabHost.newTabSpec("Qui som?")
        tabSpec.setContent(R.id.tab1)
        tabSpec.setIndicator("Qui som?")
        tabHost.addTab(tabSpec)

        //Tab 2
        tabSpec = tabHost.newTabSpec("On som?")
        tabSpec.setContent(R.id.tab2)
        tabSpec.setIndicator("On som?")
        tabHost.addTab(tabSpec)

        // Tab 3
        tabSpec = tabHost.newTabSpec("Drets d'autor")
        tabSpec.setContent(R.id.tab3)
        tabSpec.setIndicator("Drets d'autor")
        tabHost.addTab(tabSpec)
    }

}