package com.aizoid.unityadsdemo

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unity3d.ads.UnityAds
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAdsShowOptions

data class StoreProject(
    val name: String,
    val description: String,
    val category: String,
    val fileUrl: String
)

class MainActivity : ComponentActivity(),
    IUnityAdsInitializationListener,
    IUnityAdsLoadListener,
    IUnityAdsShowListener {

    private val gameId = "800430632"
    private val rewardedId = "BP_Rewarded_Android"
    private val testMode = true

    private var adsReady by mutableStateOf(false)
    private var selectedProject: StoreProject? = null
    private var adStatus by mutableStateOf("Ads loading...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UnityAds.initialize(this, gameId, testMode, this)

        setContent {
            MaterialTheme {
                StoreScreen(
                    adsReady = adsReady,
                    adStatus = adStatus,
                    onDownload = { project ->
                        selectedProject = project
                        if (adsReady) {
                            UnityAds.show(
                                this,
                                rewardedId,
                                UnityAdsShowOptions(),
                                this
                            )
                        } else {
                            Toast.makeText(
                                this,
                                "Ad abhi ready nahi hai. Thodi der baad try karo.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }

    override fun onInitializationComplete() {
        adStatus = "Ads ready"
        UnityAds.load(rewardedId, this)
    }

    override fun onInitializationFailed(
        error: UnityAds.UnityAdsInitializationError,
        message: String
    ) {
        adStatus = "Ads initialization failed"
    }

    override fun onUnityAdsAdLoaded(placementId: String) {
        if (placementId == rewardedId) {
            adsReady = true
            adStatus = "Ads ready"
        }
    }

    override fun onUnityAdsFailedToLoad(
        placementId: String,
        error: UnityAds.UnityAdsLoadError,
        message: String
    ) {
        adsReady = false
        adStatus = "Ad load failed"
    }

    override fun onUnityAdsShowStart(placementId: String) {}

    override fun onUnityAdsShowClick(placementId: String) {}

    override fun onUnityAdsShowFailure(
        placementId: String,
        error: UnityAds.UnityAdsShowError,
        message: String
    ) {
        Toast.makeText(
            this,
            "Ad failed. Download nahi hua.",
            Toast.LENGTH_SHORT
        ).show()

        adsReady = false
        UnityAds.load(rewardedId, this)
    }

    override fun onUnityAdsShowComplete(
        placementId: String,
        state: UnityAds.UnityAdsShowCompletionState
    ) {
        if (
            placementId == rewardedId &&
            state == UnityAds.UnityAdsShowCompletionState.COMPLETED
        ) {
            selectedProject?.let { startDownload(it) }
        } else {
            Toast.makeText(
                this,
                "Ad complete nahi hua.",
                Toast.LENGTH_SHORT
            ).show()
        }

        adsReady = false
        UnityAds.load(rewardedId, this)
    }

    private fun startDownload(project: StoreProject) {
        try {
            if (project.fileUrl.isBlank()) {
                Toast.makeText(
                    this,
                    "Download link available nahi hai.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            val request = DownloadManager.Request(
                Uri.parse(project.fileUrl)
            )
                .setTitle(project.name)
                .setDescription("Downloading from RAKESH APP STORE")
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val manager =
                getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            manager.enqueue(request)

            Toast.makeText(
                this,
                "Download started!",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Download failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    adsReady: Boolean,
    adStatus: String,
    onDownload: (StoreProject) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    val projects = remember {
        mutableStateListOf<StoreProject>()
    }

    val filteredProjects = projects.filter {
        it.name.contains(searchText, ignoreCase = true) ||
        it.category.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("RAKESH APP STORE")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                "Welcome!",
                style = MaterialTheme.typography.headlineMedium
            )

            Text("Your favourite apps and projects in one place.")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search projects") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text("Rewarded Ads")
                    Text(adStatus)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Latest Projects",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredProjects.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No projects available yet.\nFirebase connection is the next step."
                    )
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProjects) { project ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    project.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(project.category)

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(project.description)

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        onDownload(project)
                                    },
                                    enabled = adsReady
                                ) {
                                    Text("Watch Ad & Download")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
