package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class StudentFlowExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public StudentFlowExample() {
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

        public Criteria andCurrentFlowIdIsNull() {
            addCriterion("current_flow_id is null");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdIsNotNull() {
            addCriterion("current_flow_id is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdEqualTo(Long value) {
            addCriterion("current_flow_id =", value, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdNotEqualTo(Long value) {
            addCriterion("current_flow_id <>", value, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdGreaterThan(Long value) {
            addCriterion("current_flow_id >", value, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdGreaterThanOrEqualTo(Long value) {
            addCriterion("current_flow_id >=", value, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdLessThan(Long value) {
            addCriterion("current_flow_id <", value, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdLessThanOrEqualTo(Long value) {
            addCriterion("current_flow_id <=", value, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdIn(List<Long> values) {
            addCriterion("current_flow_id in", values, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdNotIn(List<Long> values) {
            addCriterion("current_flow_id not in", values, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdBetween(Long value1, Long value2) {
            addCriterion("current_flow_id between", value1, value2, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowIdNotBetween(Long value1, Long value2) {
            addCriterion("current_flow_id not between", value1, value2, "currentFlowId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdIsNull() {
            addCriterion("current_flow_max_id is null");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdIsNotNull() {
            addCriterion("current_flow_max_id is not null");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdEqualTo(Long value) {
            addCriterion("current_flow_max_id =", value, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdNotEqualTo(Long value) {
            addCriterion("current_flow_max_id <>", value, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdGreaterThan(Long value) {
            addCriterion("current_flow_max_id >", value, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdGreaterThanOrEqualTo(Long value) {
            addCriterion("current_flow_max_id >=", value, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdLessThan(Long value) {
            addCriterion("current_flow_max_id <", value, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdLessThanOrEqualTo(Long value) {
            addCriterion("current_flow_max_id <=", value, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdIn(List<Long> values) {
            addCriterion("current_flow_max_id in", values, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdNotIn(List<Long> values) {
            addCriterion("current_flow_max_id not in", values, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdBetween(Long value1, Long value2) {
            addCriterion("current_flow_max_id between", value1, value2, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andCurrentFlowMaxIdNotBetween(Long value1, Long value2) {
            addCriterion("current_flow_max_id not between", value1, value2, "currentFlowMaxId");
            return (Criteria) this;
        }

        public Criteria andTimeMachineIsNull() {
            addCriterion("time_machine is null");
            return (Criteria) this;
        }

        public Criteria andTimeMachineIsNotNull() {
            addCriterion("time_machine is not null");
            return (Criteria) this;
        }

        public Criteria andTimeMachineEqualTo(Integer value) {
            addCriterion("time_machine =", value, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineNotEqualTo(Integer value) {
            addCriterion("time_machine <>", value, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineGreaterThan(Integer value) {
            addCriterion("time_machine >", value, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineGreaterThanOrEqualTo(Integer value) {
            addCriterion("time_machine >=", value, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineLessThan(Integer value) {
            addCriterion("time_machine <", value, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineLessThanOrEqualTo(Integer value) {
            addCriterion("time_machine <=", value, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineIn(List<Integer> values) {
            addCriterion("time_machine in", values, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineNotIn(List<Integer> values) {
            addCriterion("time_machine not in", values, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineBetween(Integer value1, Integer value2) {
            addCriterion("time_machine between", value1, value2, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andTimeMachineNotBetween(Integer value1, Integer value2) {
            addCriterion("time_machine not between", value1, value2, "timeMachine");
            return (Criteria) this;
        }

        public Criteria andPresentFlowIsNull() {
            addCriterion("present_flow is null");
            return (Criteria) this;
        }

        public Criteria andPresentFlowIsNotNull() {
            addCriterion("present_flow is not null");
            return (Criteria) this;
        }

        public Criteria andPresentFlowEqualTo(Integer value) {
            addCriterion("present_flow =", value, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowNotEqualTo(Integer value) {
            addCriterion("present_flow <>", value, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowGreaterThan(Integer value) {
            addCriterion("present_flow >", value, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowGreaterThanOrEqualTo(Integer value) {
            addCriterion("present_flow >=", value, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowLessThan(Integer value) {
            addCriterion("present_flow <", value, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowLessThanOrEqualTo(Integer value) {
            addCriterion("present_flow <=", value, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowIn(List<Integer> values) {
            addCriterion("present_flow in", values, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowNotIn(List<Integer> values) {
            addCriterion("present_flow not in", values, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowBetween(Integer value1, Integer value2) {
            addCriterion("present_flow between", value1, value2, "presentFlow");
            return (Criteria) this;
        }

        public Criteria andPresentFlowNotBetween(Integer value1, Integer value2) {
            addCriterion("present_flow not between", value1, value2, "presentFlow");
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