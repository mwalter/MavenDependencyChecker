package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MessageCreator {

    public static String createResultMessage(List<DependencyUpdateResult> dependenciesToUpdate) {
        List<DependencyUpdateResult> sortedDependenciesToUpdate = dependenciesToUpdate.stream()
                .sorted(Comparator.comparing(DependencyUpdateResult::getGroupId)
                        .thenComparing(DependencyUpdateResult::getArtifactId))
                .collect(Collectors.toList());

        StringBuilder message = new StringBuilder();
        for (Iterator<DependencyUpdateResult> iter = sortedDependenciesToUpdate.iterator(); iter.hasNext(); ) {
            DependencyUpdateResult dependency = iter.next();
            message.append( "\n");
            message.append(dependency.getGroupId());
            message.append(":");
            message.append(dependency.getArtifactId());
            message.append(":");
            message.append(dependency.getCurrentVersion());
            message.append(" > ");
            message.append(dependency.getLatestVersion());
        }
        message.append( "\n");
        return message.toString();
    }

}
