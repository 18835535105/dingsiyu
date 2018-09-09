package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class LevelExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LevelExample() {
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

        public Criteria andLevelNameIsNull() {
            addCriterion("level_name is null");
            return (Criteria) this;
        }

        public Criteria andLevelNameIsNotNull() {
            addCriterion("level_name is not null");
            return (Criteria) this;
        }

        public Criteria andLevelNameEqualTo(String value) {
            addCriterion("level_name =", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameNotEqualTo(String value) {
            addCriterion("level_name <>", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameGreaterThan(String value) {
            addCriterion("level_name >", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameGreaterThanOrEqualTo(String value) {
            addCriterion("level_name >=", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameLessThan(String value) {
            addCriterion("level_name <", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameLessThanOrEqualTo(String value) {
            addCriterion("level_name <=", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameLike(String value) {
            addCriterion("level_name like", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameNotLike(String value) {
            addCriterion("level_name not like", value, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameIn(List<String> values) {
            addCriterion("level_name in", values, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameNotIn(List<String> values) {
            addCriterion("level_name not in", values, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameBetween(String value1, String value2) {
            addCriterion("level_name between", value1, value2, "levelName");
            return (Criteria) this;
        }

        public Criteria andLevelNameNotBetween(String value1, String value2) {
            addCriterion("level_name not between", value1, value2, "levelName");
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

        public Criteria andNextIdIsNull() {
            addCriterion("next_id is null");
            return (Criteria) this;
        }

        public Criteria andNextIdIsNotNull() {
            addCriterion("next_id is not null");
            return (Criteria) this;
        }

        public Criteria andNextIdEqualTo(Long value) {
            addCriterion("next_id =", value, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdNotEqualTo(Long value) {
            addCriterion("next_id <>", value, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdGreaterThan(Long value) {
            addCriterion("next_id >", value, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdGreaterThanOrEqualTo(Long value) {
            addCriterion("next_id >=", value, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdLessThan(Long value) {
            addCriterion("next_id <", value, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdLessThanOrEqualTo(Long value) {
            addCriterion("next_id <=", value, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdIn(List<Long> values) {
            addCriterion("next_id in", values, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdNotIn(List<Long> values) {
            addCriterion("next_id not in", values, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdBetween(Long value1, Long value2) {
            addCriterion("next_id between", value1, value2, "nextId");
            return (Criteria) this;
        }

        public Criteria andNextIdNotBetween(Long value1, Long value2) {
            addCriterion("next_id not between", value1, value2, "nextId");
            return (Criteria) this;
        }

        public Criteria andGoldIsNull() {
            addCriterion("gold is null");
            return (Criteria) this;
        }

        public Criteria andGoldIsNotNull() {
            addCriterion("gold is not null");
            return (Criteria) this;
        }

        public Criteria andGoldEqualTo(Integer value) {
            addCriterion("gold =", value, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldNotEqualTo(Integer value) {
            addCriterion("gold <>", value, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldGreaterThan(Integer value) {
            addCriterion("gold >", value, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldGreaterThanOrEqualTo(Integer value) {
            addCriterion("gold >=", value, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldLessThan(Integer value) {
            addCriterion("gold <", value, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldLessThanOrEqualTo(Integer value) {
            addCriterion("gold <=", value, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldIn(List<Integer> values) {
            addCriterion("gold in", values, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldNotIn(List<Integer> values) {
            addCriterion("gold not in", values, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldBetween(Integer value1, Integer value2) {
            addCriterion("gold between", value1, value2, "gold");
            return (Criteria) this;
        }

        public Criteria andGoldNotBetween(Integer value1, Integer value2) {
            addCriterion("gold not between", value1, value2, "gold");
            return (Criteria) this;
        }

        public Criteria andImgUrlIsNull() {
            addCriterion("img_url is null");
            return (Criteria) this;
        }

        public Criteria andImgUrlIsNotNull() {
            addCriterion("img_url is not null");
            return (Criteria) this;
        }

        public Criteria andImgUrlEqualTo(String value) {
            addCriterion("img_url =", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlNotEqualTo(String value) {
            addCriterion("img_url <>", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlGreaterThan(String value) {
            addCriterion("img_url >", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlGreaterThanOrEqualTo(String value) {
            addCriterion("img_url >=", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlLessThan(String value) {
            addCriterion("img_url <", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlLessThanOrEqualTo(String value) {
            addCriterion("img_url <=", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlLike(String value) {
            addCriterion("img_url like", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlNotLike(String value) {
            addCriterion("img_url not like", value, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlIn(List<String> values) {
            addCriterion("img_url in", values, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlNotIn(List<String> values) {
            addCriterion("img_url not in", values, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlBetween(String value1, String value2) {
            addCriterion("img_url between", value1, value2, "imgUrl");
            return (Criteria) this;
        }

        public Criteria andImgUrlNotBetween(String value1, String value2) {
            addCriterion("img_url not between", value1, value2, "imgUrl");
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