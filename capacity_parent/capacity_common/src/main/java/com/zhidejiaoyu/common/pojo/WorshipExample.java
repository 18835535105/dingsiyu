package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorshipExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public WorshipExample() {
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

        public Criteria andStudentIdWorshipIsNull() {
            addCriterion("student_id_worship is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipIsNotNull() {
            addCriterion("student_id_worship is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipEqualTo(Long value) {
            addCriterion("student_id_worship =", value, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipNotEqualTo(Long value) {
            addCriterion("student_id_worship <>", value, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipGreaterThan(Long value) {
            addCriterion("student_id_worship >", value, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id_worship >=", value, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipLessThan(Long value) {
            addCriterion("student_id_worship <", value, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipLessThanOrEqualTo(Long value) {
            addCriterion("student_id_worship <=", value, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipIn(List<Long> values) {
            addCriterion("student_id_worship in", values, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipNotIn(List<Long> values) {
            addCriterion("student_id_worship not in", values, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipBetween(Long value1, Long value2) {
            addCriterion("student_id_worship between", value1, value2, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdWorshipNotBetween(Long value1, Long value2) {
            addCriterion("student_id_worship not between", value1, value2, "studentIdWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipIsNull() {
            addCriterion("student_id_by_worship is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipIsNotNull() {
            addCriterion("student_id_by_worship is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipEqualTo(Long value) {
            addCriterion("student_id_by_worship =", value, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipNotEqualTo(Long value) {
            addCriterion("student_id_by_worship <>", value, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipGreaterThan(Long value) {
            addCriterion("student_id_by_worship >", value, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id_by_worship >=", value, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipLessThan(Long value) {
            addCriterion("student_id_by_worship <", value, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipLessThanOrEqualTo(Long value) {
            addCriterion("student_id_by_worship <=", value, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipIn(List<Long> values) {
            addCriterion("student_id_by_worship in", values, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipNotIn(List<Long> values) {
            addCriterion("student_id_by_worship not in", values, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipBetween(Long value1, Long value2) {
            addCriterion("student_id_by_worship between", value1, value2, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andStudentIdByWorshipNotBetween(Long value1, Long value2) {
            addCriterion("student_id_by_worship not between", value1, value2, "studentIdByWorship");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeIsNull() {
            addCriterion("worship_time is null");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeIsNotNull() {
            addCriterion("worship_time is not null");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeEqualTo(Date value) {
            addCriterion("worship_time =", value, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeNotEqualTo(Date value) {
            addCriterion("worship_time <>", value, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeGreaterThan(Date value) {
            addCriterion("worship_time >", value, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("worship_time >=", value, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeLessThan(Date value) {
            addCriterion("worship_time <", value, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeLessThanOrEqualTo(Date value) {
            addCriterion("worship_time <=", value, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeIn(List<Date> values) {
            addCriterion("worship_time in", values, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeNotIn(List<Date> values) {
            addCriterion("worship_time not in", values, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeBetween(Date value1, Date value2) {
            addCriterion("worship_time between", value1, value2, "worshipTime");
            return (Criteria) this;
        }

        public Criteria andWorshipTimeNotBetween(Date value1, Date value2) {
            addCriterion("worship_time not between", value1, value2, "worshipTime");
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