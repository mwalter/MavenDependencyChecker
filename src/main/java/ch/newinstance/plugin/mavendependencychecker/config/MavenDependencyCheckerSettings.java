package ch.newinstance.plugin.mavendependencychecker.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "MavenDependencyCheckerSettings", storages = {@Storage(value = "MavenDependencyCheckerSettings.xml")})
public class MavenDependencyCheckerSettings implements PersistentStateComponent<MavenDependencyCheckerSettings> {

    private String installedVersion = "1.0";

    private boolean majorVersionChangeIgnored = false;
    private boolean prereleaseVersionsExcluded = false;

    static MavenDependencyCheckerSettings getInstance() {
        return ApplicationManager.getApplication().getService(MavenDependencyCheckerSettings.class);
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public void setInstalledVersion(String installedVersion) {
        this.installedVersion = installedVersion;
    }

    public boolean isMajorVersionChangeIgnored() {
        return majorVersionChangeIgnored;
    }

    public void setMajorVersionChangeIgnored(boolean majorVersionChangeIgnored) {
        this.majorVersionChangeIgnored = majorVersionChangeIgnored;
    }

    public boolean isPrereleaseVersionsExcluded() {
        return prereleaseVersionsExcluded;
    }

    public void setPrereleaseVersionsExcluded(boolean prereleaseVersionsExcluded) {
        this.prereleaseVersionsExcluded = prereleaseVersionsExcluded;
    }

    @Override
    public @Nullable MavenDependencyCheckerSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MavenDependencyCheckerSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
