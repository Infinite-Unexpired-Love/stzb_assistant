package com.local.stzb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.DashboardActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.CaptureVpnService
import com.local.stzb.core.designsystem.AstzbTheme
import com.local.stzb.core.navigation.StzbApp
import com.local.stzb.feature.overlay.BattlefieldOverlayService
import hev.sockstun.TProxyService

class StzbAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstzbTheme {
                val app = application as StzbApplication
                StzbApp(
                    repository = app.battlefieldRepository,
                    teamsRepository = app.teamsRepository,
                    battleRepository = app.battleRepository,
                    allianceRepository = app.allianceRepository,
                    intelRepository = app.intelRepository,
                    rankingRepository = app.rankingRepository,
                    captureController = app.captureConsoleController,
                    openLegacyDashboard = ::openLegacyDashboard,
                    openCaptureConsole = ::openCaptureConsole,
                    profileSnapshot = app.profileSnapshot(),
                    onRegisterProfile = app::registerLocalProfile,
                    onSwitchProfile = { profileId ->
                        app.switchLocalProfile(profileId).onSuccess { recreate() }
                    },
                    onLogout = ::exitAssistant,
                )
            }
        }
    }

    private fun openLegacyDashboard(module: String) {
        startActivity(
            Intent(this, DashboardActivity::class.java).apply {
                putExtra(DashboardActivity.EXTRA_MODULE, module)
            },
        )
    }

    private fun openCaptureConsole() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun exitAssistant() {
        stopService(Intent(this, CaptureVpnService::class.java))
        BattlefieldOverlayService.stop(this)
        startService(
            Intent(this, TProxyService::class.java).apply {
                action = TProxyService.ACTION_DISCONNECT
            },
        )
        finishAndRemoveTask()
    }
}
