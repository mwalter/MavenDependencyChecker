package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageCreatorTest {

    @Test
    void createResultMessage_twoDependencies_shouldReturnMessage() {
        DependencyUpdateResult dependency1 = new DependencyUpdateResult("org.apache.commons", "commons-lang3", "3.8", "3.12.0", true);
        DependencyUpdateResult dependency2 = new DependencyUpdateResult("org.apache.commons", "commons-collections4", "4.2", "4.4", true);

        String resultMessage = MessageCreator.createResultMessage(List.of(dependency1, dependency2));

        assertFalse(resultMessage.isEmpty());
        assertTrue(StringUtils.contains(resultMessage, "org.apache.commons:commons-lang3:3.8 > 3.12.0"));
    }
}