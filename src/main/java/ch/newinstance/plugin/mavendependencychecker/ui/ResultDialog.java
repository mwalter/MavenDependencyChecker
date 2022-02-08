package ch.newinstance.plugin.mavendependencychecker.ui;

import com.intellij.openapi.ui.messages.MessageDialog;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.awt.Dimension;


public class ResultDialog extends MessageDialog {

    public ResultDialog(@NlsContexts.DialogMessage @Nullable String message, @NlsContexts.DialogTitle String title, String @NotNull [] options, int defaultOptionIndex, @Nullable Icon icon) {
        super(message, title, options, defaultOptionIndex, icon);
    }

    @Override
    protected JComponent doCreateCenterPanel() {
        JComponent panel = super.doCreateCenterPanel();
        panel.setPreferredSize(new Dimension(500, 200));
        return panel;
    }

}
