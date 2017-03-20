package com.eden.orchid.impl.compilers.sass;

import com.eden.common.util.EdenPair;
import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.resources.OrchidResources;
import com.eden.orchid.api.resources.resource.OrchidResource;
import com.eden.orchid.utilities.OrchidUtils;
import com.google.inject.Provider;
import io.bit3.jsass.importer.Import;
import io.bit3.jsass.importer.Importer;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

public class SassImporter implements Importer {

    private OrchidContext context;
    private Provider<OrchidResources> resources;

    @Inject
    public SassImporter(OrchidContext context, Provider<OrchidResources> resources) {
        this.context = context;
        this.resources = resources;
    }

    @Override
    public Collection<Import> apply(String url, Import previous) {

        EdenPair<String, String> thisItem = splitPath(url);

        String[] availableFiles = new String[] {
                thisItem.first + "/" + thisItem.second + ".scss",
                thisItem.first + "/" + thisItem.second + ".sass",
                thisItem.first + "/" + "_" + thisItem.second + ".scss",
                thisItem.first + "/" + "_" + thisItem.second + ".sass"
        };

        for(String availableFile : availableFiles) {
            String absoluteUri = splitPath(previous.getAbsoluteUri().getPath()).first + "/" + availableFile;

            OrchidResource importedResource = resources.get().getResourceEntry("assets/css/" + OrchidUtils.stripSeparators(absoluteUri));

            if (importedResource != null) {
                String content = importedResource.getContent();

                if(importedResource.shouldPrecompile()) {
                    content = context.getTheme().precompile(content, importedResource.getEmbeddedData());
                }

                try {
                    return Collections.singletonList(new Import(OrchidUtils.stripSeparators(absoluteUri), OrchidUtils.stripSeparators(absoluteUri), content));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private EdenPair<String, String> splitPath(String name) {
        if(name.contains("/")) {
            String[] pieces = name.split("/");
            String path = "";

            for (int i = 0; i < pieces.length - 1; i++) {
                path += pieces[i].replaceAll("_", "") + "/";
            }
            String fileName = pieces[pieces.length - 1].replaceAll("_", "");

            return new EdenPair<>(OrchidUtils.stripSeparators(path), OrchidUtils.stripSeparators(fileName));
        }
        else {
            return new EdenPair<>("", name);
        }
    }
}