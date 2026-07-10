package com.skillforge.dto;

import com.skillforge.model.ResumeKeyword;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeKeywordAnalysisResponse {

    private List<KeywordItem> detectedKeywords;
    private List<KeywordItem> missingKeywords;
    private List<KeywordItem> recommendedKeywords;
    private List<KeywordItem> importantKeywords;

    @Getter
    @Setter
    public static class KeywordItem {
        private Long id;
        private String keywordText;
        private String keywordType;
        private Integer priorityRank;

        public static KeywordItem from(ResumeKeyword keyword) {
            KeywordItem item = new KeywordItem();
            item.setId(keyword.getId());
            item.setKeywordText(keyword.getKeywordText());
            item.setKeywordType(keyword.getKeywordType());
            item.setPriorityRank(keyword.getPriorityRank());
            return item;
        }
    }
}