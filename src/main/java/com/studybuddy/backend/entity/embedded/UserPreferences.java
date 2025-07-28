package com.studybuddy.backend.entity.embedded;

import java.time.ZoneId;

import com.studybuddy.backend.enums.Theme;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPreferences {
    private boolean notificationsEnabled = false;
    private String timeZone = ZoneId.systemDefault().getId();
    private Theme theme = Theme.SYSTEM;

    // Last used canvas
    private String lastCanvasId;

}
