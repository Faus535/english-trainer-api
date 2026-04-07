package com.faus535.englishtrainer.article.domain;

public final class ArticleParagraph {

    private final ArticleParagraphId id;
    private final ArticleReadingId articleReadingId;
    private final String content;
    private final int orderIndex;
    private final ArticleSpeaker speaker;

    private ArticleParagraph(ArticleParagraphId id, ArticleReadingId articleReadingId,
                              String content, int orderIndex, ArticleSpeaker speaker) {
        this.id = id;
        this.articleReadingId = articleReadingId;
        this.content = content;
        this.orderIndex = orderIndex;
        this.speaker = speaker;
    }

    public static ArticleParagraph create(ArticleReadingId articleReadingId, String content,
                                           int orderIndex, ArticleSpeaker speaker) {
        return new ArticleParagraph(ArticleParagraphId.generate(), articleReadingId, content, orderIndex, speaker);
    }

    public static ArticleParagraph reconstitute(ArticleParagraphId id, ArticleReadingId articleReadingId,
                                                 String content, int orderIndex, ArticleSpeaker speaker) {
        return new ArticleParagraph(id, articleReadingId, content, orderIndex, speaker);
    }

    public ArticleParagraphId id() { return id; }
    public ArticleReadingId articleReadingId() { return articleReadingId; }
    public String content() { return content; }
    public int orderIndex() { return orderIndex; }
    public ArticleSpeaker speaker() { return speaker; }
}
