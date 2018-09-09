package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class MedalExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MedalExample() {
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

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andParentNameIsNull() {
            addCriterion("parent_name is null");
            return (Criteria) this;
        }

        public Criteria andParentNameIsNotNull() {
            addCriterion("parent_name is not null");
            return (Criteria) this;
        }

        public Criteria andParentNameEqualTo(String value) {
            addCriterion("parent_name =", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameNotEqualTo(String value) {
            addCriterion("parent_name <>", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameGreaterThan(String value) {
            addCriterion("parent_name >", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameGreaterThanOrEqualTo(String value) {
            addCriterion("parent_name >=", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameLessThan(String value) {
            addCriterion("parent_name <", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameLessThanOrEqualTo(String value) {
            addCriterion("parent_name <=", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameLike(String value) {
            addCriterion("parent_name like", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameNotLike(String value) {
            addCriterion("parent_name not like", value, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameIn(List<String> values) {
            addCriterion("parent_name in", values, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameNotIn(List<String> values) {
            addCriterion("parent_name not in", values, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameBetween(String value1, String value2) {
            addCriterion("parent_name between", value1, value2, "parentName");
            return (Criteria) this;
        }

        public Criteria andParentNameNotBetween(String value1, String value2) {
            addCriterion("parent_name not between", value1, value2, "parentName");
            return (Criteria) this;
        }

        public Criteria andNextParentIsNull() {
            addCriterion("next_parent is null");
            return (Criteria) this;
        }

        public Criteria andNextParentIsNotNull() {
            addCriterion("next_parent is not null");
            return (Criteria) this;
        }

        public Criteria andNextParentEqualTo(Long value) {
            addCriterion("next_parent =", value, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentNotEqualTo(Long value) {
            addCriterion("next_parent <>", value, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentGreaterThan(Long value) {
            addCriterion("next_parent >", value, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentGreaterThanOrEqualTo(Long value) {
            addCriterion("next_parent >=", value, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentLessThan(Long value) {
            addCriterion("next_parent <", value, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentLessThanOrEqualTo(Long value) {
            addCriterion("next_parent <=", value, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentIn(List<Long> values) {
            addCriterion("next_parent in", values, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentNotIn(List<Long> values) {
            addCriterion("next_parent not in", values, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentBetween(Long value1, Long value2) {
            addCriterion("next_parent between", value1, value2, "nextParent");
            return (Criteria) this;
        }

        public Criteria andNextParentNotBetween(Long value1, Long value2) {
            addCriterion("next_parent not between", value1, value2, "nextParent");
            return (Criteria) this;
        }

        public Criteria andChildNameIsNull() {
            addCriterion("child_name is null");
            return (Criteria) this;
        }

        public Criteria andChildNameIsNotNull() {
            addCriterion("child_name is not null");
            return (Criteria) this;
        }

        public Criteria andChildNameEqualTo(String value) {
            addCriterion("child_name =", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameNotEqualTo(String value) {
            addCriterion("child_name <>", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameGreaterThan(String value) {
            addCriterion("child_name >", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameGreaterThanOrEqualTo(String value) {
            addCriterion("child_name >=", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameLessThan(String value) {
            addCriterion("child_name <", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameLessThanOrEqualTo(String value) {
            addCriterion("child_name <=", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameLike(String value) {
            addCriterion("child_name like", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameNotLike(String value) {
            addCriterion("child_name not like", value, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameIn(List<String> values) {
            addCriterion("child_name in", values, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameNotIn(List<String> values) {
            addCriterion("child_name not in", values, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameBetween(String value1, String value2) {
            addCriterion("child_name between", value1, value2, "childName");
            return (Criteria) this;
        }

        public Criteria andChildNameNotBetween(String value1, String value2) {
            addCriterion("child_name not between", value1, value2, "childName");
            return (Criteria) this;
        }

        public Criteria andNextChildIsNull() {
            addCriterion("next_child is null");
            return (Criteria) this;
        }

        public Criteria andNextChildIsNotNull() {
            addCriterion("next_child is not null");
            return (Criteria) this;
        }

        public Criteria andNextChildEqualTo(Long value) {
            addCriterion("next_child =", value, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildNotEqualTo(Long value) {
            addCriterion("next_child <>", value, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildGreaterThan(Long value) {
            addCriterion("next_child >", value, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildGreaterThanOrEqualTo(Long value) {
            addCriterion("next_child >=", value, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildLessThan(Long value) {
            addCriterion("next_child <", value, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildLessThanOrEqualTo(Long value) {
            addCriterion("next_child <=", value, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildIn(List<Long> values) {
            addCriterion("next_child in", values, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildNotIn(List<Long> values) {
            addCriterion("next_child not in", values, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildBetween(Long value1, Long value2) {
            addCriterion("next_child between", value1, value2, "nextChild");
            return (Criteria) this;
        }

        public Criteria andNextChildNotBetween(Long value1, Long value2) {
            addCriterion("next_child not between", value1, value2, "nextChild");
            return (Criteria) this;
        }

        public Criteria andExplainIsNull() {
            addCriterion("explain is null");
            return (Criteria) this;
        }

        public Criteria andExplainIsNotNull() {
            addCriterion("explain is not null");
            return (Criteria) this;
        }

        public Criteria andExplainEqualTo(String value) {
            addCriterion("explain =", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotEqualTo(String value) {
            addCriterion("explain <>", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThan(String value) {
            addCriterion("explain >", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThanOrEqualTo(String value) {
            addCriterion("explain >=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThan(String value) {
            addCriterion("explain <", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThanOrEqualTo(String value) {
            addCriterion("explain <=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLike(String value) {
            addCriterion("explain like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotLike(String value) {
            addCriterion("explain not like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainIn(List<String> values) {
            addCriterion("explain in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotIn(List<String> values) {
            addCriterion("explain not in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainBetween(String value1, String value2) {
            addCriterion("explain between", value1, value2, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotBetween(String value1, String value2) {
            addCriterion("explain not between", value1, value2, "explain");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsIsNull() {
            addCriterion("marked_words is null");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsIsNotNull() {
            addCriterion("marked_words is not null");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsEqualTo(String value) {
            addCriterion("marked_words =", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsNotEqualTo(String value) {
            addCriterion("marked_words <>", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsGreaterThan(String value) {
            addCriterion("marked_words >", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsGreaterThanOrEqualTo(String value) {
            addCriterion("marked_words >=", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsLessThan(String value) {
            addCriterion("marked_words <", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsLessThanOrEqualTo(String value) {
            addCriterion("marked_words <=", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsLike(String value) {
            addCriterion("marked_words like", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsNotLike(String value) {
            addCriterion("marked_words not like", value, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsIn(List<String> values) {
            addCriterion("marked_words in", values, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsNotIn(List<String> values) {
            addCriterion("marked_words not in", values, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsBetween(String value1, String value2) {
            addCriterion("marked_words between", value1, value2, "markedWords");
            return (Criteria) this;
        }

        public Criteria andMarkedWordsNotBetween(String value1, String value2) {
            addCriterion("marked_words not between", value1, value2, "markedWords");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlIsNull() {
            addCriterion("child_img_url is null");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlIsNotNull() {
            addCriterion("child_img_url is not null");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlEqualTo(String value) {
            addCriterion("child_img_url =", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlNotEqualTo(String value) {
            addCriterion("child_img_url <>", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlGreaterThan(String value) {
            addCriterion("child_img_url >", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlGreaterThanOrEqualTo(String value) {
            addCriterion("child_img_url >=", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlLessThan(String value) {
            addCriterion("child_img_url <", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlLessThanOrEqualTo(String value) {
            addCriterion("child_img_url <=", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlLike(String value) {
            addCriterion("child_img_url like", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlNotLike(String value) {
            addCriterion("child_img_url not like", value, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlIn(List<String> values) {
            addCriterion("child_img_url in", values, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlNotIn(List<String> values) {
            addCriterion("child_img_url not in", values, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlBetween(String value1, String value2) {
            addCriterion("child_img_url between", value1, value2, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andChildImgUrlNotBetween(String value1, String value2) {
            addCriterion("child_img_url not between", value1, value2, "childImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlIsNull() {
            addCriterion("parent_img_url is null");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlIsNotNull() {
            addCriterion("parent_img_url is not null");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlEqualTo(String value) {
            addCriterion("parent_img_url =", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlNotEqualTo(String value) {
            addCriterion("parent_img_url <>", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlGreaterThan(String value) {
            addCriterion("parent_img_url >", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlGreaterThanOrEqualTo(String value) {
            addCriterion("parent_img_url >=", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlLessThan(String value) {
            addCriterion("parent_img_url <", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlLessThanOrEqualTo(String value) {
            addCriterion("parent_img_url <=", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlLike(String value) {
            addCriterion("parent_img_url like", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlNotLike(String value) {
            addCriterion("parent_img_url not like", value, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlIn(List<String> values) {
            addCriterion("parent_img_url in", values, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlNotIn(List<String> values) {
            addCriterion("parent_img_url not in", values, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlBetween(String value1, String value2) {
            addCriterion("parent_img_url between", value1, value2, "parentImgUrl");
            return (Criteria) this;
        }

        public Criteria andParentImgUrlNotBetween(String value1, String value2) {
            addCriterion("parent_img_url not between", value1, value2, "parentImgUrl");
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