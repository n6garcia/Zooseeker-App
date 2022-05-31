package com.example.zooseeker_cse_110_team_30;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

/**
 * Permission checker, encapsulates all permission requests
 * @author CSE 110 Instructors
 */
public class PermissionChecker {
    private ComponentActivity activity;
    final ActivityResultLauncher<String[]> requestPermissionLauncher;
    private boolean hasNoLocationPerms;
    private String[] requiredPermissions;

    public PermissionChecker(ComponentActivity activity) {
        this.activity = activity;
        this.requiredPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        requestPermissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), perms -> {
            perms.forEach((perm, isGranted) -> {
                Log.i("LAB7", String.format("Permission %s granted: %s", perm, isGranted));
            });
        });
    }

    /**
     * Prompts the user for the required permissions if we do not have them.
     * @return true if the app has no permissions, false otherwise.
     */
    public boolean ensurePermissions() {
        if (!hasPermissions()) {
            requestPermissionLauncher.launch(requiredPermissions);
            return true;
        }
        return false;
    }

    /**
     * Returns whether or not all permissions for this app have been granted by the user.
     * @return true if all permissions granted, false otherwise.
     */
    public boolean hasPermissions() {
        return !Arrays.stream(requiredPermissions)
                .map(perm -> ContextCompat.checkSelfPermission(activity, perm))
                .allMatch(status -> status == PackageManager.PERMISSION_DENIED);
    }
}