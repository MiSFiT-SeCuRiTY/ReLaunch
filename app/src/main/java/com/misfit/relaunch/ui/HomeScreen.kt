package com.misfit.relaunch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misfit.relaunch.R
import com.misfit.relaunch.domain.model.CompatibilityStatus
import com.misfit.relaunch.domain.model.PowerMenuResult
import com.misfit.relaunch.domain.model.RestrictionReason

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            StatusIndicator(
                status = uiState.compatibility,
                onOpenSettings = {
                    context.startActivity(viewModel.accessibilitySettingsIntent())
                }
            )

            Spacer(Modifier.height(24.dp))

            PowerButtonCard(
                enabled = uiState.compatibility?.canAttemptPowerMenu == true,
                isLoading = uiState.isInvoking,
                onClick = viewModel::invokePowerMenu
            )

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = uiState.lastResult != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.lastResult?.let { ResultCard(it, viewModel::clearResult) }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = viewModel::toggleDiagnostics,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Build, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.toggle_diagnostics))
            }

            AnimatedVisibility(visible = uiState.showDiagnostics) {
                DiagnosticsCard(uiState.compatibility)
            }

            Spacer(Modifier.height(32.dp))
            HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            Text(
                text = stringResource(R.string.developer_credit),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatusIndicator(
    status: CompatibilityStatus?,
    onOpenSettings: () -> Unit
) {
    if (status == null) return

    val (icon, label, color) = when {
        status.canAttemptPowerMenu -> Triple(
            Icons.Default.CheckCircle,
            stringResource(R.string.status_ready),
            MaterialTheme.colorScheme.primary
        )
        status.restrictionReason == RestrictionReason.ACCESSIBILITY_DISABLED -> Triple(
            Icons.Default.Warning,
            stringResource(R.string.status_accessibility_needed),
            MaterialTheme.colorScheme.error
        )
        else -> Triple(
            Icons.Default.Info,
            stringResource(R.string.status_unavailable),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.titleLarge, color = color)
                    if (status.restrictionReason == RestrictionReason.ACCESSIBILITY_DISABLED) {
                        Text(
                            stringResource(R.string.status_accessibility_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (status.restrictionReason == RestrictionReason.ACCESSIBILITY_DISABLED) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.open_accessibility_settings))
                }
            }
        }
    }
}

@Composable
private fun PowerButtonCard(enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.PowerSettingsNew,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Open Power Menu",
                style = MaterialTheme.typography.titleLarge,
                color = if (enabled) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onClick,
                enabled = enabled && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enabled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.surface,
                    contentColor = if (enabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (enabled) "Tap to Open" else "Unavailable")
                }
            }
        }
    }
}

@Composable
private fun ResultCard(result: PowerMenuResult, onDismiss: () -> Unit) {
    val (title, body, color) = when (result) {
        is PowerMenuResult.Success -> Triple(
            "Request sent",
            result.message,
            MaterialTheme.colorScheme.primary
        )
        is PowerMenuResult.Unavailable -> Triple(
            "Not available",
            result.reason + "\n\nAlternatives:\n" +
                    result.alternatives.joinToString("\n") { "• $it" },
            MaterialTheme.colorScheme.error
        )
        is PowerMenuResult.Error -> Triple(
            "Error",
            result.message,
            MaterialTheme.colorScheme.error
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = color)
            Spacer(Modifier.height(8.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dismiss")
            }
        }
    }
}

@Composable
private fun DiagnosticsCard(status: CompatibilityStatus?) {
    if (status == null) return

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Diagnostics", style = MaterialTheme.typography.titleLarge)
            Text("API level: ${status.apiLevel}")
            Text("Accessibility enabled: ${status.accessibilityEnabled}")
            Text("Power menu API available: ${status.powerMenuApiAvailable}")
            Text("Power menu API restricted: ${status.powerMenuApiRestricted}")
            Text("Can attempt power menu: ${status.canAttemptPowerMenu}")
            Text("Restriction reason: ${status.restrictionReason?.name ?: "none"}")
        }
    }
}