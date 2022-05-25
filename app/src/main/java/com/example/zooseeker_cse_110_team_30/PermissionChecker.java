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

    public PermissionChecker(ComponentActivity activity) {
        this.activity = activity;
        requestPermissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), perms -> {
            perms.forEach((perm, isGranted) -> {
                Log.i("LAB7", String.format("Permission %s granted: %s", perm, isGranted));
            });
        });
    }

    boolean ensurePermissions() {
        String[] requiredPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        boolean hasNoLocationPerms = Arrays.stream(requiredPermissions)
                .map(perm -> ContextCompat.checkSelfPermission(activity, perm))
                .allMatch(status -> status == PackageManager.PERMISSION_DENIED);

        if (hasNoLocationPerms) {
            requestPermissionLauncher.launch(requiredPermissions);
            return true;
        }
        return false;
    }
}