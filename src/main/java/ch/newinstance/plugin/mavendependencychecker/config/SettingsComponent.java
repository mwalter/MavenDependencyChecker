package ch.newinstance.plugin.mavendependencychecker.config;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class SettingsComponent {

    private final JPanel mainPanel;
    private final JBCheckBox ignoreMajorVersionChanges = new JBCheckBox("Ignore major version changes");

    public SettingsComponent() {
        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(ignoreMajorVersionChanges, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return ignoreMajorVersionChanges;
    }

    public boolean getIgnoreMajorVersionChanges() {
        return ignoreMajorVersionChanges.isSelected();
    }

    public void setIgnoreMajorVersionChanges(boolean newStatus) {
        ignoreMajorVersionChanges.setSelected(newStatus);
    }
}
