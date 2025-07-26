package ch.newinstance.plugin.mavendependencychecker.config;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class SettingsComponent {

    private final JPanel mainPanel;

    private final JBCheckBox excludePrereleaseVersions = new JBCheckBox("Exclude prerelease and build versions");
    private final JBCheckBox ignoreMajorVersionChanges = new JBCheckBox("Ignore major version changes");

    public SettingsComponent() {
        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(excludePrereleaseVersions, 1)
                .addComponent(ignoreMajorVersionChanges, 2)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return excludePrereleaseVersions;
    }

    public boolean getExcludePrereleaseVersions() {
        return excludePrereleaseVersions.isSelected();
    }

    public void seteEcludePrereleaseVersions(boolean newStatus) {
        excludePrereleaseVersions.setSelected(newStatus);
    }

    public boolean getIgnoreMajorVersionChanges() {
        return ignoreMajorVersionChanges.isSelected();
    }

    public void setIgnoreMajorVersionChanges(boolean newStatus) {
        ignoreMajorVersionChanges.setSelected(newStatus);
    }

}
