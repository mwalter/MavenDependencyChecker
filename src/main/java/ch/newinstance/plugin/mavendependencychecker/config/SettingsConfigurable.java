package ch.newinstance.plugin.mavendependencychecker.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class SettingsConfigurable implements Configurable {

    private SettingsComponent settingsComponent;

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "Maven Dependency Checker";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingsComponent = new SettingsComponent();
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        MavenDependencyCheckerSettings settings = MavenDependencyCheckerSettings.getInstance();
        return settingsComponent.getIgnoreMajorVersionChanges() != settings.isMajorVersionChangeIgnored()
                || settingsComponent.getExcludePrereleaseVersions() != settings.isPrereleaseVersionsExcluded();
    }

    @Override
    public void apply() {
        MavenDependencyCheckerSettings settings = MavenDependencyCheckerSettings.getInstance();
        settings.setMajorVersionChangeIgnored(settingsComponent.getIgnoreMajorVersionChanges());
        settings.setPrereleaseVersionsExcluded(settingsComponent.getExcludePrereleaseVersions());
    }

    @Override
    public void reset() {
        MavenDependencyCheckerSettings settings = MavenDependencyCheckerSettings.getInstance();
        settingsComponent.setIgnoreMajorVersionChanges(settings.isMajorVersionChangeIgnored());
        settingsComponent.seteEcludePrereleaseVersions(settings.isPrereleaseVersionsExcluded());
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
}
