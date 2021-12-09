package com.mmlok.plugin;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PluginEnter implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().findByType(BaseExtension.class)
                .registerTransform(new Test2Transform());
    }
}
