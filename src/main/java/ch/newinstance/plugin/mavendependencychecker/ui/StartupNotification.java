package ch.newinstance.plugin.mavendependencychecker.ui;

import ch.newinstance.plugin.mavendependencychecker.config.MavenDependencyCheckerSettings;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.BrowseNotificationAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.text.VersionComparatorUtil;
import org.jetbrains.annotations.NotNull;

public class StartupNotification implements StartupActivity, DumbAware {

    private static final NotificationGroup GROUP = NotificationGroupManager.getInstance().getNotificationGroup("MavenDependencyChecker");

    @Override
    public void runActivity(@NotNull Project project) {
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("ch.newinstance.plugin.mavendependencychecker"));
        MavenDependencyCheckerSettings settings = ApplicationManager.getApplication().getService(MavenDependencyCheckerSettings.class);
        String installedVersion = settings.getInstalledVersion();

        int compareResult = 0;
        if (plugin != null) {
            compareResult = VersionComparatorUtil.compare(installedVersion, plugin.getVersion());
        }

        if (compareResult < 0) {
            Notification notification = createNotification();
            notification.notify(null);
            settings.setInstalledVersion(plugin.getVersion());
        }
    }

    private Notification createNotification() {
        return GROUP.createNotification(
                        "Maven Dependency Checker",
                        "Thank you for using my plugin! If you like it, please add a rating or write a review.",
                        NotificationType.INFORMATION)
                .setImportant(false)
                .setIcon(IconLoader.getIcon("/icons/pluginIcon.png", StartupNotification.class))
                .addAction(new BrowseNotificationAction(
                        "Plugin Homepage",
                        "https://plugins.jetbrains.com/plugin/18525-maven-dependency-checker")
                );
    }

}
