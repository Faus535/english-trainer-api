package com.faus535.englishtrainer.vocabulary.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "vocab_entries")
public class VocabEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String en;
    private String ipa;
    private String es;
    private String type;
    private String example;
    private String level;

    protected VocabEntry() {}

    public VocabEntry(String en, String ipa, String es, String type, String example, String level) {
        this.en = en;
        this.ipa = ipa;
        this.es = es;
        this.type = type;
        this.example = example;
        this.level = level;
    }

    public UUID getId() { return id; }
    public String getEn() { return en; }
    public String getIpa() { return ipa; }
    public String getEs() { return es; }
    public String getType() { return type; }
    public String getExample() { return example; }
    public String getLevel() { return level; }
}
