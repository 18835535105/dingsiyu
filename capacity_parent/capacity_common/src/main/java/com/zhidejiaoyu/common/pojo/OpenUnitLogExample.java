package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OpenUnitLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OpenUnitLogExample() {
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

        public Criteria andStudentIdIsNull() {
            addCriterion("student_id is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNotNull() {
            addCriterion("student_id is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdEqualTo(Long value) {
            addCriterion("student_id =", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotEqualTo(Long value) {
            addCriterion("student_id <>", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThan(Long value) {
            addCriterion("student_id >", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id >=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThan(Long value) {
            addCriterion("student_id <", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThanOrEqualTo(Long value) {
            addCriterion("student_id <=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdIn(List<Long> values) {
            addCriterion("student_id in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotIn(List<Long> values) {
            addCriterion("student_id not in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdBetween(Long value1, Long value2) {
            addCriterion("student_id between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotBetween(Long value1, Long value2) {
            addCriterion("student_id not between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdIsNull() {
            addCriterion("current_unit_id is null");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdIsNotNull() {
            addCriterion("current_unit_id is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdEqualTo(Long value) {
            addCriterion("current_unit_id =", value, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdNotEqualTo(Long value) {
            addCriterion("current_unit_id <>", value, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdGreaterThan(Long value) {
            addCriterion("current_unit_id >", value, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdGreaterThanOrEqualTo(Long value) {
            addCriterion("current_unit_id >=", value, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdLessThan(Long value) {
            addCriterion("current_unit_id <", value, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdLessThanOrEqualTo(Long value) {
            addCriterion("current_unit_id <=", value, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdIn(List<Long> values) {
            addCriterion("current_unit_id in", values, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdNotIn(List<Long> values) {
            addCriterion("current_unit_id not in", values, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdBetween(Long value1, Long value2) {
            addCriterion("current_unit_id between", value1, value2, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andCurrentUnitIdNotBetween(Long value1, Long value2) {
            addCriterion("current_unit_id not between", value1, value2, "currentUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdIsNull() {
            addCriterion("next_unit_id is null");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdIsNotNull() {
            addCriterion("next_unit_id is not null");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdEqualTo(Long value) {
            addCriterion("next_unit_id =", value, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdNotEqualTo(Long value) {
            addCriterion("next_unit_id <>", value, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdGreaterThan(Long value) {
            addCriterion("next_unit_id >", value, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdGreaterThanOrEqualTo(Long value) {
            addCriterion("next_unit_id >=", value, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdLessThan(Long value) {
            addCriterion("next_unit_id <", value, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdLessThanOrEqualTo(Long value) {
            addCriterion("next_unit_id <=", value, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdIn(List<Long> values) {
            addCriterion("next_unit_id in", values, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdNotIn(List<Long> values) {
            addCriterion("next_unit_id not in", values, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdBetween(Long value1, Long value2) {
            addCriterion("next_unit_id between", value1, value2, "nextUnitId");
            return (Criteria) this;
        }

        public Criteria andNextUnitIdNotBetween(Long value1, Long value2) {
            addCriterion("next_unit_id not between", value1, value2, "nextUnitId");
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