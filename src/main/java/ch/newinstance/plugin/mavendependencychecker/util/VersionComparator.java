package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VersionComparator {

    private static final String RESPONSE = "response";
    private static final String DOCS_ARRAY = "docs";
    private static final String LATEST_VERSION = "latestVersion";
    private static final String GROUP = "g";
    private static final String ARTIFACT = "a";

    private final Map<String, String> moduleDependencies;

    public VersionComparator(Map<String, String> moduleDependencies) {
        this.moduleDependencies = moduleDependencies;
    }

    public List<DependencyUpdateResult> compareVersions(List<String> jsons) {
        List<DependencyUpdateResult> result = new ArrayList<>();
        for (String json : jsons) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject response = jsonObject.getJSONObject(RESPONSE);
                JSONObject docsObject = response.getJSONArray(DOCS_ARRAY).getJSONObject(0);
                String groupId = docsObject.getString(GROUP);
                String artifactId = docsObject.getString(ARTIFACT);
                String latestVersion = docsObject.getString(LATEST_VERSION);

                String key = groupId + ":" + artifactId;
                String currentVersion = moduleDependencies.get(key);

                if (!currentVersion.equals(latestVersion)) {
                    result.add(new DependencyUpdateResult(groupId, artifactId, currentVersion, latestVersion));
                }
            } catch (JSONException je) {
                // ignoring
            }
        }
        return result;
    }

}
