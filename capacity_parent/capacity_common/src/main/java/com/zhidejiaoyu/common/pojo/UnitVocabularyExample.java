package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class UnitVocabularyExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UnitVocabularyExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andUnitIdIsNull() {
            addCriterion("unit_id is null");
            return (Criteria) this;
        }

        public Criteria andUnitIdIsNotNull() {
            addCriterion("unit_id is not null");
            return (Criteria) this;
        }

        public Criteria andUnitIdEqualTo(Long value) {
            addCriterion("unit_id =", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotEqualTo(Long value) {
            addCriterion("unit_id <>", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdGreaterThan(Long value) {
            addCriterion("unit_id >", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdGreaterThanOrEqualTo(Long value) {
            addCriterion("unit_id >=", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdLessThan(Long value) {
            addCriterion("unit_id <", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdLessThanOrEqualTo(Long value) {
            addCriterion("unit_id <=", value, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdIn(List<Long> values) {
            addCriterion("unit_id in", values, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotIn(List<Long> values) {
            addCriterion("unit_id not in", values, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdBetween(Long value1, Long value2) {
            addCriterion("unit_id between", value1, value2, "unitId");
            return (Criteria) this;
        }

        public Criteria andUnitIdNotBetween(Long value1, Long value2) {
            addCriterion("unit_id not between", value1, value2, "unitId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdIsNull() {
            addCriterion("vocabulary_id is null");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdIsNotNull() {
            addCriterion("vocabulary_id is not null");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdEqualTo(Long value) {
            addCriterion("vocabulary_id =", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotEqualTo(Long value) {
            addCriterion("vocabulary_id <>", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdGreaterThan(Long value) {
            addCriterion("vocabulary_id >", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdGreaterThanOrEqualTo(Long value) {
            addCriterion("vocabulary_id >=", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdLessThan(Long value) {
            addCriterion("vocabulary_id <", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdLessThanOrEqualTo(Long value) {
            addCriterion("vocabulary_id <=", value, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdIn(List<Long> values) {
            addCriterion("vocabulary_id in", values, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotIn(List<Long> values) {
            addCriterion("vocabulary_id not in", values, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdBetween(Long value1, Long value2) {
            addCriterion("vocabulary_id between", value1, value2, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andVocabularyIdNotBetween(Long value1, Long value2) {
            addCriterion("vocabulary_id not between", value1, value2, "vocabularyId");
            return (Criteria) this;
        }

        public Criteria andClassifyIsNull() {
            addCriterion("classify is null");
            return (Criteria) this;
        }

        public Criteria andClassifyIsNotNull() {
            addCriterion("classify is not null");
            return (Criteria) this;
        }

        public Criteria andClassifyEqualTo(Integer value) {
            addCriterion("classify =", value, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyNotEqualTo(Integer value) {
            addCriterion("classify <>", value, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyGreaterThan(Integer value) {
            addCriterion("classify >", value, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyGreaterThanOrEqualTo(Integer value) {
            addCriterion("classify >=", value, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyLessThan(Integer value) {
            addCriterion("classify <", value, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyLessThanOrEqualTo(Integer value) {
            addCriterion("classify <=", value, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyIn(List<Integer> values) {
            addCriterion("classify in", values, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyNotIn(List<Integer> values) {
            addCriterion("classify not in", values, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyBetween(Integer value1, Integer value2) {
            addCriterion("classify between", value1, value2, "classify");
            return (Criteria) this;
        }

        public Criteria andClassifyNotBetween(Integer value1, Integer value2) {
            addCriterion("classify not between", value1, value2, "classify");
            return (Criteria) this;
        }

        public Criteria andWordChineseIsNull() {
            addCriterion("word_chinese is null");
            return (Criteria) this;
        }

        public Criteria andWordChineseIsNotNull() {
            addCriterion("word_chinese is not null");
            return (Criteria) this;
        }

        public Criteria andWordChineseEqualTo(String value) {
            addCriterion("word_chinese =", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotEqualTo(String value) {
            addCriterion("word_chinese <>", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseGreaterThan(String value) {
            addCriterion("word_chinese >", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseGreaterThanOrEqualTo(String value) {
            addCriterion("word_chinese >=", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseLessThan(String value) {
            addCriterion("word_chinese <", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseLessThanOrEqualTo(String value) {
            addCriterion("word_chinese <=", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseLike(String value) {
            addCriterion("word_chinese like", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotLike(String value) {
            addCriterion("word_chinese not like", value, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseIn(List<String> values) {
            addCriterion("word_chinese in", values, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotIn(List<String> values) {
            addCriterion("word_chinese not in", values, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseBetween(String value1, String value2) {
            addCriterion("word_chinese between", value1, value2, "wordChinese");
            return (Criteria) this;
        }

        public Criteria andWordChineseNotBetween(String value1, String value2) {
            addCriterion("word_chinese not between", value1, value2, "wordChinese");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}