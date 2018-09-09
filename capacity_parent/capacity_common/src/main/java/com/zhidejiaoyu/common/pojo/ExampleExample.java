package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExampleExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ExampleExample() {
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

        public Criteria andExampleEnglishIsNull() {
            addCriterion("example_english is null");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishIsNotNull() {
            addCriterion("example_english is not null");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishEqualTo(String value) {
            addCriterion("example_english =", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishNotEqualTo(String value) {
            addCriterion("example_english <>", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishGreaterThan(String value) {
            addCriterion("example_english >", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishGreaterThanOrEqualTo(String value) {
            addCriterion("example_english >=", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishLessThan(String value) {
            addCriterion("example_english <", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishLessThanOrEqualTo(String value) {
            addCriterion("example_english <=", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishLike(String value) {
            addCriterion("example_english like", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishNotLike(String value) {
            addCriterion("example_english not like", value, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishIn(List<String> values) {
            addCriterion("example_english in", values, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishNotIn(List<String> values) {
            addCriterion("example_english not in", values, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishBetween(String value1, String value2) {
            addCriterion("example_english between", value1, value2, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleEnglishNotBetween(String value1, String value2) {
            addCriterion("example_english not between", value1, value2, "exampleEnglish");
            return (Criteria) this;
        }

        public Criteria andExampleChineseIsNull() {
            addCriterion("example_chinese is null");
            return (Criteria) this;
        }

        public Criteria andExampleChineseIsNotNull() {
            addCriterion("example_chinese is not null");
            return (Criteria) this;
        }

        public Criteria andExampleChineseEqualTo(String value) {
            addCriterion("example_chinese =", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseNotEqualTo(String value) {
            addCriterion("example_chinese <>", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseGreaterThan(String value) {
            addCriterion("example_chinese >", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseGreaterThanOrEqualTo(String value) {
            addCriterion("example_chinese >=", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseLessThan(String value) {
            addCriterion("example_chinese <", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseLessThanOrEqualTo(String value) {
            addCriterion("example_chinese <=", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseLike(String value) {
            addCriterion("example_chinese like", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseNotLike(String value) {
            addCriterion("example_chinese not like", value, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseIn(List<String> values) {
            addCriterion("example_chinese in", values, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseNotIn(List<String> values) {
            addCriterion("example_chinese not in", values, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseBetween(String value1, String value2) {
            addCriterion("example_chinese between", value1, value2, "exampleChinese");
            return (Criteria) this;
        }

        public Criteria andExampleChineseNotBetween(String value1, String value2) {
            addCriterion("example_chinese not between", value1, value2, "exampleChinese");
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

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemIsNull() {
            addCriterion("update_tiem is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemIsNotNull() {
            addCriterion("update_tiem is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemEqualTo(Date value) {
            addCriterion("update_tiem =", value, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemNotEqualTo(Date value) {
            addCriterion("update_tiem <>", value, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemGreaterThan(Date value) {
            addCriterion("update_tiem >", value, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemGreaterThanOrEqualTo(Date value) {
            addCriterion("update_tiem >=", value, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemLessThan(Date value) {
            addCriterion("update_tiem <", value, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemLessThanOrEqualTo(Date value) {
            addCriterion("update_tiem <=", value, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemIn(List<Date> values) {
            addCriterion("update_tiem in", values, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemNotIn(List<Date> values) {
            addCriterion("update_tiem not in", values, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemBetween(Date value1, Date value2) {
            addCriterion("update_tiem between", value1, value2, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andUpdateTiemNotBetween(Date value1, Date value2) {
            addCriterion("update_tiem not between", value1, value2, "updateTiem");
            return (Criteria) this;
        }

        public Criteria andCourseUnitIsNull() {
            addCriterion("course_unit is null");
            return (Criteria) this;
        }

        public Criteria andCourseUnitIsNotNull() {
            addCriterion("course_unit is not null");
            return (Criteria) this;
        }

        public Criteria andCourseUnitEqualTo(String value) {
            addCriterion("course_unit =", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitNotEqualTo(String value) {
            addCriterion("course_unit <>", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitGreaterThan(String value) {
            addCriterion("course_unit >", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitGreaterThanOrEqualTo(String value) {
            addCriterion("course_unit >=", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitLessThan(String value) {
            addCriterion("course_unit <", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitLessThanOrEqualTo(String value) {
            addCriterion("course_unit <=", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitLike(String value) {
            addCriterion("course_unit like", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitNotLike(String value) {
            addCriterion("course_unit not like", value, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitIn(List<String> values) {
            addCriterion("course_unit in", values, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitNotIn(List<String> values) {
            addCriterion("course_unit not in", values, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitBetween(String value1, String value2) {
            addCriterion("course_unit between", value1, value2, "courseUnit");
            return (Criteria) this;
        }

        public Criteria andCourseUnitNotBetween(String value1, String value2) {
            addCriterion("course_unit not between", value1, value2, "courseUnit");
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