package com.eden.orchid.javadoc.impl.docParsers;

import com.eden.common.json.JSONElement;
import com.eden.common.util.EdenUtils;
import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.javadoc.api.JavadocBlockTagHandler;
import com.eden.orchid.javadoc.api.JavadocInlineTagHandler;
import com.eden.orchid.utilities.ObservableTreeSet;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Set;

public class CommentParser {

    private OrchidContext context;
    private Set<JavadocBlockTagHandler> blockTagHandlers;
    private Set<JavadocInlineTagHandler> inlineTagHandlers;

    @Inject
    public CommentParser(OrchidContext context, Set<JavadocBlockTagHandler> blockTagHandlers, Set<JavadocInlineTagHandler> inlineTagHandlers) {
        this.context = context;
        this.blockTagHandlers = new ObservableTreeSet<>(blockTagHandlers);
        this.inlineTagHandlers = new ObservableTreeSet<>(inlineTagHandlers);
    }

    public JSONObject getCommentObject(Doc doc) {
        JSONObject comment = new JSONObject();

        String firstSentence = "";

        for (Tag tag : doc.firstSentenceTags()) {
            firstSentence += tag.text();
        }

        if (!EdenUtils.isEmpty(firstSentence)) {
            comment.put("shortDescription", firstSentence);
        }
        if (!EdenUtils.isEmpty(doc.commentText())) {
            String content = doc.commentText();

            if (context.query("options.commentExt") != null) {
                content = context.compile(context.query("options.commentExt").toString(), content);
            }

            comment.put("description", content);
        }

        comment.put("inlineTags", getInlineTags(doc));
        comment.put("blockTags", getBlockTags(doc));

        return (comment.length() > 0) ? comment : null;
    }

    private JSONArray getInlineTags(Doc doc) {
        JSONArray array = new JSONArray();

        Tag[] tags = doc.inlineTags();

        if (!EdenUtils.isEmpty(tags)) {

            for (Tag tag : tags) {
                JavadocInlineTagHandler handler = null;

                for (JavadocInlineTagHandler tagHandler : inlineTagHandlers) {
                    if (("@" + tagHandler.getName()).equalsIgnoreCase(tag.kind())) {
                        handler = tagHandler;
                        break;
                    }
                }

                JSONObject result = new JSONObject();

                if (handler != null) {
                    result.put("kind", handler.getName());
                    result.put("value", handler.processTag(tag).getElement());
                }
                else {
                    result.put("kind", tag.kind().replaceAll("@", "").toLowerCase());
                    result.put("value", tag.text());
                }

                array.put(result);
            }
        }

        return (array.length() > 0) ? array : null;
    }

    private JSONObject getBlockTags(Doc doc) {
        JSONObject object = new JSONObject();

        for (JavadocBlockTagHandler tagHandler : blockTagHandlers) {
            Tag[] tags = doc.tags(tagHandler.getName());

            if (!EdenUtils.isEmpty(tags)) {
                JSONElement el = tagHandler.processTags(tags);

                if (el != null) {
                    object.put(tagHandler.getName(), el.getElement());
                }
            }
        }

        return (object.length() > 0) ? object : null;
    }
}
