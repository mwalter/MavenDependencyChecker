package ch.newinstance.plugin.mavendependencychecker.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "MavenDependencyCheckerSettings", storages = {@Storage(value = "MavenDependencyCheckerSettings.xml")})
public class MavenDependencyCheckerSettings implements PersistentStateComponent<MavenDependencyCheckerSettings> {

    public String installedVersion = "1.0";

    public String getInstalledVersion() {
        return installedVersion;
    }

    public void setInstalledVersion(String installedVersion) {
        this.installedVersion = installedVersion;
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
