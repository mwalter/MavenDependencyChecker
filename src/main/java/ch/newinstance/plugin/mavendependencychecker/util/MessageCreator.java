package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MessageCreator {

    private MessageCreator() {
        // utility class
    }

    public static String createResultMessage(List<DependencyUpdateResult> dependenciesToUpdate) {
        List<DependencyUpdateResult> sortedDependenciesToUpdate = dependenciesToUpdate.stream()
                .sorted(Comparator.comparing(DependencyUpdateResult::getGroupId)
                        .thenComparing(DependencyUpdateResult::getArtifactId))
                .toList();

        StringBuilder message = new StringBuilder();
        for (Iterator<DependencyUpdateResult> iter = sortedDependenciesToUpdate.iterator(); iter.hasNext(); ) {
            DependencyUpdateResult dependency = iter.next();
            message.append(dependency.getGroupId());
            message.append(":");
            message.append(dependency.getArtifactId());
            message.append(":");
            message.append(dependency.getCurrentVersion());
            message.append(" > ");
            message.append(dependency.getLatestVersion());
            if (iter.hasNext()) {
                message.append("\n");
            }
        }
        message.append( "\n");
        return message.toString();
    }

}
